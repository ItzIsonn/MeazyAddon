package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

public class NullLiteralEvaluationFunction extends AbstractEvaluationFunction<NullLiteral> {
    public NullLiteralEvaluationFunction() {
        super("null_literal");
    }

    @Override
    public RuntimeValue<?> evaluate(NullLiteral nullLiteral, RuntimeContext context, Environment environment, Object... extra) {
        return NullValue.INSTANCE;
    }
}
