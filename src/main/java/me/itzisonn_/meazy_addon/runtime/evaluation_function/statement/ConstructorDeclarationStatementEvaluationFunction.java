package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.impl.constructor.RuntimeConstructorValueImpl;

public class ConstructorDeclarationStatementEvaluationFunction extends AbstractEvaluationFunction<ConstructorDeclarationStatement> {
    public ConstructorDeclarationStatementEvaluationFunction() {
        super("constructor_declaration_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ConstructorDeclarationStatement constructorDeclarationStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
            throw new InvalidSyntaxException("Can't declare constructor in this environment");
        }

        for (Modifier modifier : constructorDeclarationStatement.getModifiers()) {
            if (!modifier.canUse(constructorDeclarationStatement, context, environment))
                throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
        }

        ConstructorValue runtimeConstructorValue = new RuntimeConstructorValueImpl(
                constructorDeclarationStatement.getParameters(),
                constructorDeclarationStatement.getBody(),
                constructorDeclarationEnvironment,
                constructorDeclarationStatement.getModifiers());

        constructorDeclarationEnvironment.declareConstructor(runtimeConstructorValue);
        return null;
    }
}
