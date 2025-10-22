package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

@MeazyNativeClass("data/program/primitive/boolean.mea")
public class BooleanClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        return switch (value.getFinalValue().toString()) {
            case "0", "false" -> new BooleanValue(false);
            case "1", "true" -> new BooleanValue(true);
            default -> new NullValue();
        };
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        if (value == null) return false;
        if (value instanceof Boolean || value instanceof BooleanValue) return true;

        if (value instanceof RuntimeValue<?> runtimeValue) {
            return runtimeValue.getFinalRuntimeValue() instanceof BooleanValue;
        }
        return false;
    }
}
