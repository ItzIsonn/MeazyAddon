package me.itzisonn_.meazy_addon.runtime.value.native_class.collections;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.constructor.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetClassValue extends NativeClassValue {
    private static GlobalEnvironment globalEnvironment = null;

    public SetClassValue(ClassDeclarationEnvironment parent) {
        this(parent, new HashSet<>());
    }

    public SetClassValue(Set<RuntimeValue<?>> set) {
        this(globalEnvironment, set);
    }

    public SetClassValue(ClassDeclarationEnvironment parent, Set<RuntimeValue<?>> set) {
        super(Set.of("Collection"), new ClassEnvironmentImpl(parent, false, "Set"));
        setupEnvironment(getEnvironment());
        getEnvironment().assignVariable("value", new InnerSetValue(new HashSet<>(set)));

        if (parent instanceof GlobalEnvironment globalEnv) globalEnvironment = globalEnv;
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerSetValue(new HashSet<>()),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false,
                classEnvironment));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't add element to non-set value");

                setValue.getValue().add(constructorArgs.getFirst().getFinalRuntimeValue());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getSize", List.of(), new DataType("Int", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't get size of non-set value");

                return new IntValue(setValue.getValue().size());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't add element to non-set value");

                RuntimeValue<?> itemValue = functionArgs.getFirst().getFinalRuntimeValue();
                if (setValue.getValue().contains(itemValue)) throw new InvalidSyntaxException("Set can't store identical items");

                setValue.getValue().add(itemValue);
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't remove element to non-set value");

                setValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't use non-set value");

                return new BooleanValue(setValue.getValue().isEmpty());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("contains", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't use non-set value");

                return new BooleanValue(setValue.getValue().contains(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("toString", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't convert non-set value to string");

                return new StringClassValue(AddonUtils.unpackRuntimeValuesCollection(setValue.getValue()).toString());
            }
        });
    }

    public static class InnerSetValue extends CollectionClassValue.InnerCollectionValue<Set<RuntimeValue<?>>> {
        private InnerSetValue(Set<RuntimeValue<?>> value) {
            super(value);
        }
    }
}