package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@MeazyNativeClass("data/program/primitive/number.mea")
public class NumberClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        try {
            return AddonUtils.optimalNumberValue(Double.parseDouble(value.getFinalValue().toString()));
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        if (value == null) return false;
        return value instanceof Number || value instanceof NumberValue<?>;
    }
}
