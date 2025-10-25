package me.itzisonn_.meazy_addon.runtime.native_class;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.*;

import java.util.ArrayList;
import java.util.List;

@MeazyNativeClass("data/program/math.mea")
public class MathClassNative {
    public static LongValue round(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
        return new LongValue(Math.round(numberValue.getValue().doubleValue()));
    }

    public static LongValue floor(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't floor non-number value");
        return new LongValue((long) Math.floor(numberValue.getValue().doubleValue()));
    }

    public static LongValue ceil(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't ceil non-number value");
        return new LongValue((long) Math.ceil(numberValue.getValue().doubleValue()));
    }



    public static NumberValue<?> pow(RuntimeValue<?> number, RuntimeValue<?> power, FunctionEnvironment functionEnvironment) {
        if (!(number.getFinalRuntimeValue() instanceof NumberValue<?> numberValue) || !(power.getFinalRuntimeValue() instanceof NumberValue<?> degreeValue)) {
            throw new InvalidSyntaxException("Can't get power non-number values");
        }
        return AddonUtils.optimalNumberValue(Math.pow(numberValue.getValue().doubleValue(), degreeValue.getValue().doubleValue()));
    }

    public static NumberValue<?> abs(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> number)) throw new InvalidSyntaxException("Can't get abs of non-number value");
        return AddonUtils.optimalNumberValue(Math.abs(number.getValue().doubleValue()));
    }

    public static NumberValue<?> factorial(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        if (!(value.getFinalRuntimeValue() instanceof IntValue numberValue)) throw new InvalidSyntaxException("Can't get factorial of non-int value");
        int result = 1;
        for (int i = 1; i <= numberValue.getValue(); i++) {
            result = result * i;
        }
        return optimalIntegerValue(result);
    }



    public static NumberValue<?> min(RuntimeValue<?> a, RuntimeValue<?> b, FunctionEnvironment functionEnvironment) {
        if (!(a.getFinalRuntimeValue() instanceof NumberValue<?> aValue) || !(b.getFinalRuntimeValue() instanceof NumberValue<?> bValue)) {
            throw new InvalidSyntaxException("Can't get min of non-number values");
        }
        return AddonUtils.optimalNumberValue(Math.min(aValue.getValue().doubleValue(), bValue.getValue().doubleValue()));
    }

    public static NumberValue<?> max(RuntimeValue<?> a, RuntimeValue<?> b, FunctionEnvironment functionEnvironment) {
        if (!(a.getFinalRuntimeValue() instanceof NumberValue<?> aValue) || !(b.getFinalRuntimeValue() instanceof NumberValue<?> bValue)) {
            throw new InvalidSyntaxException("Can't get max of non-number values");
        }
        return AddonUtils.optimalNumberValue(Math.max(aValue.getValue().doubleValue(), bValue.getValue().doubleValue()));
    }



    public static ClassValue range(RuntimeValue<?> begin, RuntimeValue<?> end, RuntimeContext context, FunctionEnvironment functionEnvironment) {
        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");

        List<RuntimeValue<?>> list = range(beginValue.getValue(), endValue.getValue(), 1);
        return ListClassNative.newList(functionEnvironment, context, list);
    }

    public static ClassValue range(RuntimeValue<?> begin, RuntimeValue<?> end, RuntimeValue<?> step, RuntimeContext context, FunctionEnvironment functionEnvironment) {
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
