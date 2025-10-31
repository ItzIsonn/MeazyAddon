package me.itzisonn_.meazy_addon.runtime.native_class.collection;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.native_annotation.NewInstance;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

@NativeContainer("data/program/collection/list.mea")
public class MapClassNative {
    public static ClassValue newMap(Environment callEnvironment, RuntimeContext context, Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        ClassValue classValue = EvaluationHelper.callClassValue(context, callEnvironment.getFileEnvironment().getClass("Map"), callEnvironment, new ArrayList<>());

        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("map").getVariable("map").getValue() instanceof InnerMapValue mapValue)) {
            throw new RuntimeException("Can't create map from non-map value");
        }
        mapValue.getValue().putAll(map);

        return classValue;
    }

    @Function
    public static InnerMapValue getNativeMap() {
        return new InnerMapValue(new HashMap<>());
    }



    @Function
    public static IntValue getSize(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't get size of non-map value");

        return new IntValue(mapValue.getValue().size());
    }



    @Function
    public static void put(@Argument RuntimeValue<?> key, @Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't put pair to non-map value");

        mapValue.getValue().put(key.getFinalRuntimeValue(), value.getFinalRuntimeValue());
    }

    @Function
    public static void putIfAbsent(@Argument RuntimeValue<?> key, @Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't put pair to non-map value");

        mapValue.getValue().putIfAbsent(key.getFinalRuntimeValue(), value.getFinalRuntimeValue());
    }

    @Function
    public static RuntimeValue<?> remove(@Argument RuntimeValue<?> key, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't remove pair from non-map value");

        return mapValue.getValue().remove(key.getFinalRuntimeValue());
    }

    @Function
    public static BooleanValue remove(@Argument RuntimeValue<?> key, @Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't remove pair from non-map value");

        return new BooleanValue(mapValue.getValue().remove(key.getFinalRuntimeValue(), value.getFinalRuntimeValue()));
    }

    @Function
    public static RuntimeValue<?> get(@Argument RuntimeValue<?> key, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't get value from non-map value");

        return mapValue.getValue().get(key.getFinalRuntimeValue());
    }

    @Function
    public static RuntimeValue<?> getOrDefault(@Argument RuntimeValue<?> key, @Argument RuntimeValue<?> defaultValue, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't get value from non-map value");

        return mapValue.getValue().getOrDefault(key.getFinalRuntimeValue(), defaultValue.getFinalRuntimeValue());
    }



    @Function
    public static BooleanValue isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't use non-map value");

        return new BooleanValue(mapValue.getValue().isEmpty());
    }

    @Function
    public static BooleanValue containsKey(@Argument RuntimeValue<?> key, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't use non-map value");

        return new BooleanValue(mapValue.getValue().containsKey(key.getFinalRuntimeValue()));
    }

    @Function
    public static BooleanValue containsValue(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't use non-map value");

        return new BooleanValue(mapValue.getValue().containsValue(value.getFinalRuntimeValue()));
    }



    @Function
    public static ClassValue getKeySet(RuntimeContext context, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't get key set of non-map value");

        return SetClassNative.newSet(functionEnvironment, context, mapValue.getValue().keySet());
    }

    @Function
    public static ClassValue getValueList(RuntimeContext context, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't get value list of non-map value");

        return ListClassNative.newList(functionEnvironment, context, new ArrayList<>(mapValue.getValue().values()));
    }



    @NewInstance
    public static ClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        return new RuntimeClassValueImpl(baseClasses, classEnvironment, body) {
            @Override
            public String toString() {
                RuntimeValue<?> map = getEnvironment().getVariableDeclarationEnvironment("map").getVariable("map").getValue();
                if (!(map instanceof InnerMapValue mapValue)) throw new RuntimeException("Can't get string from non-map value");
                return unpackRuntimeValuesMap(mapValue.getValue()).toString();
            }
        };
    }

    
    
    private static Map<RuntimeValue<?>, RuntimeValue<?>> unpackRuntimeValuesMap(Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        Map<RuntimeValue<?>, RuntimeValue<?>> unpackedMap = new HashMap<>();
        for (RuntimeValue<?> runtimeValue : map.keySet()) {
            unpackedMap.put(runtimeValue.getFinalRuntimeValue(), map.get(runtimeValue).getFinalRuntimeValue());
        }
        return unpackedMap;
    }

    public static class InnerMapValue extends RuntimeValueImpl<Map<RuntimeValue<?>, RuntimeValue<?>>> {
        private InnerMapValue(Map<RuntimeValue<?>, RuntimeValue<?>> value) {
            super(value);
        }
    }
}
