package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

@MeazyNativeClass("data/program/primitive/char.mea")
public class CharClassNative {
    public static RuntimeValue<?> valueOf(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        String stringValue = value.getFinalValue().toString();
        if (stringValue.length() == 1) return StringClassNative.newString(functionEnvironment.getFileEnvironment(), stringValue);
        return new NullValue();
    }

    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
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
        if (!(runtimeValue instanceof StringClassNative.InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

        return stringValue.getValue().length() == 1;
    }
}
