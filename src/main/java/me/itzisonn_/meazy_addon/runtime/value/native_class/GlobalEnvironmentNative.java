package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

@MeazyNativeClass("data/program/global.mea")
public class GlobalEnvironmentNative {
    public static void print(RuntimeValue<?> value, RuntimeContext context, FunctionEnvironment functionEnvironment) {
        value = value.getFinalRuntimeValue();
        context.getInterpreter().getOutput().append(value);
        System.out.print(value);
    }

    public static void println(RuntimeValue<?> value, RuntimeContext context, FunctionEnvironment functionEnvironment) {
        value = value.getFinalRuntimeValue();
        context.getInterpreter().getOutput().append(value).append("\n");
        System.out.println(value);
    }
}