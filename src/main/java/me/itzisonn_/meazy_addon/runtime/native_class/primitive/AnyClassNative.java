package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

@NativeContainer("data/program/primitive/any.mea")
public class AnyClassNative {
    @Function
    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        return value != null;
    }
}
