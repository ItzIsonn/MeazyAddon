package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.constructor.NativeConstructorValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.NativeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

public class StringClassValue extends NativeClassValueImpl {
    private final boolean isDummy;

    public StringClassValue(ClassDeclarationEnvironment parent) {
        this(parent, null);
    }

    public StringClassValue(ClassDeclarationEnvironment parent, String string) {
        super(new ClassEnvironmentImpl(parent, false, "String"));
        setupEnvironment(getEnvironment());

        if (string != null) getEnvironment().assignVariable("value", new InnerStringValue(string));
        isDummy = (string == null);
    }

    protected StringClassValue(ClassEnvironment environment) {
        super(environment);
        isDummy = false;
    }

    @Override
    public NativeClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment) {
        return new StringClassValue(classEnvironment);
    }

    @Override
    public void setupEnvironment(ClassEnvironment classEnvironment) {
        classEnvironment.declareVariable(new VariableValueImpl(
                "value",
                new DataTypeImpl("Any", false),
                null,
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false,
                classEnvironment));


        classEnvironment.declareConstructor(new NativeConstructorValueImpl(List.of(
                new CallArgExpression("str", new DataTypeImpl("String", false), true)),
                classEnvironment, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, RuntimeContext context, ConstructorEnvironment constructorEnvironment) {
                constructorEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                        new InnerStringValue(String.valueOf(constructorArgs.getFirst().getFinalValue())));
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValueImpl("valueOf", List.of(
                new CallArgExpression("object", new DataTypeImpl("Any", false), true)),
                new DataTypeImpl("String", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                return new StringClassValue(functionEnvironment.getFileEnvironment(), functionArgs.getFirst().getFinalValue().toString());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("getLength", List.of(), new DataTypeImpl("Int", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get length of non-string value");

                return new IntValue(stringValue.getValue().length());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("replace", List.of(
                new CallArgExpression("target", new DataTypeImpl("String", false), true), new CallArgExpression("replacement", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't replace in non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                classEnvironment.assignVariable("value", new InnerStringValue(
                        stringValue.getValue().replace(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("replaceRegex", List.of(
                new CallArgExpression("target", new DataTypeImpl("String", false), true), new CallArgExpression("replacement", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't replace in non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerStringValue(
                        stringValue.getValue().replaceAll(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("replaceFirst", List.of(
                new CallArgExpression("target", new DataTypeImpl("String", false), true), new CallArgExpression("replacement", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't replace in non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerStringValue(
                        stringValue.getValue().replaceFirst(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("toUpperCase", List.of(), new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't make uppercase non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerStringValue(stringValue.getValue().toUpperCase()));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("toLowerCase", List.of(), new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't make lowercase non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerStringValue(stringValue.getValue().toLowerCase()));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("getCharAt", List.of(
                new CallArgExpression("pos", new DataTypeImpl("Int", false), true)),
                new DataTypeImpl("Char", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get char of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't get char at non-int pos");

                try {
                    return new StringClassValue(functionEnvironment.getFileEnvironment(), String.valueOf(stringValue.getValue().charAt(intValue.getValue())));
                }
                catch (IndexOutOfBoundsException ignore) {
                    throw new InvalidArgumentException("Index " + intValue.getValue() + " is out of bounds " + (stringValue.getValue().length() - 1));
                }
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("setCharAt",
                List.of(new CallArgExpression("pos", new DataTypeImpl("Int", false), true), new CallArgExpression("char", new DataTypeImpl("Char", false), true)),
                new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't change char of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't set char at non-int pos");
                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof InnerStringValue charValue)) throw new InvalidArgumentException("Can't set non-char value");

                StringBuilder stringBuilder = new StringBuilder(stringValue.getValue());
                stringBuilder.setCharAt(intValue.getValue(), charValue.getValue().charAt(0));
                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerStringValue(stringBuilder.toString()));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("contains", List.of(
                new CallArgExpression("target", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().contains(functionArgs.getFirst().getFinalValue().toString()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("startsWith", List.of(
                new CallArgExpression("target", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().startsWith(functionArgs.getFirst().getFinalValue().toString()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("endsWith", List.of(
                new CallArgExpression("target", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().endsWith(functionArgs.getFirst().getFinalValue().toString()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("repeat", List.of(
                new CallArgExpression("count", new DataTypeImpl("Int", false), true)),
                new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't repeat non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't repeat string non-int times");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                        new InnerStringValue(stringValue.getValue().repeat(intValue.getValue())));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("trim", List.of(), new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't trim non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new InnerStringValue(stringValue.getValue().trim()));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("isBlank", List.of(), new DataTypeImpl("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().isBlank());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("substring", List.of(
                new CallArgExpression("begin", new DataTypeImpl("Int", false), true), new CallArgExpression("end", new DataTypeImpl("Int", false), true)),
                new DataTypeImpl("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get substring of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Can't get substring with non-int begin value");
                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("Can't get substring with non-int end value");

                functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                        new InnerStringValue(stringValue.getValue().substring(beginValue.getValue(), endValue.getValue())));
                return new StringClassValue(classEnvironment);
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValueImpl("split", List.of(
                new CallArgExpression("regex", new DataTypeImpl("String", false), true)),
                new DataTypeImpl("List", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't split non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof StringClassValue splitValue)) throw new InvalidArgumentException("Can't split string with non-string value");

                String[] splitString = stringValue.getValue().split(splitValue.getValue());
                List<RuntimeValue<?>> list = new ArrayList<>();
                for (String str : splitString) {
                    list.add(new StringClassValue(functionEnvironment.getFileEnvironment(), str));
                }

                return ListClassNative.newList(functionEnvironment, context, list);
            }
        });
    }

    @Override
    public String getValue() {
        if (isDummy) return "";
        return String.valueOf(getEnvironment().getVariable("value").getValue().getFinalValue());
    }

    @Override
    public String toString() {
        return getValue();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        else if (!(o instanceof StringClassValue other)) return false;
        else {
            return other.getValue().equals(this.getValue());
        }
    }

    public static class InnerStringValue extends RuntimeValueImpl<String> {
        private InnerStringValue(String value) {
            super(value);
        }
    }
}