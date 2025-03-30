package me.itzisonn_.meazy_addon.runtime.environment.default_classes.collections;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetClassEnvironment extends ClassEnvironmentImpl {
    public SetClassEnvironment(ClassDeclarationEnvironment parent) {
        this(parent, new HashSet<>());
    }

    public SetClassEnvironment(ClassDeclarationEnvironment parent, Set<RuntimeValue<?>> set) {
        super(parent, false, "Set");


        declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerSetValue(new HashSet<>(set)),
                false,
                Set.of(Modifiers.PRIVATE()),
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
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't add element to non-set value");

                setValue.getValue().add(constructorArgs.getFirst().getFinalRuntimeValue());
            }
        });


        declareFunction(new DefaultFunctionValue("getSize", List.of(), new DataType("Int", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't get size of non-set value");

                return new IntValue(setValue.getValue().size());
            }
        });


        declareFunction(new DefaultFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
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

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't remove element to non-set value");

                setValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't use non-set value");

                return new BooleanValue(setValue.getValue().isEmpty());
            }
        });

        declareFunction(new DefaultFunctionValue("contains", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't use non-set value");

                return new BooleanValue(setValue.getValue().contains(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("toString", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't convert non-set value to string");

                return new StringValue(Utils.unpackRuntimeValuesCollection(setValue.getValue()).toString());
            }
        });
    }

    public static class InnerSetValue extends CollectionClassEnvironment.InnerCollectionValue<Set<RuntimeValue<?>>> {
        private InnerSetValue(Set<RuntimeValue<?>> value) {
            super(value);
        }
    }
}