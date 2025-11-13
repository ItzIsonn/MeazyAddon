package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

public class PowerOperator extends Operator {
    public PowerOperator() {
        super("power", "^", OperatorType.INFIX);
    }

    @Override
    public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
        if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
            return NumberValue.getOptimal(Math.pow(numberValue1.getValue().doubleValue(), numberValue2.getValue().doubleValue()));
        }
        return null;
    }
}
