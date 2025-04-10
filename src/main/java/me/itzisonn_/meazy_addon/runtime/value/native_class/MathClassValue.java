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
import java.util.Set;

public class MathClassValue extends NativeClassValue {
    public MathClassValue(ClassDeclarationEnvironment parent) {
        super(getClassEnvironment(parent));
    }


    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "Math");


        classEnvironment.declareVariable(new VariableValue(
                "PI",
                new DataType("Float", false),
                new DoubleValue(Math.PI),
                true,
                Set.of(AddonModifiers.SHARED()),
                false));

        classEnvironment.declareVariable(new VariableValue(
                "E",
                new DataType("Float", false),
                new DoubleValue(Math.E),
                true,
                Set.of(AddonModifiers.SHARED()),
                false));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("round", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Int", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue))
                    throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.round(numberValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("floor", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Int", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue))
                    throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.floor(numberValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("ceil", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Int", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue))
                    throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.ceil(numberValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("pow", List.of(
                new CallArgExpression("number", new DataType("Float", false), true), new CallArgExpression("degree", new DataType("Float", false), true)),
                new DataType("Float", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> degreeValue))
                    throw new InvalidSyntaxException("Can't get power non-number values");
                return new DoubleValue(Math.pow(numberValue.getValue().doubleValue(), degreeValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("abs", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Float", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> number))
                    throw new InvalidSyntaxException("Can't get abs value of non-number value");
                return new DoubleValue(Math.abs(number.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("min", List.of(
                new CallArgExpression("value", new DataType("Float", false), true), new CallArgExpression("degree", new DataType("Float", false), true)),
                new DataType("Float", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> firstValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> secondValue))
                    throw new InvalidSyntaxException("Can't get min of non-number values");
                return new DoubleValue(Math.min(firstValue.getValue().doubleValue(), secondValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("max", List.of(
                new CallArgExpression("value", new DataType("Float", false), true), new CallArgExpression("degree", new DataType("Float", false), true)),
                new DataType("Float", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> firstValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> secondValue))
                    throw new InvalidSyntaxException("Can't get max of non-number values");
                return new DoubleValue(Math.max(firstValue.getValue().doubleValue(), secondValue.getValue().doubleValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("factorial", List.of(
                new CallArgExpression("value", new DataType("Int", false), true)),
                new DataType("Int", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue numberValue))
                    throw new InvalidSyntaxException("Can't get factorial of non-int value");
                int result = 1;
                for (int i = 1; i <= numberValue.getValue(); i++) {
                    result = result * i;
                }
                return new IntValue(result);
            }
        });
        
        return classEnvironment;
    }
}