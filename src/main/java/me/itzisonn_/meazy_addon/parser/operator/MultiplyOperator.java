package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
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

        if (value1 instanceof StringClassNative.StringClassValue stringValue && value2 instanceof IntValue numberValue) {
            string = stringValue.getValue();
            amount = numberValue.getValue();
        }
        else if (value2 instanceof StringClassNative.StringClassValue stringValue && value1 instanceof IntValue numberValue) {
            string = stringValue.getValue();
            amount = numberValue.getValue();
        }
        else throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_multiply.values", value1.getClass().getName(), value2.getClass().getName()));

        if (amount < 0) throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_multiply.string_by_negative"));
        return StringClassNative.newString(environment, string.repeat(amount));
    }
}
