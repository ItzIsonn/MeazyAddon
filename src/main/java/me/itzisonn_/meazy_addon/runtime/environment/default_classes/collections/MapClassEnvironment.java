package me.itzisonn_.meazy_addon.runtime.environment.default_classes.collections;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

public class MapClassEnvironment extends ClassEnvironmentImpl {
    public MapClassEnvironment(ClassDeclarationEnvironment parent) {
        this(parent, new HashMap<>());
    }

    public MapClassEnvironment(ClassDeclarationEnvironment parent, Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        super(parent, false, "Map");

        declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerMapValue(new HashMap<>(map)),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        declareConstructor(new DefaultConstructorValue(List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't add pair to non-map value");

                mapValue.getValue().put(constructorArgs.getFirst().getFinalRuntimeValue(), constructorArgs.get(1).getFinalRuntimeValue());
            }
        });


        declareFunction(new DefaultFunctionValue("getSize", List.of(), new DataType("Int", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get size of non-map value");

                return new IntValue(mapValue.getValue().size());
            }
        });

        declareFunction(new DefaultFunctionValue("put", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't add pair to non-map value");

                mapValue.getValue().put(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("putIfAbsent", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't add pair to non-map value");

                mapValue.getValue().putIfAbsent(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("key", new DataType("Any", true), true)),
                new DataType("Any", true), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't remove pair from non-map value");

                return mapValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't remove pair from non-map value");

                mapValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("get", List.of(
                new CallArgExpression("key", new DataType("Any", true), true)),
                new DataType("Any", true), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value from non-map value");

                return mapValue.getValue().get(functionArgs.getFirst().getFinalRuntimeValue());
            }
        });

        declareFunction(new DefaultFunctionValue("getOrDefault", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("defaultValue", new DataType("Any", true), true)),
                new DataType("Any", true), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value from non-map value");

                return mapValue.getValue().getOrDefault(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
            }
        });

        declareFunction(new DefaultFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

                return new BooleanValue(mapValue.getValue().isEmpty());
            }
        });

        declareFunction(new DefaultFunctionValue("containsKey", List.of(
                new CallArgExpression("key", new DataType("Any", true), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

                return new BooleanValue(mapValue.getValue().containsKey(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("containsValue", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

                return new BooleanValue(mapValue.getValue().containsValue(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("getKeySet", List.of(), new DataType("Set", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get key set of non-map value");

                return new DefaultClassValue(Set.of("Collection"), new SetClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), mapValue.getValue().keySet()));
            }
        });

        declareFunction(new DefaultFunctionValue("getValueList", List.of(), new DataType("List", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value list of non-map value");

                return new DefaultClassValue(Set.of("Collection"), new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), new ArrayList<>(mapValue.getValue().values())));
            }
        });

        declareFunction(new DefaultFunctionValue("toString", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't convert non-map value to string");

                return new StringValue(unpackRuntimeValuesMap(mapValue.getValue()).toString());
            }
        });
    }

    private static Map<Object, Object> unpackRuntimeValuesMap(Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        Map<Object, Object> unpackedMap = new HashMap<>();
        for (RuntimeValue<?> runtimeValue : map.keySet()) {
            unpackedMap.put(runtimeValue.getFinalValue(), map.get(runtimeValue).getFinalValue());
        }
        return unpackedMap;
    }

    public static class InnerMapValue extends RuntimeValue<Map<RuntimeValue<?>, RuntimeValue<?>>> {
        private InnerMapValue(Map<RuntimeValue<?>, RuntimeValue<?>> value) {
            super(value);
        }
    }
}