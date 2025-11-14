package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

@NativeContainer("data/program/primitive/int.mea")
public class IntClassNative {
    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value) {
        try {
            return new IntValue(Integer.parseInt(value.getFinalValue().toString().replaceAll("\\.0$", "")));
        }
        catch (NumberFormatException ignore) {
            return NullValue.INSTANCE;
        }
    }

    @Function
    public static RuntimeValue<?> fromBase(@Argument RuntimeValue<?> value, @Argument RuntimeValue<?> base) {
        if (!(base.getFinalRuntimeValue() instanceof IntValue baseValue)) throw new RuntimeException("Base must be int");

        try {
            return new IntValue(Integer.parseInt(value.getFinalValue().toString(), baseValue.getValue()));
        }
        catch (NumberFormatException e) {
            return NullValue.INSTANCE;
        }
    }

    @Function
    public static RuntimeValue<?> toBase(@Argument RuntimeValue<?> value, @Argument RuntimeValue<?> base, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof IntValue intValue)) throw new RuntimeException("Value must be int");
        if (!(base.getFinalRuntimeValue() instanceof IntValue baseValue)) throw new RuntimeException("Base must be int");

        try {
            return StringClassNative.newString(functionEnvironment, Integer.toString(intValue.getValue(), baseValue.getValue()));
        }
        catch (NumberFormatException e) {
            return NullValue.INSTANCE;
        }
    }



    @IsMatches
    public static boolean isMatches(Object value) {
        if (value == null) return false;
        if (value instanceof Integer || value instanceof IntValue) return true;

        double doubleValue;
        if (value instanceof Number number) doubleValue = number.doubleValue();
        else if (value instanceof NumberValue<?> number) doubleValue = number.getValue().doubleValue();
        else return false;

        return doubleValue >= Integer.MIN_VALUE && doubleValue <= Integer.MAX_VALUE && doubleValue % 1 == 0;
    }
}
