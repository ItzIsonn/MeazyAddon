package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;

@NativeContainer("data/program/primitive/any.mea")
public class AnyClassNative {
    @IsMatches
    public static boolean isMatches(Object value) {
        return value != null;
    }
}
