package me.itzisonn_.meazy_addon.runtime.value.native_class.collections;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

public class MapClassValue extends NativeClassValue {
    public MapClassValue(ClassDeclarationEnvironment parent) {
        this(parent, new HashMap<>());
    }

    public MapClassValue(Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        this(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), map);
    }

    public MapClassValue(ClassDeclarationEnvironment parent, Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        super(getClassEnvironment(parent, map));
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent, Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "Map");


        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerMapValue(new HashMap<>(map)),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't add pair to non-map value");

                mapValue.getValue().put(constructorArgs.getFirst().getFinalRuntimeValue(), constructorArgs.get(1).getFinalRuntimeValue());
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("getSize", List.of(), new DataType("Int", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get size of non-map value");

                return new IntValue(mapValue.getValue().size());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("put", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't add pair to non-map value");

                mapValue.getValue().put(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("putIfAbsent", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't add pair to non-map value");

                mapValue.getValue().putIfAbsent(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("remove", List.of(
                new CallArgExpression("key", new DataType("Any", true), true)),
                new DataType("Any", true), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't remove pair from non-map value");

                return mapValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("remove", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("value", new DataType("Any", true), true)),
                null, classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't remove pair from non-map value");

                mapValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
                return null;
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("get", List.of(
                new CallArgExpression("key", new DataType("Any", true), true)),
                new DataType("Any", true), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value from non-map value");

                return mapValue.getValue().get(functionArgs.getFirst().getFinalRuntimeValue());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getOrDefault", List.of(
                new CallArgExpression("key", new DataType("Any", true), true), new CallArgExpression("defaultValue", new DataType("Any", true), true)),
                new DataType("Any", true), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value from non-map value");

                return mapValue.getValue().getOrDefault(functionArgs.getFirst().getFinalRuntimeValue(), functionArgs.get(1).getFinalRuntimeValue());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("isEmpty", List.of(), new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

                return new BooleanValue(mapValue.getValue().isEmpty());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("containsKey", List.of(
                new CallArgExpression("key", new DataType("Any", true), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

                return new BooleanValue(mapValue.getValue().containsKey(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("containsValue", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

                return new BooleanValue(mapValue.getValue().containsValue(functionArgs.getFirst().getFinalRuntimeValue()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getKeySet", List.of(), new DataType("Set", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get key set of non-map value");

                return new SetClassValue(mapValue.getValue().keySet());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getValueList", List.of(), new DataType("List", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value list of non-map value");

                return new ListClassValue(new ArrayList<>(mapValue.getValue().values()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("toString", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't convert non-map value to string");

                return new StringClassValue(unpackRuntimeValuesMap(mapValue.getValue()).toString());
            }
        });

        return classEnvironment;
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