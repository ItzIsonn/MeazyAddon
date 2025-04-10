package me.itzisonn_.meazy_addon.runtime.value.native_class.primitive;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.ListClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.*;

public class StringClassValue extends NativeClassValue {
    public StringClassValue(ClassDeclarationEnvironment parent) {
        this(parent, null);
    }

    public StringClassValue(String string) {
        this(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), string);
    }

    public StringClassValue(ClassDeclarationEnvironment parent, String string) {
        this(getClassEnvironment(parent, string));
    }

    protected StringClassValue(ClassEnvironment environment) {
        super(environment);
    }

    private static ClassEnvironment getClassEnvironment(ClassDeclarationEnvironment parent, String string) {
        ClassEnvironment classEnvironment = new ClassEnvironmentImpl(parent, false, "String");


        classEnvironment.declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerStringValue(string),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        classEnvironment.declareConstructor(new NativeConstructorValue(List.of(), classEnvironment, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        classEnvironment.declareFunction(new NativeFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("String", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringClassValue(functionArgs.getFirst().getFinalValue().toString());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("getLength", List.of(), new DataType("Int", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get length of non-string value");

                return new IntValue(stringValue.getValue().length());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("replace", List.of(
                new CallArgExpression("target", new DataType("String", false), true), new CallArgExpression("replacement", new DataType("String", false), true)),
                new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("replaceRegex", List.of(
                new CallArgExpression("target", new DataType("String", false), true), new CallArgExpression("replacement", new DataType("String", false), true)),
                new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("replaceFirst", List.of(
                new CallArgExpression("target", new DataType("String", false), true), new CallArgExpression("replacement", new DataType("String", false), true)),
                new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("toUpperCase", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("toLowerCase", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("getCharAt", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                new DataType("Char", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get char of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidArgumentException("Can't get char at non-int pos");

                try {
                    return new StringClassValue(String.valueOf(stringValue.getValue().charAt(intValue.getValue())));
                }
                catch (IndexOutOfBoundsException ignore) {
                    throw new InvalidArgumentException("Index " + intValue.getValue() + " is out of bounds " + (stringValue.getValue().length() - 1));
                }
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("setCharAt",
                List.of(new CallArgExpression("pos", new DataType("Int", false), true), new CallArgExpression("char", new DataType("Char", false), true)),
                new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("contains", List.of(
                new CallArgExpression("target", new DataType("String", false), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().contains(functionArgs.getFirst().getFinalValue().toString()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("startsWith", List.of(
                new CallArgExpression("target", new DataType("String", false), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().startsWith(functionArgs.getFirst().getFinalValue().toString()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("endsWith", List.of(
                new CallArgExpression("target", new DataType("String", false), true)),
                new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().endsWith(functionArgs.getFirst().getFinalValue().toString()));
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("repeat", List.of(
                new CallArgExpression("count", new DataType("Int", false), true)),
                new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("trim", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("isBlank", List.of(), new DataType("Boolean", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't get data of non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new BooleanValue(stringValue.getValue().isBlank());
            }
        });

        classEnvironment.declareFunction(new NativeFunctionValue("substring", List.of(
                new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

        classEnvironment.declareFunction(new NativeFunctionValue("split", List.of(
                new CallArgExpression("regex", new DataType("String", false), true)),
                new DataType("List", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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
                    list.add(new StringClassValue(str));
                }
                return new ListClassValue(list);
            }
        });


        classEnvironment.declareFunction(new NativeFunctionValue("copy", List.of(), new DataType("String", false), classEnvironment, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerStringValue stringValue)) throw new InvalidSyntaxException("Can't copy non-string value");

                if (!(functionEnvironment.getVariableDeclarationEnvironment("value") instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidCallException("Invalid function call");
                }
                if (!classEnvironment.getId().equals("String")) throw new InvalidCallException("Invalid function call");

                return new StringClassValue(stringValue.getValue());
            }
        });

        return classEnvironment;
    }

    @Override
    public String getValue() {
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

    public static class InnerStringValue extends RuntimeValue<String> {
        private InnerStringValue(String value) {
            super(value);
        }
    }
}