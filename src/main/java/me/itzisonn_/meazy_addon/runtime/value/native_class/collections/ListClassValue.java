package me.itzisonn_.meazy_addon.runtime.value.native_class.collections;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListClassValue extends NativeClassValue {
    public ListClassValue(ClassDeclarationEnvironment parent) {
        this(parent, new ArrayList<>());
    }

    public ListClassValue(List<RuntimeValue<?>> list) {
        this(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list);
    }

    public ListClassValue(ClassDeclarationEnvironment parent, List<RuntimeValue<?>> list) {
        super(Set.of("Collection"), getClassEnvironment(parent, list));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent, List<RuntimeValue<?>> list) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "List");

        
        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerListValue(new ArrayList<>(list)),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


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
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                listValue.getValue().add(constructorArgs.getFirst().getFinalRuntimeValue());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getSize", List.of(), new DataType("Int", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get size of non-list value");

                return new IntValue(listValue.getValue().size());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                listValue.getValue().add(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("add",
                List.of(new CallArgExpression("element", new DataType("Any", true), true), new CallArgExpression("pos", new DataType("Int", false), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't add element to non-int pos");
                listValue.getValue().add(intValue.getValue(), functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("remove", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element from non-list value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't remove element from non-int pos");
                listValue.getValue().remove(intValue.getValue().intValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element to non-list value");

                listValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("get", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                new DataType("Any", true), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get element from non-list value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get element from non-int pos");
                return new RuntimeValue<>(listValue.getValue().get(intValue.getValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't use non-list value");

                return new BooleanValue(listValue.getValue().isEmpty());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("contains", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't use non-list value");

                return new BooleanValue(listValue.getValue().contains(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("toString", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't convert non-list value to string");

                return new StringClassValue(AddonUtils.unpackRuntimeValuesCollection(listValue.getValue()).toString());
            }
        });
        
        return classEnvironment;
    }

    public static class InnerListValue extends CollectionClassValue.InnerCollectionValue<List<RuntimeValue<?>>> {
        private InnerListValue(List<RuntimeValue<?>> value) {
            super(value);
        }
    }
}