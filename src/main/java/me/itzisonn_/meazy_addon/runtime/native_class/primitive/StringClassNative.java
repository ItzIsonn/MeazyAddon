package me.itzisonn_.meazy_addon.runtime.native_class.primitive;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.native_annotation.NativeContainer;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.native_annotation.NewInstance;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NativeContainer("data/program/primitive/string.mea")
public class StringClassNative {
    public static ClassValue newString(Environment callEnvironment, String string) {
        FileEnvironment fileEnvironment = callEnvironment.getFileEnvironment();
        ClassValue classValue = EvaluationHelper.callEmptyClassValue(fileEnvironment.getParent().getContext(), fileEnvironment.getClass("String"));
        classValue.getEnvironment().assignVariable("value", new InnerStringValue(string));
        return classValue;
    }



    @Function
    public static RuntimeValue<?> valueOf(@Argument RuntimeValue<?> value, FunctionEnvironment functionEnvironment) {
        try {
            return StringClassNative.newString(functionEnvironment, value.getFinalRuntimeValue().toString());
        }
        catch (NumberFormatException ignore) {
            return new NullValue();
        }
    }



    @Function
    public static IntValue getLength(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get length of non-string value");

        return new IntValue(stringValue.getValue().length());
    }

    @Function
    public static RuntimeValue<?> getCharAt(@Argument RuntimeValue<?> pos, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get char of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't get char at non-int pos");

        try {
            return StringClassNative.newString(functionEnvironment, String.valueOf(stringValue.getValue().charAt(intValue.getValue())));
        }
        catch (IndexOutOfBoundsException ignore) {
            throw new InvalidArgumentException("Index " + intValue.getValue() + " is out of bounds " + (stringValue.getValue().length() - 1));
        }
    }

    @Function
    public static RuntimeValue<?> setCharAt(@Argument RuntimeValue<?> pos, @Argument RuntimeValue<?> character, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't change char of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        if (!(pos.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't set char at non-int pos");

        StringBuilder stringBuilder = new StringBuilder(stringValue.getValue());
        stringBuilder.setCharAt(intValue.getValue(), character.getFinalValue().toString().charAt(0));
        return StringClassNative.newString(functionEnvironment, stringBuilder.toString());
    }



    @Function
    public static RuntimeValue<?> replace(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't replace in non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return StringClassNative.newString(classEnvironment, stringValue.getValue().replace(begin.getFinalValue().toString(), end.getFinalValue().toString()));
    }

    @Function
    public static RuntimeValue<?> replaceRegex(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't replace in non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().replaceAll(begin.getFinalValue().toString(), end.getFinalValue().toString()));
    }

    @Function
    public static RuntimeValue<?> replaceFirst(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't replace in non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().replaceFirst(begin.getFinalValue().toString(), end.getFinalValue().toString()));
    }



    @Function
    public static RuntimeValue<?> substring(@Argument RuntimeValue<?> begin, @Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get substring of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Can't get substring with non-int begin value");
        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("Can't get substring with non-int end value");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().substring(beginValue.getValue(), endValue.getValue()));
    }

    @Function
    public static RuntimeValue<?> substringFrom(@Argument RuntimeValue<?> begin, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get substring of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        if (!(begin.getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Can't get substring with non-int begin value");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().substring(beginValue.getValue()));
    }

    @Function
    public static RuntimeValue<?> substringTo(@Argument RuntimeValue<?> end, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get substring of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        if (!(end.getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("Can't get substring with non-int begin value");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().substring(0, endValue.getValue()));
    }



    @Function
    public static RuntimeValue<?> split(@Argument RuntimeValue<?> regex, RuntimeContext context, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't split non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        String[] splitString = stringValue.getValue().split(regex.getFinalValue().toString());
        List<RuntimeValue<?>> list = new ArrayList<>();
        for (String str : splitString) {
            list.add(StringClassNative.newString(functionEnvironment, str));
        }

        return ListClassNative.newList(functionEnvironment, context, list);
    }

    @Function
    public static RuntimeValue<?> repeat(@Argument RuntimeValue<?> times, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't repeat non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        if (!(times.getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't repeat string non-int times");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().repeat(intValue.getValue()));
    }

    @Function
    public static RuntimeValue<?> trim(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't trim non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().trim());
    }

    @Function
    public static RuntimeValue<?> toUpperCase(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't make uppercase non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().toUpperCase());
    }

    @Function
    public static RuntimeValue<?> toLowerCase(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't make uppercase non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return StringClassNative.newString(functionEnvironment, stringValue.getValue().toLowerCase());
    }



    @Function
    public static RuntimeValue<?> contains(@Argument RuntimeValue<?> target, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return new BooleanValue(stringValue.getValue().contains(target.getFinalValue().toString()));
    }

    @Function
    public static RuntimeValue<?> startsWith(@Argument RuntimeValue<?> target, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return new BooleanValue(stringValue.getValue().startsWith(target.getFinalValue().toString()));
    }

    @Function
    public static RuntimeValue<?> endsWith(@Argument RuntimeValue<?> target, FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return new BooleanValue(stringValue.getValue().endsWith(target.getFinalValue().toString()));
    }

    @Function
    public static RuntimeValue<?> isEmpty(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return new BooleanValue(stringValue.getValue().isEmpty());
    }

    @Function
    public static RuntimeValue<?> isBlank(FunctionEnvironment functionEnvironment) {
        RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
        if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

        if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
            throw new InvalidCallException("Invalid function call");
        }
        if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

        return new BooleanValue(stringValue.getValue().isBlank());
    }



    @NewInstance
    public static ClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        return new StringClassValue(baseClasses, classEnvironment, body);
    }

    public static class InnerStringValue extends RuntimeValueImpl<String> {
        private InnerStringValue(String value) {
            super(value);
        }
    }

    private static class StringClassValue extends RuntimeClassValueImpl {
        private StringClassValue(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
            super(baseClasses, classEnvironment, body);
        }

        @Override
        public String getValue() {
            VariableValue variableValue = getEnvironment().getVariable("value");
            if (variableValue == null) throw new RuntimeException("Failed to find internal variable with id 'value'");

            RuntimeValue<?> value = variableValue.getValue();
            if (value == null) return "";

            return String.valueOf(value.getFinalValue());
        }

        @Override
        public String toString() {
            return getValue();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            else if (!(o instanceof StringClassValue other)) return false;
            else return other.getValue().equals(getValue());
        }
    }
}
