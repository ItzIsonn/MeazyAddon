package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

@NativeContainer("data/program/primitive/boolean.mea")
public class BooleanClassNative {
    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value) {
        return switch (value.getFinalValue().toString()) {
            case "0", "false" -> new BooleanValue(false);
            case "1", "true" -> new BooleanValue(true);
            default -> new NullValue();
        };
    }

    @IsMatches
    public static boolean isMatches(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean || value instanceof BooleanValue) return true;

        if (value instanceof RuntimeValue<?> runtimeValue) {
            return runtimeValue.getFinalRuntimeValue() instanceof BooleanValue;
        }
        return false;
    }
}
