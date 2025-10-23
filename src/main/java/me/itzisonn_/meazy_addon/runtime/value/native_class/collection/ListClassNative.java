package me.itzisonn_.meazy_addon.runtime.value.native_class.collection;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.AddonEvaluationFunctions;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.List;

@MeazyNativeClass("data/program/collection/list.mea")
public class ListClassNative {
    public static ClassValue newList(Environment callEnvironment, RuntimeContext context, List<RuntimeValue<?>> list) {
        ClassValue classValue = AddonEvaluationFunctions.callClassValue(context, callEnvironment.getFileEnvironment().getClass("List"), callEnvironment, new ArrayList<>());

        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue() instanceof InnerListValue listValue)) {
            throw new InvalidSyntaxException("Can't create list from non-list value");
        }
        listValue.getValue().addAll(list);

        return classValue;
    }

    public static InnerListValue getNativeList(FunctionEnvironment functionEnvironment) {
        return new InnerListValue(new ArrayList<>());
    }



    public static IntValue getSize(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get size of non-list value");

        return new IntValue(listValue.getValue().size());
    }



    public static BooleanValue add(RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

        return new BooleanValue(listValue.getValue().add(element.getFinalRuntimeValue()));
    }

    public static void add(RuntimeValue<?> element, RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't add element to non-int pos");
        listValue.getValue().add(intValue.getValue(), element.getFinalRuntimeValue());
    }

    public static BooleanValue remove(RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element to non-list value");

        return new BooleanValue(listValue.getValue().remove(element.getFinalRuntimeValue()));
    }

    public static RuntimeValue<?> removeFromPos(RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element from non-list value");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't remove element from non-int pos");
        return listValue.getValue().remove(intValue.getValue().intValue());
    }

    public static RuntimeValue<?> get(RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get element from non-list value");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get element from non-int pos");
        return listValue.getValue().get(intValue.getValue());
    }



    public static BooleanValue isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't use non-list value");

        return new BooleanValue(listValue.getValue().isEmpty());
    }

    public static BooleanValue contains(RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't use non-list value");

        return new BooleanValue(listValue.getValue().contains(element.getFinalRuntimeValue()));
    }



    public static ClassValue toString(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't convert non-list value to string");

        return StringClassNative.newString(functionEnvironment, AddonUtils.unpackRuntimeValuesCollection(listValue.getValue()).toString());
    }



    public static class InnerListValue extends InnerCollectionValue<List<RuntimeValue<?>>> {
        private InnerListValue(List<RuntimeValue<?>> value) {
            super(value);
        }
    }
}
