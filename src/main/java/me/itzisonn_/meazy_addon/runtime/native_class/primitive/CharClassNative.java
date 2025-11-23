package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

@NativeContainer("data/program/primitive/char.mea")
public class CharClassNative {
    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        String stringValue = value.getFinalValue().toString();
        if (stringValue.length() == 1) return StringClassNative.newString(functionEnvironment.getFileEnvironment(), stringValue);
        return NullValue.INSTANCE;
    }

    @IsMatches
    public static boolean isMatches(Object value) {
        return switch (value) {
            case Character _ -> true;
            case StringClassNative.StringClassValue classValue -> classValue.getValue().length() == 1;
            default -> false;
        };
    }
}
