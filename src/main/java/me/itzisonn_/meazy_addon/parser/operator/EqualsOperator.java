package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

public class EqualsOperator extends Operator {
    public EqualsOperator() {
        super("equals", "==", OperatorType.INFIX);
    }

    @Override
    public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
        if (value1 instanceof NullValue) return BooleanValue.of(value2 instanceof NullValue);
        if (value1.getValue() == null) return BooleanValue.of(value1.equals(value2));
        return BooleanValue.of(value1.getValue().equals(value2.getValue()));
    }
}
