package me.itzisonn_.meazy_addon.runtime.value.native_class.collection;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@MeazyNativeClass("datagen/collection/list.mea")
public class MapClassNative {
    public static ClassValue newMap(Environment callEnvironment, Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        ClassValue classValue = AddonEvaluationFunctions.callClassValue(callEnvironment.getGlobalEnvironment().getClass("Map"), callEnvironment, new ArrayList<>());

        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("map").getVariable("map").getValue() instanceof InnerMapValue mapValue)) {
            throw new InvalidSyntaxException("Can't create map from non-map value");
        }
        mapValue.getValue().putAll(map);

        return classValue;
    }

    public static InnerMapValue getNativeMap(FunctionEnvironment functionEnvironment) {
        return new InnerMapValue(new HashMap<>());
    }



    public static IntValue getSize(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get size of non-map value");

        return new IntValue(mapValue.getValue().size());
    }



    public static void put(RuntimeValue<?> key, RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't put pair to non-map value");

        mapValue.getValue().put(key.getFinalRuntimeValue(), value.getFinalRuntimeValue());
    }

    public static void putIfAbsent(RuntimeValue<?> key, RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't put pair to non-map value");

        mapValue.getValue().putIfAbsent(key.getFinalRuntimeValue(), value.getFinalRuntimeValue());
    }

    public static RuntimeValue<?> remove(RuntimeValue<?> key, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't remove pair from non-map value");

        return mapValue.getValue().remove(key.getFinalRuntimeValue());
    }

    public static BooleanValue remove(RuntimeValue<?> key, RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't remove pair from non-map value");

        return new BooleanValue(mapValue.getValue().remove(key.getFinalRuntimeValue(), value.getFinalRuntimeValue()));
    }

    public static RuntimeValue<?> get(RuntimeValue<?> key, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value from non-map value");

        return mapValue.getValue().get(key.getFinalRuntimeValue());
    }

    public static RuntimeValue<?> getOrDefault(RuntimeValue<?> key, RuntimeValue<?> defaultValue, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value from non-map value");

        return mapValue.getValue().getOrDefault(key.getFinalRuntimeValue(), defaultValue.getFinalRuntimeValue());
    }



    public static BooleanValue isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

        return new BooleanValue(mapValue.getValue().isEmpty());
    }

    public static BooleanValue containsKey(RuntimeValue<?> key, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

        return new BooleanValue(mapValue.getValue().containsKey(key.getFinalRuntimeValue()));
    }

    public static BooleanValue containsValue(RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't use non-map value");

        return new BooleanValue(mapValue.getValue().containsValue(value.getFinalRuntimeValue()));
    }



    public static ClassValue getKeySet(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get key set of non-map value");

        return SetClassNative.newSet(functionEnvironment, mapValue.getValue().keySet());
    }

    public static ClassValue getValueList(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value list of non-map value");

        return ListClassNative.newList(functionEnvironment, new ArrayList<>(mapValue.getValue().values()));
    }



    public static StringClassValue toString(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't convert non-map value to string");

        return new StringClassValue(unpackRuntimeValuesMap(mapValue.getValue()).toString());
    }

    
    
    private static Map<RuntimeValue<?>, RuntimeValue<?>> unpackRuntimeValuesMap(Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        Map<RuntimeValue<?>, RuntimeValue<?>> unpackedMap = new HashMap<>();
        for (RuntimeValue<?> runtimeValue : map.keySet()) {
            unpackedMap.put(runtimeValue.getFinalRuntimeValue(), map.get(runtimeValue).getFinalRuntimeValue());
        }
        return unpackedMap;
    }

    public static class InnerMapValue extends RuntimeValue<Map<RuntimeValue<?>, RuntimeValue<?>>> {
        private InnerMapValue(Map<RuntimeValue<?>, RuntimeValue<?>> value) {
            super(value);
        }
    }
}
