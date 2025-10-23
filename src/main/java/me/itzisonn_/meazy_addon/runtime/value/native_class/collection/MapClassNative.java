package me.itzisonn_.meazy_addon.runtime.value.native_class.collection;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

@MeazyNativeClass("data/program/collection/list.mea")
public class MapClassNative {
    public static ClassValue newMap(Environment callEnvironment, RuntimeContext context, Map<RuntimeValue<?>, RuntimeValue<?>> map) {
        ClassValue classValue = AddonEvaluationFunctions.callClassValue(context, callEnvironment.getFileEnvironment().getClass("Map"), callEnvironment, new ArrayList<>());

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



    public static ClassValue getKeySet(RuntimeContext context, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get key set of non-map value");

        return SetClassNative.newSet(functionEnvironment, context, mapValue.getValue().keySet());
    }

    public static ClassValue getValueList(RuntimeContext context, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> map = functionEnvironment.getVariableDeclarationEnvironment("map").getVariable("map").getValue();
        if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get value list of non-map value");

        return ListClassNative.newList(functionEnvironment, context, new ArrayList<>(mapValue.getValue().values()));
    }



    public static ClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        return new RuntimeClassValueImpl(baseClasses, classEnvironment, body) {
            @Override
            public String toString() {
                RuntimeValue<?> map = getEnvironment().getVariableDeclarationEnvironment("map").getVariable("map").getValue();
                if (!(map instanceof InnerMapValue mapValue)) throw new InvalidSyntaxException("Can't get string from non-map value");
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
