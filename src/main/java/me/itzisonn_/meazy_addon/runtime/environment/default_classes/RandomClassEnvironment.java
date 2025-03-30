package me.itzisonn_.meazy_addon.runtime.environment.default_classes;

import me.itzisonn_.meazy_addon.parser.Modifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.DoubleValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;
import me.itzisonn_.meazy_addon.runtime.value.number.NumberValue;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomClassEnvironment extends ClassEnvironmentImpl {
    public RandomClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Random");


        declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerRandomValue(new Random()),
                false,
                Set.of(Modifiers.PRIVATE()),
                false));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        declareConstructor(new DefaultConstructorValue(List.of(
                new CallArgExpression("seed", new DataType("Int", false), true)),
                this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't set seed to non-random value");

                if (!(constructorArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
                randomValue.getValue().setSeed(intValue.getValue());
            }
        });

        declareFunction(new DefaultFunctionValue("setSeed", List.of(
                new CallArgExpression("seed", new DataType("Int", false), true)),
                null, this, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't set seed to non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
                randomValue.getValue().setSeed(intValue.getValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("randomInt", List.of(
                new CallArgExpression("value", new DataType("Int", false), true)),
                new DataType("Int", false), this, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get random of non-int value");
                return new IntValue(randomValue.getValue().nextInt(intValue.getValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("randomInt", List.of(
                new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("Int", false), this, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidSyntaxException("Can't get random of non-int values");
                return new IntValue(randomValue.getValue().nextInt(beginValue.getValue(), endValue.getValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("randomFloat", List.of(), new DataType("Float", false), this, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                return new DoubleValue(randomValue.getValue().nextDouble());
            }
        });


        declareFunction(new DefaultFunctionValue("randomFloat", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Float", false), this, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't get random of non-number value");
                return new DoubleValue(randomValue.getValue().nextDouble(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("randomFloat", List.of(
                new CallArgExpression("begin", new DataType("Float", false), true), new CallArgExpression("end", new DataType("Float", false), true)),
                new DataType("Float", false), this, Set.of()) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue endValue)) throw new InvalidSyntaxException("Can't get random of non-number values");
                return new DoubleValue(randomValue.getValue().nextDouble(beginValue.getValue(), endValue.getValue()));
            }
        });
    }

    public static class InnerRandomValue extends RuntimeValue<Random> {
        private InnerRandomValue(Random value) {
            super(value);
        }
    }
}
