package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

public class NegationOperator extends Operator {
    public NegationOperator() {
        super("negation", "-", OperatorType.PREFIX);
    }

    @Override
    public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
        if (value1 instanceof NumberValue<?> numberValue) {
            return NumberValue.getOptimal(-numberValue.getValue().doubleValue());
        }
        return null;
    }
}
