package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@NativeContainer("data/program/primitive/number.mea")
public class NumberClassNative {
    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value) {
        try {
            return AddonUtils.optimalNumberValue(Double.parseDouble(value.getFinalValue().toString()));
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }

    @IsMatches
    public static boolean isMatches(Object value) {
        if (value == null) return false;
        return value instanceof Number || value instanceof NumberValue<?>;
    }
}
