package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;

public class OrOperator extends Operator {
    public OrOperator() {
        super("or", "||", OperatorType.INFIX);
    }

    @Override
    public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
        if (value1 instanceof BooleanValue booleanValue1 && value2 instanceof BooleanValue booleanValue2) {
            return BooleanValue.of(booleanValue1.getValue() || booleanValue2.getValue());
        }
        return null;
    }
}
