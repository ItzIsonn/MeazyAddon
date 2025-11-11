package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;

public class BooleanLiteralEvaluationFunction extends AbstractEvaluationFunction<BooleanLiteral> {
    public BooleanLiteralEvaluationFunction() {
        super("boolean_literal");
    }

    @Override
    public RuntimeValue<?> evaluate(BooleanLiteral booleanLiteral, RuntimeContext context, Environment environment, Object... extra) {
        return BooleanValue.of(booleanLiteral.isValue());
    }
}
