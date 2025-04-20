package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.List;

@MeazyNativeClass("datagen/global.mea")
public class GlobalEnvironmentNative {
    public static void print(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        value = value.getFinalRuntimeValue();
        Interpreter.OUTPUT.append(value);
        System.out.print(value);
    }

    public static void println(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        value = value.getFinalRuntimeValue();
        Interpreter.OUTPUT.append(value).append("\n");
        System.out.println(value);
    }

    public static ClassValue range(RuntimeValue<?> begin, RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");

        List<RuntimeValue<?>> list = range(beginValue.getValue(), endValue.getValue(), 1);
        return ListClassNative.newList(functionEnvironment, list);
    }

    public static ClassValue range(RuntimeValue<?> begin, RuntimeValue<?> end, RuntimeValue<?> step, FunctionEnvironment functionEnvironment) {
        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");
        if (!(step.getFinalRuntimeValue() instanceof IntValue stepValue)) throw new InvalidArgumentException("Step must be int");

        if (stepValue.getValue() <= 0) throw new InvalidArgumentException("Step must be positive int");

        List<RuntimeValue<?>> list = range(beginValue.getValue(),  endValue.getValue(), stepValue.getValue());
        return ListClassNative.newList(functionEnvironment, list);
    }



    private static List<RuntimeValue<?>> range(int begin, int end, int step) {
        List<RuntimeValue<?>> list = new ArrayList<>();

        if (begin < end) {
            for (int i = begin; i < end; i += step) {
                list.add(new IntValue(i));
            }
        }
        else {
            for (int i = begin; i > end; i -= step) {
                list.add(new IntValue(i));
            }
        }

        return list;
    }
}