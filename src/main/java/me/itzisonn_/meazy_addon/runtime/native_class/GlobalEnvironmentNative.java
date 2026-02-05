package me.itzisonn_.meazy_addon.runtime.native_class;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

@NativeContainer("data/program/global.mea")
public final class GlobalEnvironmentNative {
    private GlobalEnvironmentNative() {}

    @Function
    public static void print(@Argument RuntimeValue<?> value, RuntimeContext context) {
        value = value.getFinalRuntimeValue();
        context.getInterpreter().getOutput().append(value);
        System.out.print(value);
    }

    @Function
    public static void println(@Argument RuntimeValue<?> value, RuntimeContext context) {
        value = value.getFinalRuntimeValue();
        context.getInterpreter().getOutput().append(value).append("\n");
        System.out.println(value);
    }
}