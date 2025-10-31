package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

@NativeContainer("data/program/primitive/char.mea")
public class CharClassNative {
    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        String stringValue = value.getFinalValue().toString();
        if (stringValue.length() == 1) return StringClassNative.newString(functionEnvironment.getFileEnvironment(), stringValue);
        return new NullValue();
    }

    @IsMatches
    public static boolean isMatches(Object value) {
        return switch (value) {
            case Character _ -> true;
            case ClassValue classValue -> fromClassValue(classValue);
            case RuntimeValue<?> runtimeValue -> {
                if (runtimeValue.getFinalRuntimeValue() instanceof ClassValue classValue) {
                    yield fromClassValue(classValue);
                }
                yield false;
            }
            default -> false;
        };
    }

    private static boolean fromClassValue(ClassValue classValue) {
        ClassEnvironment classEnvironment = classValue.getEnvironment();
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        RuntimeValue<?> runtimeValue = classEnvironment.getVariable("value").getValue();
        if (!(runtimeValue instanceof StringClassNative.InnerStringValue stringValue)) throw new RuntimeException("Can't get data of non-string value");

        return stringValue.getValue().length() == 1;
    }
}
