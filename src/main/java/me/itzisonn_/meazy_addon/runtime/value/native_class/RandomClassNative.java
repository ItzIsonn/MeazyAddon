package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

import java.util.Random;

@MeazyNativeClass("datagen/random.mea")
public class RandomClassNative {
    public static InnerRandomValue getNativeRandom(FunctionEnvironment functionEnvironment) {
        return new InnerRandomValue(new Random());
    }

    public static void setSeed(RuntimeValue<?> seed, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(seed.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
        randomValue.getValue().setSeed(intValue.getValue());
    }

    public static IntValue randomInt(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(value.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get random of non-int value");
        return new IntValue(randomValue.getValue().nextInt(intValue.getValue()));
    }

    public static IntValue randomInt(RuntimeValue<?> begin, RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue) || !(end.getFinalRuntimeValue() instanceof IntValue endValue)) {
            throw new InvalidSyntaxException("Can't get random of non-int values");
        }
        return new IntValue(randomValue.getValue().nextInt(beginValue.getValue(), endValue.getValue()));
    }

    public static DoubleValue randomDouble(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        return new DoubleValue(randomValue.getValue().nextDouble());
    }

    public static DoubleValue randomDouble(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> rawRandom = functionEnvironment.getVariableDeclarationEnvironment("random").getVariable("random").getValue();
        if (!(rawRandom instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Invalid random value");

        if (!(value.getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't get random of non-number value");
        return new DoubleValue(randomValue.getValue().nextDouble(numberValue.getValue().doubleValue()));
    }

    public static DoubleValue randomDouble(RuntimeValue<?> begin, RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
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
