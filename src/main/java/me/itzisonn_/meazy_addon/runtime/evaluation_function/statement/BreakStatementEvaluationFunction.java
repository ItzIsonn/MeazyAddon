package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.BreakStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

public class BreakStatementEvaluationFunction extends AbstractEvaluationFunction<BreakStatement> {
    public BreakStatementEvaluationFunction() {
        super("break_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(BreakStatement breakStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
            return null;
        }

        throw new InvalidSyntaxException("Can't use break statement outside of for/while statements");
    }
}
