package me.itzisonn_.meazy_addon.runtime.environment.default_classes.collections;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CollectionClassEnvironment extends ClassEnvironmentImpl {
    public CollectionClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, false, "Collection", Set.of(Modifiers.ABSTRACT()));


        declareFunction(new DefaultFunctionValue("getSize", List.of(), new DataType("Int", false), this, Set.of(Modifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, Set.of(Modifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, Set.of(Modifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), this, Set.of(Modifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("contains", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                new DataType("Boolean", false), this, Set.of(Modifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("toString", List.of(), new DataType("String", false), this, Set.of(Modifiers.ABSTRACT())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return null;
            }
        });
    }

    public static class InnerCollectionValue<T extends Collection<RuntimeValue<?>>> extends RuntimeValue<T> {
        protected InnerCollectionValue(T value) {
            super(value);
        }
    }
}