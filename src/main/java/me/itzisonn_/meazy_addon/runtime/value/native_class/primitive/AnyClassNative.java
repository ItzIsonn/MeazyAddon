package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

@MeazyNativeClass("data/program/primitive/any.mea")
public class AnyClassNative {
    public static boolean isMatches(Object value, ClassEnvironment classEnvironment) {
        return value != null;
    }
}
