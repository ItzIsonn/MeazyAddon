package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

@MeazyNativeClass("data/program/primitive/char.mea")
public class CharClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        String stringValue = value.getFinalValue().toString();
        if (stringValue.length() == 1) return new StringClassValue(functionEnvironment.getFileEnvironment(), stringValue);
        return new NullValue();
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        return switch (value) {
            case Character _ -> true;
            case StringClassValue stringClassValue -> stringClassValue.getValue().length() == 1;
            case RuntimeValue<?> runtimeValue -> {
                if (runtimeValue.getFinalRuntimeValue() instanceof StringClassValue stringClassValue) {
                    yield stringClassValue.getValue().length() == 1;
                }
                yield false;
            }
            default -> false;
        };
    }
}
