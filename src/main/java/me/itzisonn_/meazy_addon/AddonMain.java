package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.RuntimeFunctionValue;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy_addon.parser.AddonParsingFunctions;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy_addon.parser.json_converter.AddonConverters;
import me.itzisonn_.meazy_addon.runtime.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.environment.*;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;
import me.itzisonn_.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonMain extends Addon {
    public static final String NAMESPACE = "meazy_addon";

    @Override
    public void onInitialize() {
        AddonTokenTypes.INIT();
        AddonTokenTypeSets.INIT();
        AddonModifiers.INIT();
        AddonOperators.INIT();
        AddonParsingFunctions.INIT();
        AddonEvaluationFunctions.INIT();
        AddonConverters.INIT();

        Registries.PARSE_TOKENS_FUNCTION.register(getIdentifier("parse_tokens"), tokens -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null!");
            Parser.setTokens(tokens);

            Parser.moveOverOptionalNewLines();

            Map<String, String> requiredAddons = null;

            List<Statement> body = new ArrayList<>();
            while (!Parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                if (Parser.getCurrent().getType().equals(AddonTokenTypes.REQUIRE())) requiredAddons = AddonParsingFunctions.parseRequiredAddons();
                else body.add(Parser.parse(getIdentifier("global_statement"), Statement.class));
                Parser.moveOverOptionalNewLines();
            }

            if (requiredAddons == null) {
                requiredAddons = new HashMap<>();
                for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                    AddonInfo addonInfo = addon.getAddonInfo();
                    requiredAddons.put(addonInfo.getId(), addonInfo.getVersion());
                }
            }

            return new Program(MeazyMain.VERSION, requiredAddons, body);
        });

        Registries.EVALUATE_PROGRAM_FUNCTION.register(getIdentifier("evaluate_program"), program -> {
            for (String addonId : program.getRequiredAddons().keySet()) {
                Addon addon = MeazyMain.ADDON_MANAGER.getAddon(addonId);
                if (addon == null) throw new RuntimeException("Can't find required addon with id " + addonId);

                String addonVersion = program.getRequiredAddons().get(addonId);
                if (addonVersion != null && !addon.getAddonInfo().getVersion().equals(addonId)) {
                    throw new RuntimeException("Can't find required addon with id " + addonId + " of version " + addonVersion +
                            " (found version " + addon.getAddonInfo().getVersion() + ")");
                }
            }

            Interpreter.evaluate(program, Registries.GLOBAL_ENVIRONMENT.getEntry().getValue());

            RuntimeValue<?> runtimeValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getFunction("main", new ArrayList<>());
            if (runtimeValue == null) {
                MeazyMain.LOGGER.log(Level.WARN, "File doesn't contain main function");
                return;
            }

            if (!(runtimeValue instanceof RuntimeFunctionValue runtimeFunctionValue)) {
                MeazyMain.LOGGER.log(Level.WARN, "File contains invalid main function");
                return;
            }

            FunctionEnvironment functionEnvironment;
            try {
                functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(FunctionDeclarationEnvironment.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue());
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            if (!runtimeFunctionValue.getArgs().isEmpty()) throw new InvalidArgumentException("Main function must have no args");

            for (int i = 0; i < runtimeFunctionValue.getBody().size(); i++) {
                Statement statement = runtimeFunctionValue.getBody().get(i);
                if (statement instanceof ReturnStatement returnStatement) {
                    if (returnStatement.getValue() != null) {
                        throw new InvalidSyntaxException("Found return statement but function must return nothing");
                    }
                    if (i + 1 < runtimeFunctionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                    break;
                }

                RuntimeValue<?> value = Interpreter.evaluate(statement, functionEnvironment);
                if (value instanceof ReturnInfoValue returnInfoValue) {
                    if (returnInfoValue.getFinalValue() != null) {
                        throw new InvalidSyntaxException("Found return statement but function must return nothing");
                    }
                    break;
                }
            }
        });

        GlobalEnvironmentImpl globalEnvironment = new GlobalEnvironmentImpl();
        Registries.GLOBAL_ENVIRONMENT.register(getIdentifier("global_environment"), globalEnvironment);
        globalEnvironment.init();
        Registries.CLASS_ENVIRONMENT.register(getIdentifier("class_environment"), ClassEnvironmentImpl.class);
        Registries.FUNCTION_ENVIRONMENT.register(getIdentifier("function_environment"), FunctionEnvironmentImpl.class);
        Registries.CONSTRUCTOR_ENVIRONMENT.register(getIdentifier("constructor_environment"), ConstructorEnvironmentImpl.class);
        Registries.LOOP_ENVIRONMENT.register(getIdentifier("loop_environment"), LoopEnvironmentImpl.class);
        Registries.ENVIRONMENT.register(getIdentifier("environment"), EnvironmentImpl.class);



        getLogger().log(Level.INFO, "Successfully initialized!");
    }

    public static RegistryIdentifier getIdentifier(String id) {
        return RegistryIdentifier.of(NAMESPACE, id);
    }
}