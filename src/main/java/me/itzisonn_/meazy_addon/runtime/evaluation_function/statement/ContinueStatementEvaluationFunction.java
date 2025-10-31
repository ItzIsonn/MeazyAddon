package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ContinueStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

public class ContinueStatementEvaluationFunction extends AbstractEvaluationFunction<ContinueStatement> {
    public ContinueStatementEvaluationFunction() {
        super("continue_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(ContinueStatement continueStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
            return null;
        }

        throw new RuntimeException("Can't use continue statement outside of for/while statements");
    }
}
