package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidAccessException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.registry.RegistryEntry;

import java.util.List;
import java.util.stream.Collectors;

public class IdentifierEvaluationFunction extends AbstractEvaluationFunction<Identifier> {
    public IdentifierEvaluationFunction() {
        super("identifier");
    }

    @Override
    public RuntimeValue<?> evaluate(Identifier identifier, RuntimeContext context, Environment environment, Object... extra) {
        Environment requestEnvironment;
        if (extra.length == 0) requestEnvironment = environment;
        else if (extra[0] instanceof Environment env) requestEnvironment = env;
        else requestEnvironment = environment;

        if (identifier instanceof VariableIdentifier) {
            VariableDeclarationEnvironment variableDeclarationEnvironment = environment.getVariableDeclarationEnvironment(identifier.getId());
            if (variableDeclarationEnvironment == null) throw new InvalidIdentifierException("Variable with id " + identifier.getId() + " doesn't exist");

            VariableValue variableValue = variableDeclarationEnvironment.getVariable(identifier.getId());
            if (variableValue == null) throw new InvalidIdentifierException("Variable with id " + identifier.getId() + " doesn't exist");

            for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
                Modifier modifier = entry.getValue();
                boolean hasModifier = variableValue.getModifiers().contains(modifier);

                if (!modifier.canAccess(context, requestEnvironment, variableDeclarationEnvironment, identifier, hasModifier)) {
                    if (hasModifier) throw new InvalidAccessException("Can't access variable with id " + identifier.getId() + " because it has " + modifier.getId() + " modifier");
                    else throw new InvalidAccessException("Can't access variable with id " + identifier.getId() + " because it doesn't have " + modifier.getId() + " modifier");
                }
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
            if (functionDeclarationEnvironment == null) throw new InvalidIdentifierException("Function with id " + identifier.getId() + " doesn't exist");

            FunctionValue functionValue = functionDeclarationEnvironment.getFunction(identifier.getId(), args);
            if (functionValue == null) throw new InvalidIdentifierException("Function with id " + identifier.getId() + " doesn't exist");

            for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
                Modifier modifier = entry.getValue();
                boolean hasModifier = functionValue.getModifiers().contains(modifier);

                if (!modifier.canAccess(context, requestEnvironment, functionDeclarationEnvironment, identifier, hasModifier)) {
                    if (hasModifier) throw new InvalidAccessException("Can't access function with id " + identifier.getId() + " because it has " + modifier.getId() + " modifier");
                    else throw new InvalidAccessException("Can't access function with id " + identifier.getId() + " because it doesn't have " + modifier.getId() + " modifier");
                }
            }

            return functionValue;
        }

        if (identifier instanceof ClassIdentifier) {
            ClassValue classValue = environment.getFileEnvironment().getClass(identifier.getId());
            if (classValue == null) return evaluate(new VariableIdentifier(identifier.getId()), context, environment, extra);

            for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
                Modifier modifier = entry.getValue();
                boolean hasModifier = classValue.getModifiers().contains(modifier);

                if (!modifier.canAccess(context, requestEnvironment, classValue.getEnvironment().getParent(), identifier, hasModifier)) {
                    if (hasModifier) throw new InvalidAccessException("Can't access class with id " + identifier.getId() + " because it has " + modifier.getId() + " modifier");
                    else throw new InvalidAccessException("Can't access class with id " + identifier.getId() + " because it doesn't have " + modifier.getId() + " modifier");
                }
            }

            return classValue;
        }

        throw new InvalidIdentifierException("Invalid identifier " + identifier.getClass().getName());
    }
}
