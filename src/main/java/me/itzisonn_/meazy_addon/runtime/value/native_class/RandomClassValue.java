package me.itzisonn_.meazy_addon.runtime.value.native_class;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomClassValue extends NativeClassValue {
    public RandomClassValue(ClassDeclarationEnvironment parent) {
        super(getClassEnvironment(parent));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "Random");


        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerRandomValue(new Random()),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(
                new CallArgExpression("seed", new DataType("Int", false), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't set seed to non-random value");

                if (!(constructorArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
                randomValue.getValue().setSeed(intValue.getValue());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("setSeed", List.of(
                new CallArgExpression("seed", new DataType("Int", false), true)),
                null, classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't set seed to non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
                randomValue.getValue().setSeed(intValue.getValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("randomInt", List.of(
                new CallArgExpression("value", new DataType("Int", false), true)),
                new DataType("Int", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get random of non-int value");
                return new IntValue(randomValue.getValue().nextInt(intValue.getValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("randomInt", List.of(
                new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("Int", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidSyntaxException("Can't get random of non-int values");
                return new IntValue(randomValue.getValue().nextInt(beginValue.getValue(), endValue.getValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("randomFloat", List.of(), new DataType("Float", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                return new DoubleValue(randomValue.getValue().nextDouble());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("randomFloat", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Float", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't get random of non-number value");
                return new DoubleValue(randomValue.getValue().nextDouble(numberValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("randomFloat", List.of(
                new CallArgExpression("begin", new DataType("Float", false), true), new CallArgExpression("end", new DataType("Float", false), true)),
                new DataType("Float", false), classEnvironment, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue endValue)) throw new InvalidSyntaxException("Can't get random of non-number values");
                return new DoubleValue(randomValue.getValue().nextDouble(beginValue.getValue(), endValue.getValue()));
            }
        });
        
        return classEnvironment;
    }

    public static class InnerRandomValue extends RuntimeValue<Random> {
        private InnerRandomValue(Random value) {
            super(value);
        }
    }
}
