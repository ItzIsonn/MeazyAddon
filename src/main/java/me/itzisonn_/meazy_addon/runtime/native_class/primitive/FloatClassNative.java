package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.FloatValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@MeazyNativeClass("data/program/primitive/float.mea")
public class FloatClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        try {
            return new FloatValue(Float.parseFloat(value.getFinalValue().toString()));
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        if (value == null) return false;
        if (value instanceof Float || value instanceof FloatValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= -Float.MAX_VALUE && doubleValue <= Float.MAX_VALUE;
    }
}
