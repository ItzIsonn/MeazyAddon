package me.itzisonn_.meazy_addon.runtime.native_class;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.*;

import java.util.ArrayList;
import java.util.List;

@NativeContainer("data/program/math.mea")
public class MathClassNative {
    @Function
    public static LongValue round(@Argument RuntimeValue<?> value) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new RuntimeException("Can't round non-number value");
        return new LongValue(Math.round(numberValue.getValue().doubleValue()));
    }

    @Function
    public static LongValue floor(@Argument RuntimeValue<?> value) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new RuntimeException("Can't floor non-number value");
        return new LongValue((long) Math.floor(numberValue.getValue().doubleValue()));
    }

    @Function
    public static LongValue ceil(@Argument RuntimeValue<?> value) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new RuntimeException("Can't ceil non-number value");
        return new LongValue((long) Math.ceil(numberValue.getValue().doubleValue()));
    }



    @Function
    public static NumberValue<?> pow(@Argument RuntimeValue<?> number, @Argument RuntimeValue<?> power) {
        if (!(number.getFinalRuntimeValue() instanceof NumberValue<?> numberValue) || !(power.getFinalRuntimeValue() instanceof NumberValue<?> degreeValue)) {
            throw new RuntimeException("Can't get power non-number values");
        }
        return AddonUtils.optimalNumberValue(Math.pow(numberValue.getValue().doubleValue(), degreeValue.getValue().doubleValue()));
    }

    @Function
    public static NumberValue<?> abs(@Argument RuntimeValue<?> value) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> number)) throw new RuntimeException("Can't get abs of non-number value");
        return AddonUtils.optimalNumberValue(Math.abs(number.getValue().doubleValue()));
    }

    @Function
    public static NumberValue<?> factorial(@Argument RuntimeValue<?> value) {
        if (!(value.getFinalRuntimeValue() instanceof IntValue numberValue)) throw new RuntimeException("Can't get factorial of non-int value");
        int result = 1;
        for (int i = 1; i <= numberValue.getValue(); i++) {
            result = result * i;
        }
        return optimalIntegerValue(result);
    }



    @Function
    public static NumberValue<?> min(@Argument RuntimeValue<?> a, @Argument RuntimeValue<?> b) {
        if (!(a.getFinalRuntimeValue() instanceof NumberValue<?> aValue) || !(b.getFinalRuntimeValue() instanceof NumberValue<?> bValue)) {
            throw new RuntimeException("Can't get min of non-number values");
        }
        return AddonUtils.optimalNumberValue(Math.min(aValue.getValue().doubleValue(), bValue.getValue().doubleValue()));
    }

    @Function
    public static NumberValue<?> max(@Argument RuntimeValue<?> a, @Argument RuntimeValue<?> b) {
        if (!(a.getFinalRuntimeValue() instanceof NumberValue<?> aValue) || !(b.getFinalRuntimeValue() instanceof NumberValue<?> bValue)) {
            throw new RuntimeException("Can't get max of non-number values");
        }
        return AddonUtils.optimalNumberValue(Math.max(aValue.getValue().doubleValue(), bValue.getValue().doubleValue()));
    }



    @Function
    public static ClassValue range(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, RuntimeContext context, FunctionEnvironment functionEnvironment) {
        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");

        List<RuntimeValue<?>> list = range(beginValue.getValue(), endValue.getValue(), 1);
        return ListClassNative.newList(functionEnvironment, context, list);
    }

    @Function
    public static ClassValue range(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, @Argument RuntimeValue<?> step, RuntimeContext context, FunctionEnvironment functionEnvironment) {
        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");
        if (!(step.getFinalRuntimeValue() instanceof IntValue stepValue)) throw new InvalidArgumentException("Step must be int");

        if (stepValue.getValue() <= 0) throw new InvalidArgumentException("Step must be positive int");

        List<RuntimeValue<?>> list = range(beginValue.getValue(),  endValue.getValue(), stepValue.getValue());
        return ListClassNative.newList(functionEnvironment, context, list);
    }



    private static NumberValue<?> optimalIntegerValue(long value) {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return new IntValue((int) value);
        return new LongValue(value);
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
