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
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NativeContainer("data/program/collection/set.mea")
public class SetClassNative {
    public static ClassValue newSet(Environment callEnvironment, RuntimeContext context, Set<RuntimeValue<?>> set) {
        ClassValue classValue = EvaluationHelper.callClassValue(context, callEnvironment.getFileEnvironment().getClass("Set"), callEnvironment, new ArrayList<>());

        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue() instanceof InnerSetValue setValue)) {
            throw new RuntimeException("Can't create set from non-set value");
        }
        setValue.getValue().addAll(set);

        return classValue;
    }

    @Function
    public static InnerSetValue getNativeSet() {
        return new InnerSetValue(new HashSet<>());
    }



    @Function
    public static IntValue getSize(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't get size of non-set value");

        return new IntValue(setValue.getValue().size());
    }



    @Function
    public static BooleanValue add(@Argument RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't add element to non-set value");

        return BooleanValue.of(setValue.getValue().add(element.getFinalRuntimeValue()));
    }

    @Function
    public static BooleanValue remove(@Argument RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't remove element to non-set value");

        return BooleanValue.of(setValue.getValue().remove(element.getFinalRuntimeValue()));
    }



    @Function
    public static BooleanValue isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't use non-set value");

        return BooleanValue.of(setValue.getValue().isEmpty());
    }

    @Function
    public static BooleanValue contains(@Argument RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't use non-set value");

        return BooleanValue.of(setValue.getValue().contains(element.getFinalRuntimeValue()));
    }



    @Function
    public static ClassValue toString(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't convert non-set value to string");

        return StringClassNative.newString(functionEnvironment, AddonUtils.unpackRuntimeValuesCollection(setValue.getValue()).toString());
    }



    @NewInstance
    public static ClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        return new RuntimeClassValueImpl(baseClasses, classEnvironment, body) {
            @Override
            public String toString() {
                RuntimeValue<?> value = getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new RuntimeException("Can't get string from non-set value");
                return AddonUtils.unpackRuntimeValuesCollection(setValue.getValue()).toString();
            }
        };
    }



    public static class InnerSetValue extends InnerCollectionValue<Set<RuntimeValue<?>>> {
        private InnerSetValue(Set<RuntimeValue<?>> value) {
            super(value);
        }
    }
}
