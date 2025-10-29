package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@NativeContainer("data/program/primitive/double.mea")
public class DoubleClassNative {
    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value) {
        try {
            return new DoubleValue(Double.parseDouble(value.getFinalValue().toString()));
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }

    @IsMatches
    public static boolean isMatches(Object value) {
        if (value == null) return false;
        if (value instanceof Double || value instanceof DoubleValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= -Double.MAX_VALUE && doubleValue <= Double.MAX_VALUE;
    }
}
