package me.itzisonn_.meazy_addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.RuntimeFunctionValue;
import me.itzisonn_.meazy_addon.lexer.TokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.TokenTypes;
import me.itzisonn_.meazy_addon.parser.Modifiers;
import me.itzisonn_.meazy_addon.parser.Operators;
import me.itzisonn_.meazy_addon.parser.ParsingFunctions;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy_addon.parser.json_converter.Converters;
import me.itzisonn_.meazy_addon.runtime.EvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.environment.*;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Main extends Addon {
    @Override
    public void onInitialize() {
        TokenTypes.INIT();
        TokenTypeSets.INIT();
        Modifiers.INIT();
        Operators.INIT();
        ParsingFunctions.INIT();
        EvaluationFunctions.INIT();
        Converters.INIT();

        Registries.EVALUATE_PROGRAM_FUNCTION.register(RegistryIdentifier.ofDefault("evaluate_program"), program -> {
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
        Registries.GLOBAL_ENVIRONMENT.register(RegistryIdentifier.ofDefault("global_environment"), globalEnvironment);
        globalEnvironment.init();
        Registries.CLASS_ENVIRONMENT.register(RegistryIdentifier.ofDefault("class_environment"), ClassEnvironmentImpl.class);
        Registries.FUNCTION_ENVIRONMENT.register(RegistryIdentifier.ofDefault("function_environment"), FunctionEnvironmentImpl.class);
        Registries.CONSTRUCTOR_ENVIRONMENT.register(RegistryIdentifier.ofDefault("constructor_environment"), ConstructorEnvironmentImpl.class);
        Registries.LOOP_ENVIRONMENT.register(RegistryIdentifier.ofDefault("loop_environment"), LoopEnvironmentImpl.class);
        Registries.ENVIRONMENT.register(RegistryIdentifier.ofDefault("environment"), EnvironmentImpl.class);



        getLogger().log(Level.INFO, "Successfully initialized!");
    }
}