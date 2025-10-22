package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@MeazyNativeClass("data/program/primitive/int.mea")
public class IntClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        try {
            return new IntValue(Integer.parseInt(value.getFinalValue().toString().replaceAll("\\.0$", "")));
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        if (value == null) return false;
        if (value instanceof Integer || value instanceof IntValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= Integer.MIN_VALUE && doubleValue <= Integer.MAX_VALUE && doubleValue % 1 == 0;
    }
}
