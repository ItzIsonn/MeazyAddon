package me.itzisonn_.meazy_addon.runtime.environment.default_classes.collections;

import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListClassEnvironment extends ClassEnvironmentImpl {
    public ListClassEnvironment(ClassDeclarationEnvironment parent) {
        this(parent, new ArrayList<>());
    }

    public ListClassEnvironment(ClassDeclarationEnvironment parent, List<RuntimeValue<?>> list) {
        super(parent, false, "List");

        declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerListValue(new ArrayList<>(list)),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        declareConstructor(new DefaultConstructorValue(List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                listValue.getValue().add(constructorArgs.getFirst().getFinalRuntimeValue());
            }
        });


        declareFunction(new DefaultFunctionValue("getSize", List.of(), new DataType("Int", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get size of non-list value");

                return new IntValue(listValue.getValue().size());
            }
        });


        declareFunction(new DefaultFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                listValue.getValue().add(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("add",
                List.of(new CallArgExpression("element", new DataType("Any", true), true), new CallArgExpression("pos", new DataType("Int", false), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't add element to non-int pos");
                listValue.getValue().add(intValue.getValue(), functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element from non-list value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't remove element from non-int pos");
                listValue.getValue().remove(intValue.getValue().intValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element to non-list value");

                listValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("get", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                new DataType("Any", true), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get element from non-list value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get element from non-int pos");
                return new RuntimeValue<>(listValue.getValue().get(intValue.getValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't use non-list value");

                return new BooleanValue(listValue.getValue().isEmpty());
            }
        });

        declareFunction(new DefaultFunctionValue("contains", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't use non-list value");

                return new BooleanValue(listValue.getValue().contains(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("toString", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't convert non-list value to string");

                return new StringValue(AddonUtils.unpackRuntimeValuesCollection(listValue.getValue()).toString());
            }
        });
    }

    public static class InnerListValue extends CollectionClassEnvironment.InnerCollectionValue<List<RuntimeValue<?>>> {
        private InnerListValue(List<RuntimeValue<?>> value) {
            super(value);
        }
    }
}