package me.itzisonn_.meazy_addon.runtime.environment.default_classes.primitive;

import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy_addon.runtime.environment.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy_addon.runtime.environment.default_classes.collections.ListClassEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy.runtime.value.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.StringValue;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringClassEnvironment extends ClassEnvironmentImpl {
    public StringClassEnvironment(ClassDeclarationEnvironment parent, String value) {
        super(parent, false, "String");


        declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerStringValue(value),
                false,
                Set.of(AddonModifiers.PRIVATE()),
                false));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(AddonModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("String", false), this, Set.of(AddonModifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(functionArgs.getFirst().getFinalValue().toString());
            }
        });

        declareFunction(new DefaultFunctionValue("getLength", List.of(), new DataType("Int", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();
                if (value instanceof String string) {
                    return new IntValue(string.length());
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction(new DefaultFunctionValue("replace", List.of(
                new CallArgExpression("target", new DataType("String", false), true), new CallArgExpression("replacement", new DataType("String", false), true)),
                new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                            new StringValue(string.replace(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("replaceRegex", List.of(
                new CallArgExpression("target", new DataType("String", false), true), new CallArgExpression("replacement", new DataType("String", false), true)),
                new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                            new StringValue(string.replaceAll(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("replaceFirst", List.of(
                new CallArgExpression("target", new DataType("String", false), true), new CallArgExpression("replacement", new DataType("String", false), true)),
                new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                            new StringValue(string.replaceFirst(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("toUpperCase", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.toUpperCase()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("toLowerCase", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.toLowerCase()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("getCharAt", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                new DataType("Char", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue) {
                        try {
                            return new StringValue(String.valueOf(string.charAt(intValue.getValue())));
                        }
                        catch (IndexOutOfBoundsException ignore) {
                            throw new InvalidArgumentException("Index " + intValue.getValue() + " is out of bounds " + (string.length() - 1));
                        }
                    }
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("setCharAt",
                List.of(new CallArgExpression("pos", new DataType("Int", false), true), new CallArgExpression("char", new DataType("Char", false), true)),
                new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                int pos;
                try {
                    pos = Integer.parseInt(functionArgs.getFirst().getFinalValue().toString().replaceAll("\\.0$", ""));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidArgumentException("Position must be int");
                }

                char ch = functionArgs.get(1).getFinalValue().toString().charAt(0);

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    StringBuilder stringBuilder = new StringBuilder(string);
                    stringBuilder.setCharAt(pos, ch);
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(stringBuilder.toString()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("contains", List.of(
                new CallArgExpression("target", new DataType("String", false), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.contains(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("startsWith", List.of(
                new CallArgExpression("target", new DataType("String", false), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.startsWith(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("endsWith", List.of(
                new CallArgExpression("target", new DataType("String", false), true)),
                new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.endsWith(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("repeat", List.of(
                new CallArgExpression("count", new DataType("Int", false), true)),
                new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                int count;
                try {
                    count = Integer.parseInt(functionArgs.getFirst().getFinalValue().toString().replaceAll("\\.0$", ""));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidArgumentException("Count must be int");
                }

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.repeat(count)));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("trim", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.trim()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("isBlank", List.of(), new DataType("Boolean", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.isBlank());
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("substring", List.of(
                new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                int begin;
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue) {
                    begin = beginValue.getValue();
                }
                else throw new InvalidArgumentException("Begin must be int");

                int end;
                if (functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue) {
                    end = endValue.getValue();
                }
                else throw new InvalidArgumentException("End must be int");

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.substring(begin, end)));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("split", List.of(
                new CallArgExpression("regex", new DataType("String", false), true)),
                new DataType("List", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    String[] splitString = string.split(functionArgs.getFirst().getFinalValue().toString());
                    List<RuntimeValue<?>> list = new ArrayList<>();
                    for (String str : splitString) {
                        list.add(new StringValue(str));
                    }
                    return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });
    }

    public static class InnerStringValue extends RuntimeValue<String> {
        private InnerStringValue(String value) {
            super(value);
        }
    }
}