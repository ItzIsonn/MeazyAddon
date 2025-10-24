package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.NullCheckExpression;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

public class NullCheckExpressionEvaluationFunction extends AbstractEvaluationFunction<NullCheckExpression> {
    public NullCheckExpressionEvaluationFunction() {
        super("null_check_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(NullCheckExpression nullCheckExpression, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();
        RuntimeValue<?> checkValue = interpreter.evaluate(nullCheckExpression.getCheckExpression(), environment).getFinalRuntimeValue();

        if (checkValue instanceof NullValue) {
            return interpreter.evaluate(nullCheckExpression.getNullExpression(), environment).getFinalRuntimeValue();
        }
        return checkValue;
    }
}
