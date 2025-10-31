package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.FloatValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.LongValue;

public class NumberLiteralEvaluationFunction extends AbstractEvaluationFunction<NumberLiteral> {
    public NumberLiteralEvaluationFunction() {
        super("number_literal");
    }

    @Override
    public RuntimeValue<?> evaluate(NumberLiteral numberLiteral, RuntimeContext context, Environment environment, Object... extra) {
        String value = numberLiteral.getValue();
        if (!value.contains(".")) {
            try {
                return new IntValue(Integer.parseInt(value));
            }
            catch (NumberFormatException ignore) {
                try {
                    return new LongValue(Long.parseLong(value));
                }
                catch (NumberFormatException ignore2) {
                    throw new RuntimeException("Number " + value + " is too big");
                }
            }
        }

        try {
            return new FloatValue(Float.parseFloat(value));
        }
        catch (NumberFormatException ignore) {
            try {
                return new DoubleValue(Double.parseDouble(value));
            }
            catch (NumberFormatException ignore2) {
                throw new RuntimeException("Number " + value + " is too big");
            }
        }
    }
}
