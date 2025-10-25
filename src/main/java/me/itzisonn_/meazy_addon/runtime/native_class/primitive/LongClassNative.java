package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.LongValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@MeazyNativeClass("data/program/primitive/long.mea")
public class LongClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        try {
            return new LongValue(Long.parseLong(value.getFinalValue().toString().replaceAll("\\.0$", "")));
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        if (value == null) return false;
        if (value instanceof Long || value instanceof LongValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= Long.MIN_VALUE && doubleValue <= Long.MAX_VALUE && doubleValue % 1 == 0;
    }
}