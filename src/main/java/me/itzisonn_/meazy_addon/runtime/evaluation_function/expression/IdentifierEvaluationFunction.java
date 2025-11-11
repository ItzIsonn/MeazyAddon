package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidAccessException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierEvaluationFunction extends AbstractEvaluationFunction<Identifier> {
    public IdentifierEvaluationFunction() {
        super("identifier");
    }

    @Override
    public RuntimeValue<?> evaluate(Identifier identifier, RuntimeContext context, Environment environment, Object... extra) {
        Environment callEnvironment;
        if (extra.length == 0) callEnvironment = environment;
        else if (extra[0] instanceof Environment env) callEnvironment = env;
        else callEnvironment = environment;

        if (identifier instanceof VariableIdentifier) {
            VariableDeclarationEnvironment variableDeclarationEnvironment = environment.getVariableDeclarationEnvironment(identifier.getId());
            if (variableDeclarationEnvironment == null) throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.variable.doesnt_exist", identifier.getId()));

            VariableValue variableValue = variableDeclarationEnvironment.getVariable(identifier.getId());
            if (variableValue == null) throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.variable.doesnt_exist", identifier.getId()));

            if (!variableValue.isAccessible(callEnvironment)) {
                throw new InvalidAccessException(Text.translatable("meazy_addon:runtime.variable.cant_access", identifier.getId()));
            }

            return variableValue;
        }

        if (identifier instanceof FunctionIdentifier) {
            if (extra.length == 1 || !(extra[1] instanceof List<?> rawArgs)) throw new RuntimeException("Invalid function args");

            List<RuntimeValue<?>> args = rawArgs.stream().map(object -> {
                if (object instanceof RuntimeValue<?> arg) return arg;
                throw new RuntimeException("Unknown error occurred. Probably used function has returned nothing");
            }).collect(Collectors.toList());

            FunctionDeclarationEnvironment functionDeclarationEnvironment = environment.getFunctionDeclarationEnvironment(identifier.getId(), args);
            if (functionDeclarationEnvironment == null) throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.function.doesnt_exist", identifier.getId()));

            FunctionValue functionValue = functionDeclarationEnvironment.getFunction(identifier.getId(), args);
            if (functionValue == null) throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.function.doesnt_exist", identifier.getId()));

            if (!functionValue.isAccessible(callEnvironment)) {
                throw new InvalidAccessException(Text.translatable("meazy_addon:runtime.function.cant_access", identifier.getId()));
            }

            return functionValue;
        }

        if (identifier instanceof ClassIdentifier) {
            ClassValue classValue = environment.getFileEnvironment().getClass(identifier.getId());
            if (classValue == null) throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.class.doesnt_exist", identifier.getId()));

            if (!classValue.isAccessible(callEnvironment)) {
                throw new InvalidAccessException(Text.translatable("meazy_addon:runtime.class.cant_access", identifier.getId()));
            }

            return classValue;
        }

        throw new InvalidIdentifierException(Text.translatable("meazy_addon:invalid_identifier", identifier.getClass().getName()));
    }
}
