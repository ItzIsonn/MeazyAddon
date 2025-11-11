package me.itzisonn_.meazy_addon.runtime.value.number;

import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

public abstract class NumberValue<T extends Number> extends RuntimeValueImpl<T> {
    protected NumberValue(T value) {
        super(value);
    }

    public static NumberValue<?> getOptimal(double value) {
        if (value % 1 == 0) {
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return new IntValue((int) value);
            if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) return new LongValue((long) value);
        }
        else {
            if (value >= -Float.MAX_VALUE && value <= Float.MAX_VALUE) return new FloatValue((float) value);
            if (value >= -Double.MAX_VALUE && value <= Double.MAX_VALUE) return new DoubleValue(value);
        }

        throw new InvalidValueException(Text.translatable("meazy_addon:runtime.value_out_of_bounds", value));
    }
}