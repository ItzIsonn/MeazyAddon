package me.itzisonn_.meazy_addon.parser;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassNative;
import me.itzisonn_.meazy_addon.runtime.value.number.*;
import me.itzisonn_.registry.RegistryEntry;

/**
 * Addon operators registrar
 *
 * @see Registries#OPERATORS
 */
public final class AddonOperators {
    private static boolean hasRegistered = false;

    private AddonOperators() {}



    public static Operator PLUS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("plus")).getValue();
    }

    public static Operator MINUS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("minus")).getValue();
    }

    public static Operator MULTIPLY() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("multiply")).getValue();
    }

    public static Operator DIVIDE() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("divide")).getValue();
    }

    public static Operator PERCENT() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("percent")).getValue();
    }

    public static Operator POWER() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("power")).getValue();
    }

    public static Operator NEGATION() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("negation")).getValue();
    }



    public static Operator AND() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("and")).getValue();
    }

    public static Operator OR() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("or")).getValue();
    }

    public static Operator INVERSION() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("inversion")).getValue();
    }

    public static Operator EQUALS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("equals")).getValue();
    }

    public static Operator NOT_EQUALS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("not_equals")).getValue();
    }

    public static Operator GREATER() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("greater")).getValue();
    }

    public static Operator GREATER_OR_EQUALS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("greater_or_equals")).getValue();
    }

    public static Operator LESS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("less")).getValue();
    }

    public static Operator LESS_OR_EQUALS() {
        return Registries.OPERATORS.getEntry(AddonMain.getIdentifier("less_or_equals")).getValue();
    }



    /**
     * Finds registered Operator with given symbol and given type
     *
     * @param symbol Operator's symbol
     * @param operatorType Operator's type or null if any
     * @return Operator with given symbol or null
     */
    public static Operator parse(String symbol, OperatorType operatorType) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            Operator operator = entry.getValue();
            if (symbol.equals(operator.getSymbol()) && (operatorType == null || operator.getOperatorType() == operatorType)) return operator;
        }

        return null;
    }

    /**
     * Finds registered Operator with given id
     *
     * @param id Operator's id
     * @return Operator with given id or null
     */
    public static Operator parseById(String id) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            if (entry.getIdentifier().getId().equals(id)) return entry.getValue();
        }

        return null;
    }



    /**
     * Initializes {@link Registries#OPERATORS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#OPERATORS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("Operators have already been initialized");
        hasRegistered = true;

        register("plus", new Operator("+", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return AddonUtils.optimalNumberValue(numberValue1.getValue().doubleValue() + numberValue2.getValue().doubleValue());
                }
                return StringClassNative.newString(environment, String.valueOf(value1.getValue()) + value2.getValue());
            }
        });
        register("minus", new Operator("-", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return AddonUtils.optimalNumberValue(numberValue1.getValue().doubleValue() - numberValue2.getValue().doubleValue());
                }
                return null;
            }
        });
        register("multiply", new Operator("*", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return AddonUtils.optimalNumberValue(numberValue1.getValue().doubleValue() * numberValue2.getValue().doubleValue());
                }

                String string;
                int amount;

                if (value1 instanceof StringClassNative.InnerStringValue stringValue && value2 instanceof IntValue numberValue) {
                    string = stringValue.getValue();
                    amount = numberValue.getValue();
                }
                else if (value2 instanceof StringClassNative.InnerStringValue stringValue && value1 instanceof IntValue numberValue) {
                    string = stringValue.getValue();
                    amount = numberValue.getValue();
                }
                else throw new InvalidSyntaxException("Can't multiply values " + value1 + " and " + value2);

                if (amount < 0) throw new InvalidSyntaxException("Can't multiply string by a negative int");

                return StringClassNative.newString(environment, new StringBuilder().repeat(string, amount).toString());
            }
        });
        register("divide", new Operator("/", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return AddonUtils.optimalNumberValue(numberValue1.getValue().doubleValue() / numberValue2.getValue().doubleValue());
                }
                return null;
            }
        });
        register("percent", new Operator("%", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return AddonUtils.optimalNumberValue(numberValue1.getValue().doubleValue() % numberValue2.getValue().doubleValue());
                }
                return null;
            }
        });
        register("power", new Operator("^", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return AddonUtils.optimalNumberValue(Math.pow(numberValue1.getValue().doubleValue(), numberValue2.getValue().doubleValue()));
                }
                return null;
            }
        });
        register("negation", new Operator("-", OperatorType.PREFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue) {
                    return AddonUtils.optimalNumberValue(-numberValue.getValue().doubleValue());
                }
                return null;
            }
        });

        register("and", new Operator("&&", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof BooleanValue booleanValue1 && value2 instanceof BooleanValue booleanValue2) {
                    return new BooleanValue(booleanValue1.getValue() && booleanValue2.getValue());
                }
                return null;
            }
        });
        register("or", new Operator("||", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof BooleanValue booleanValue1 && value2 instanceof BooleanValue booleanValue2) {
                    return new BooleanValue(booleanValue1.getValue() || booleanValue2.getValue());
                }
                return null;
            }
        });
        register("inversion", new Operator("!", OperatorType.PREFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof BooleanValue booleanValue) {
                    return new BooleanValue(!booleanValue.getValue());
                }
                return null;
            }
        });
        register("equals", new Operator("==", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NullValue) return new BooleanValue(value2 instanceof NullValue);
                if (value1.getValue() == null) return new BooleanValue(value1.equals(value2));
                return new BooleanValue(value1.getValue().equals(value2.getValue()));
            }
        });
        register("not_equals", new Operator("!=", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NullValue) return new BooleanValue(!(value2 instanceof NullValue));
                return new BooleanValue(!value1.getValue().equals(value2.getValue()));
            }
        });
        register("greater", new Operator(">", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() > numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringClassNative.InnerStringValue stringValue1 && value2 instanceof StringClassNative.InnerStringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() > stringValue2.getValue().length());
                }
                return null;
            }
        });
        register("greater_or_equals", new Operator(">=", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() >= numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringClassNative.InnerStringValue stringValue1 && value2 instanceof StringClassNative.InnerStringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() >= stringValue2.getValue().length());
                }
                return null;
            }
        });
        register("less", new Operator("<", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() < numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringClassNative.InnerStringValue stringValue1 && value2 instanceof StringClassNative.InnerStringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() < stringValue2.getValue().length());
                }
                return null;
            }
        });
        register("less_or_equals", new Operator("<=", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(Environment environment, RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() <= numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringClassNative.InnerStringValue stringValue1 && value2 instanceof StringClassNative.InnerStringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() <= stringValue2.getValue().length());
                }
                return null;
            }
        });
    }

    private static void register(String id, Operator operator) {
        Registries.OPERATORS.register(AddonMain.getIdentifier(id), operator);
    }
}
