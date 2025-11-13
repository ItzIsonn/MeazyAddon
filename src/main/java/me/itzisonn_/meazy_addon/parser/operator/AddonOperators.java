package me.itzisonn_.meazy_addon.parser.operator;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.registry.RegistryEntry;

/**
 * Addon operators registrar
 *
 * @see Registries#OPERATORS
 */
public final class AddonOperators {
    private static boolean hasRegistered = false;

    private AddonOperators() {}



    public static Operator POWER() {
        return parseById("power");
    }

    public static Operator NEGATION() {
        return parseById("negation");
    }

    public static Operator INVERSION() {
        return parseById("inversion");
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
            Operator operator = entry.getValue();
            if (operator.getId().equals(id)) return operator;
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

        register(new PlusOperator());
        register(new MinusOperator());
        register(new MultiplyOperator());
        register(new DivideOperator());
        register(new PercentOperator());
        register(new PowerOperator());
        register(new NegationOperator());

        register(new AndOperator());
        register(new OrOperator());
        register(new InversionOperator());
        register(new EqualsOperator());
        register(new NotEqualsOperator());
        register(new GreaterOperator());
        register(new GreaterOrEqualsOperator());
        register(new LessOperator());
        register(new LessOrEqualsOperator());
    }

    private static void register(Operator operator) {
        Registries.OPERATORS.register(AddonMain.getIdentifier(operator.getId()), operator);
    }
}
