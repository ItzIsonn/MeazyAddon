package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

public class ReturnStatementEvaluationFunction extends AbstractEvaluationFunction<ReturnStatement> {
    public ReturnStatementEvaluationFunction() {
        super("return_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ReturnStatement returnStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (environment instanceof FunctionEnvironment || environment.hasParent(parent -> parent instanceof FunctionEnvironment)) {
            if (returnStatement.getValue() == null) return null;
            return context.getInterpreter().evaluate(returnStatement.getValue(), environment);
        }

        if (returnStatement.getValue() == null) {
            return null;
        }

        throw new RuntimeException("Can't return value not inside a function");
    }
}
