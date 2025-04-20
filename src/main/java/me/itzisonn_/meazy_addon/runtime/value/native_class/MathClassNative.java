package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.number.*;

@MeazyNativeClass("datagen/math.mea")
public class MathClassNative {
    public static LongValue round(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
        return new LongValue(Math.round(numberValue.getValue().doubleValue()));
    }

    public static LongValue floor(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't floor non-number value");
        return new LongValue((long) Math.floor(numberValue.getValue().doubleValue()));
    }

    public static LongValue ceil(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't ceil non-number value");
        return new LongValue((long) Math.ceil(numberValue.getValue().doubleValue()));
    }

    public static NumberValue<?> pow(RuntimeValue<?> number, RuntimeValue<?> power, FunctionEnvironment functionEnvironment) {
        if (!(number.getFinalRuntimeValue() instanceof NumberValue<?> numberValue) || !(power.getFinalRuntimeValue() instanceof NumberValue<?> degreeValue)) {
            throw new InvalidSyntaxException("Can't get power non-number values");
        }
        return optimalNumberValue(Math.pow(numberValue.getValue().doubleValue(), degreeValue.getValue().doubleValue()));
    }

    public static NumberValue<?> abs(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> number)) throw new InvalidSyntaxException("Can't get abs of non-number value");
        return optimalNumberValue(Math.abs(number.getValue().doubleValue()));
    }

    public static NumberValue<?> factorial(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof IntValue numberValue)) throw new InvalidSyntaxException("Can't get factorial of non-int value");
        int result = 1;
        for (int i = 1; i <= numberValue.getValue(); i++) {
            result = result * i;
        }
        return optimalIntegerValue(result);
    }

    public static NumberValue<?> min(RuntimeValue<?> a, RuntimeValue<?> b, FunctionEnvironment functionEnvironment) {
        if (!(a.getFinalRuntimeValue() instanceof NumberValue<?> aValue) || !(b.getFinalRuntimeValue() instanceof NumberValue<?> bValue)) {
            throw new InvalidSyntaxException("Can't get min of non-number values");
        }
        return optimalNumberValue(Math.min(aValue.getValue().doubleValue(), bValue.getValue().doubleValue()));
    }

    public static NumberValue<?> max(RuntimeValue<?> a, RuntimeValue<?> b, FunctionEnvironment functionEnvironment) {
        if (!(a.getFinalRuntimeValue() instanceof NumberValue<?> aValue) || !(b.getFinalRuntimeValue() instanceof NumberValue<?> bValue)) {
            throw new InvalidSyntaxException("Can't get max of non-number values");
        }
        return optimalNumberValue(Math.max(aValue.getValue().doubleValue(), bValue.getValue().doubleValue()));
    }



    private static NumberValue<?> optimalNumberValue(double value) {
        if (value % 1 == 0) {
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return new IntValue((int) value);
            if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) return new LongValue((long) value);
        }
        else {
            if (value >= -Float.MAX_VALUE && value <= Float.MAX_VALUE) return new FloatValue((float) value);
            if (value >= -Double.MAX_VALUE && value <= Double.MAX_VALUE) return new DoubleValue(value);
        }
        throw new InvalidValueException("Resulted value " + value + " is out of bounds");
    }

    private static NumberValue<?> optimalIntegerValue(long value) {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return new IntValue((int) value);
        return new LongValue(value);
    }
}
