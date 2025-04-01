package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
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
import java.util.List;

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

        Registries.PARSE_TOKENS_FUNCTION.register(AddonMain.getIdentifier("parse_tokens"), tokens -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null!");
            Parser.setTokens(tokens);

            Parser.moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            while (!Parser.getCurrent().getType().equals(me.itzisonn_.meazy.lexer.TokenTypes.END_OF_FILE())) {
                body.add(Parser.parse(AddonMain.getIdentifier("global_statement"), Statement.class));
                Parser.moveOverOptionalNewLines();
            }

            return new Program(MeazyMain.VERSION, body);
        });

        Registries.EVALUATE_PROGRAM_FUNCTION.register(AddonMain.getIdentifier("evaluate_program"), program -> {
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
        Registries.GLOBAL_ENVIRONMENT.register(AddonMain.getIdentifier("global_environment"), globalEnvironment);
        globalEnvironment.init();
        Registries.CLASS_ENVIRONMENT.register(AddonMain.getIdentifier("class_environment"), ClassEnvironmentImpl.class);
        Registries.FUNCTION_ENVIRONMENT.register(AddonMain.getIdentifier("function_environment"), FunctionEnvironmentImpl.class);
        Registries.CONSTRUCTOR_ENVIRONMENT.register(AddonMain.getIdentifier("constructor_environment"), ConstructorEnvironmentImpl.class);
        Registries.LOOP_ENVIRONMENT.register(AddonMain.getIdentifier("loop_environment"), LoopEnvironmentImpl.class);
        Registries.ENVIRONMENT.register(AddonMain.getIdentifier("environment"), EnvironmentImpl.class);



        getLogger().log(Level.INFO, "Successfully initialized!");
    }

    public static RegistryIdentifier getIdentifier(String id) {
        return RegistryIdentifier.of(NAMESPACE, id);
    }
}