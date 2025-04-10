package me.itzisonn_.meazy_addon.runtime.value.native_class.collections;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CollectionClassValue extends NativeClassValue {
    public CollectionClassValue(ClassDeclarationEnvironment parent) {
        super(getClassEnvironment(parent));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "Collection", Set.of(AddonModifiers.ABSTRACT()));


        classEnvironment.declareFunction(new NativeFunctionValue("getSize", List.of(), new DataType("Int", false), classEnvironment, Set.of(AddonModifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, classEnvironment, Set.of(AddonModifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, classEnvironment, Set.of(AddonModifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), classEnvironment, Set.of(AddonModifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("contains", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                new DataType("Boolean", false), classEnvironment, Set.of(AddonModifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("toString", List.of(), new DataType("String", false), classEnvironment, Set.of(AddonModifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        return classEnvironment;
    }

    public static class InnerCollectionValue<T extends Collection<RuntimeValue<?>>> extends RuntimeValue<T> {
        protected InnerCollectionValue(T value) {
            super(value);
        }
    }
}