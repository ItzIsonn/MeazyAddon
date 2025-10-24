package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.function.RuntimeFunctionValue;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy_addon.parser.pasing_function.AddonParsingFunctions;
import me.itzisonn_.meazy_addon.parser.ast.statement.ImportStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.UsingStatement;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeFactoryImpl;
import me.itzisonn_.meazy_addon.parser.json_converter.AddonConverters;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.environment.factory.*;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;
import me.itzisonn_.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;

import java.util.*;

public class AddonMain extends Addon {
    public static final String NAMESPACE = "meazy_addon";

    @Override
    public void onInitialize() {
        AddonModifiers.REGISTER();
        AddonOperators.REGISTER();
        AddonParsingFunctions.REGISTER();
        AddonEvaluationFunctions.REGISTER();
        AddonConverters.REGISTER();

        Registries.PARSE_TOKENS_FUNCTION.register(getIdentifier("parse_tokens"), (file, tokens) -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null");
            ParsingContext parsingContext = new ParsingContext(tokens);

            Parser parser = parsingContext.getParser();
            parser.moveOverOptionalNewLines();

            Map<String, Version> requiredAddons = null;
            List<Statement> body = new ArrayList<>();
            boolean isProgramHead = true;

            while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                if (parser.getCurrent().getType().equals(AddonTokenTypes.REQUIRE())) requiredAddons = ParsingHelper.parseRequiredAddons(parsingContext);
                else {
                    Statement statement = parser.parse(getIdentifier("global_statement"), Statement.class);
                    if (statement instanceof ImportStatement) {
                        if (!isProgramHead) throw new InvalidSyntaxException("Imports must be at file's beginning");
                    }
                    else if (statement instanceof UsingStatement) {
                        if (!isProgramHead) throw new InvalidSyntaxException("Using statements must be at file's beginning");
                    }
                    else isProgramHead = false;
                    body.add(statement);
                }

                parser.moveOverOptionalNewLines();
            }

            if (requiredAddons == null) {
                requiredAddons = new HashMap<>();
                for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                    AddonInfo addonInfo = addon.getAddonInfo();
                    requiredAddons.put(addonInfo.getId(), addonInfo.getVersion());
                }
            }

            return new Program(file, MeazyMain.VERSION, requiredAddons, body);
        });

        Registries.EVALUATE_PROGRAM_FUNCTION.register(getIdentifier("evaluate_program"), (program, globalEnvironment) -> {
            for (String addonId : program.getRequiredAddons().keySet()) {
                Addon addon = MeazyMain.ADDON_MANAGER.getAddon(addonId);
                if (addon == null) throw new RuntimeException("Can't find required addon with id " + addonId);

                Version addonVersion = program.getRequiredAddons().get(addonId);
                if (addonVersion != null && !addon.getAddonInfo().getVersion().equals(addonVersion)) {
                    throw new RuntimeException("Can't find required addon with id " + addonId + " of version " + addonVersion +
                            " (found version " + addon.getAddonInfo().getVersion() + ")");
                }
            }

            RuntimeContext context = globalEnvironment.getContext();
            Interpreter interpreter = context.getInterpreter();

            FileEnvironment fileEnvironment = Registries.FILE_ENVIRONMENT_FACTORY.getEntry().getValue().create(globalEnvironment, program.getFile());
            interpreter.evaluate(program, fileEnvironment);

            return fileEnvironment;
        });

        Registries.RUN_PROGRAM_FUNCTION.register(getIdentifier("run_program"), program -> {
            RuntimeContext context = new RuntimeContext();
            Interpreter interpreter = context.getInterpreter();

            GlobalEnvironment globalEnvironment = context.getGlobalEnvironment();
            FileEnvironment fileEnvironment = Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment);

            for (ClassValue classValue : fileEnvironment.getClasses()) {
                if (EvaluationHelper.hasRepeatedBaseClasses(classValue.getBaseClasses(), new ArrayList<>(), fileEnvironment)) {
                    throw new InvalidIdentifierException("Class with id " + classValue.getId() + " has repeated base classes");
                }
                if (EvaluationHelper.hasRepeatedVariables(
                        classValue.getBaseClasses(),
                        new ArrayList<>(classValue.getEnvironment().getVariables().stream().map(VariableValue::getId).toList()),
                        fileEnvironment)) {
                    throw new InvalidIdentifierException("Class with id " + classValue.getId() + " has repeated variables");
                }
            }

            RuntimeValue<?> runtimeValue = fileEnvironment.getFunction("main", new ArrayList<>());
            if (runtimeValue == null) {
                MeazyMain.LOGGER.log(Level.WARN, "File doesn't contain main function");
                return fileEnvironment;
            }

            if (!(runtimeValue instanceof RuntimeFunctionValue runtimeFunctionValue)) {
                MeazyMain.LOGGER.log(Level.WARN, "File contains invalid main function");
                return fileEnvironment;
            }
            if (!runtimeFunctionValue.getArgs().isEmpty()) throw new InvalidArgumentException("Main function must have no args");

            FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(fileEnvironment);

            for (int i = 0; i < runtimeFunctionValue.getBody().size(); i++) {
                Statement statement = runtimeFunctionValue.getBody().get(i);
                if (statement instanceof ReturnStatement returnStatement) {
                    if (returnStatement.getValue() != null) {
                        throw new InvalidSyntaxException("Found return statement but function must return nothing");
                    }
                    if (i + 1 < runtimeFunctionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                    break;
                }

                RuntimeValue<?> value = interpreter.evaluate(statement, functionEnvironment);
                if (value instanceof ReturnInfoValue returnInfoValue) {
                    if (returnInfoValue.getFinalValue() != null) {
                        throw new InvalidSyntaxException("Found return statement but function must return nothing");
                    }
                    break;
                }
            }

            return fileEnvironment;
        });

        Registries.DATA_TYPE_FACTORY.register(getIdentifier("data_type_factory"), new DataTypeFactoryImpl());
        Registries.GLOBAL_ENVIRONMENT_FACTORY.register(getIdentifier("global_environment_factory"), new GlobalEnvironmentFactoryImpl());
        Registries.FILE_ENVIRONMENT_FACTORY.register(getIdentifier("file_environment_factory"), new FileEnvironmentFactoryImpl());
        Registries.CLASS_ENVIRONMENT_FACTORY.register(getIdentifier("class_environment_factory"), new ClassEnvironmentFactoryImpl());
        Registries.FUNCTION_ENVIRONMENT_FACTORY.register(getIdentifier("function_environment_factory"), new FunctionEnvironmentFactoryImpl());
        Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.register(getIdentifier("constructor_environment_factory"), new ConstructorEnvironmentFactoryImpl());
        Registries.LOOP_ENVIRONMENT_FACTORY.register(getIdentifier("loop_environment_factory"), new LoopEnvironmentFactoryImpl());
        Registries.ENVIRONMENT_FACTORY.register(getIdentifier("environment_factory"), new EnvironmentFactoryImpl());

        getLogger().log(Level.INFO, "Successfully initialized");
    }

    public static RegistryIdentifier getIdentifier(String id) {
        return RegistryIdentifier.of(NAMESPACE, id);
    }
}