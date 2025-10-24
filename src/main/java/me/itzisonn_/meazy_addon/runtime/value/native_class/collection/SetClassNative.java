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
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MeazyNativeClass("data/program/collection/set.mea")
public class SetClassNative {
    public static ClassValue newSet(Environment callEnvironment, RuntimeContext context, Set<RuntimeValue<?>> set) {
        ClassValue classValue = EvaluationHelper.callClassValue(context, callEnvironment.getFileEnvironment().getClass("Set"), callEnvironment, new ArrayList<>());

        if (!(classValue.getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue() instanceof InnerSetValue setValue)) {
            throw new InvalidSyntaxException("Can't create set from non-set value");
        }
        setValue.getValue().addAll(set);

        return classValue;
    }

    public static InnerSetValue getNativeSet(FunctionEnvironment functionEnvironment) {
        return new InnerSetValue(new HashSet<>());
    }



    public static IntValue getSize(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't get size of non-set value");

        return new IntValue(setValue.getValue().size());
    }



    public static BooleanValue add(RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't add element to non-set value");

        return new BooleanValue(setValue.getValue().add(element.getFinalRuntimeValue()));
    }

    public static BooleanValue remove(RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't remove element to non-set value");

        return new BooleanValue(setValue.getValue().remove(element.getFinalRuntimeValue()));
    }



    public static BooleanValue isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't use non-set value");

        return new BooleanValue(setValue.getValue().isEmpty());
    }

    public static BooleanValue contains(RuntimeValue<?> element, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't use non-set value");

        return new BooleanValue(setValue.getValue().contains(element.getFinalRuntimeValue()));
    }



    public static ClassValue toString(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
        if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't convert non-set value to string");

        return StringClassNative.newString(functionEnvironment, AddonUtils.unpackRuntimeValuesCollection(setValue.getValue()).toString());
    }



    public static ClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        return new RuntimeClassValueImpl(baseClasses, classEnvironment, body) {
            @Override
            public String toString() {
                RuntimeValue<?> value = getEnvironment().getVariableDeclarationEnvironment("collection").getVariable("collection").getValue();
                if (!(value instanceof InnerSetValue setValue)) throw new InvalidSyntaxException("Can't get string from non-set value");
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
