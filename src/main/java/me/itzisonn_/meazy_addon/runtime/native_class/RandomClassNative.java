package me.itzisonn_.meazy_addon.runtime.native_class;

import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

import java.util.Random;

@NativeContainer("data/program/random.mea")
public class RandomClassNative {
    @Function
    public static InnerRandomValue getNativeRandom() {
        return new InnerRandomValue(new Random());
    }

    @Function
    public static void setSeed(@Argument RuntimeValue<?> seed, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(seed.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
        randomValue.getValue().setSeed(intValue.getValue());
    }

    @Function
    public static IntValue randomInt(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(value.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get random of non-int value");
        return new IntValue(randomValue.getValue().nextInt(intValue.getValue()));
    }

    @Function
    public static IntValue randomInt(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue) || !(end.getFinalRuntimeValue() instanceof IntValue endValue)) {
            throw new InvalidSyntaxException("Can't get random of non-int values");
        }
        return new IntValue(randomValue.getValue().nextInt(beginValue.getValue(), endValue.getValue()));
    }

    @Function
    public static DoubleValue randomDouble(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        return new DoubleValue(randomValue.getValue().nextDouble());
    }

    @Function
    public static DoubleValue randomDouble(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't get random of non-number value");
        return new DoubleValue(randomValue.getValue().nextDouble(numberValue.getValue().doubleValue()));
    }

    @Function
    public static DoubleValue randomDouble(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(begin.getFinalRuntimeValue() instanceof DoubleValue beginValue) || !(end.getFinalRuntimeValue() instanceof DoubleValue endValue)) {
            throw new InvalidSyntaxException("Can't get random of non-number values");
        }
        return new DoubleValue(randomValue.getValue().nextDouble(beginValue.getValue(), endValue.getValue()));
    }

    public static class InnerRandomValue extends RuntimeValueImpl<Random> {
        private InnerRandomValue(Random value) {
            super(value);
        }
    }
}
