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
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NativeContainer("data/program/collection/list.mea")
public class ListClassNative {
    public static ClassValue newList(Environment callEnvironment, RuntimeContext context, List<RuntimeValue<?>> list) {
        ClassValue classValue = EvaluationHelper.callClassValue(context, callEnvironment.getFileEnvironment().getClass("List"), callEnvironment, new ArrayList<>());

        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue() instanceof InnerListValue listValue)) {
            throw new RuntimeException("Can't create list from non-list value");
        }
        listValue.getValue().addAll(list);

        return classValue;
    }

    @Function
    public static InnerListValue getNativeList() {
        return new InnerListValue(new ArrayList<>());
    }



    @Function
    public static IntValue getSize(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't get size of non-list value");

        return new IntValue(listValue.getValue().size());
    }



    @Function
    public static BooleanValue add(@Argument RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't add element to non-list value");

        return BooleanValue.of(listValue.getValue().add(element.getFinalRuntimeValue()));
    }

    @Function
    public static void add(@Argument RuntimeValue<?> element, @Argument RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't add element to non-list value");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new RuntimeException("Can't add element to non-int pos");
        listValue.getValue().add(intValue.getValue(), element.getFinalRuntimeValue());
    }

    @Function
    public static BooleanValue remove(@Argument RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't remove element to non-list value");

        return BooleanValue.of(listValue.getValue().remove(element.getFinalRuntimeValue()));
    }

    @Function
    public static RuntimeValue<?> removeFromPos(@Argument RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't remove element from non-list value");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new RuntimeException("Can't remove element from non-int pos");
        return listValue.getValue().remove(intValue.getValue().intValue());
    }

    @Function
    public static RuntimeValue<?> get(@Argument RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't get element from non-list value");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new RuntimeException("Can't get element from non-int pos");
        return listValue.getValue().get(intValue.getValue());
    }



    @Function
    public static BooleanValue isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't use non-list value");

        return BooleanValue.of(listValue.getValue().isEmpty());
    }

    @Function
    public static BooleanValue contains(@Argument RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't use non-list value");

        return BooleanValue.of(listValue.getValue().contains(element.getFinalRuntimeValue()));
    }



    @NewInstance
    public static ClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        return new RuntimeClassValueImpl(baseClasses, classEnvironment, body) {
            @Override
            public String toString() {
                RuntimeValue<?> value = getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new RuntimeException("Can't get string from non-list value");
                return AddonUtils.unpackRuntimeValuesCollection(listValue.getValue()).toString();
            }
        };
    }



    public static class InnerListValue extends InnerCollectionValue<List<RuntimeValue<?>>> {
        private InnerListValue(List<RuntimeValue<?>> value) {
            super(value);
        }
    }
}
