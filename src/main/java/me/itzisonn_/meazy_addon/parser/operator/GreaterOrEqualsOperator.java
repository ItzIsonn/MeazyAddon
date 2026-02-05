package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

public class GreaterOrEqualsOperator extends Operator {
    public GreaterOrEqualsOperator() {
        super("greater_or_equals", ">=", OperatorType.INFIX);
    }

    @Override
    public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
        if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
            return BooleanValue.of(numberValue1.getValue().doubleValue() >= numberValue2.getValue().doubleValue());
        }
        if (value1 instanceof StringClassValue stringValue1 && value2 instanceof StringClassValue stringValue2) {
            return BooleanValue.of(stringValue1.getValue().length() >= stringValue2.getValue().length());
        }
        return null;
    }
}
