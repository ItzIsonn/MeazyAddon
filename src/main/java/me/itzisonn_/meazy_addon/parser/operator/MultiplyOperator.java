package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.UnsupportedOperatorException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

public class MultiplyOperator extends Operator {
    public MultiplyOperator() {
        super("multiply", "*", OperatorType.INFIX);
    }

    @Override
    public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
        if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
            return NumberValue.getOptimal(numberValue1.getValue().doubleValue() * numberValue2.getValue().doubleValue());
        }

        String string;
        int amount;

        if (value1 instanceof StringClassNative.InnerStringValue stringValue && value2 instanceof IntValue numberValue) {
            string = stringValue.getValue();
            amount = numberValue.getValue();
        }
        else if (value2 instanceof StringClassNative.InnerStringValue stringValue && value1 instanceof IntValue numberValue) {
            string = stringValue.getValue();
            amount = numberValue.getValue();
        }
        else throw new UnsupportedOperatorException("Can't multiply values " + value1 + " and " + value2);

        if (amount < 0) throw new UnsupportedOperatorException("Can't multiply string by a negative int");

        return StringClassNative.newString(environment, new StringBuilder().repeat(string, amount).toString());
    }
}
