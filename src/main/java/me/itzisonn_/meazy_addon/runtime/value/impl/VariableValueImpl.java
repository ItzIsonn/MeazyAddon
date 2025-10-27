package me.itzisonn_.meazy_addon.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.registry.RegistryEntry;

import java.util.Set;

/**
 * Implementation of {@link VariableValue}
 */
@EqualsAndHashCode(callSuper = false, doNotUseGetters = true)
public class VariableValueImpl extends ModifierableRuntimeValueImpl<RuntimeValue<?>> implements VariableValue {
    @Getter
    private final String id;
    @Getter
    private final DataType dataType;
    private RuntimeValue<?> value;
    private Expression rawValue;
    @Getter
    private final boolean isConstant;
    @Getter
    private final boolean isArgument;
    @Getter
    private final VariableDeclarationEnvironment parentEnvironment;

    /**
     * @param id Id
     * @param dataType DataType
     * @param value Value
     * @param isConstant Whether value is constant
     * @param modifiers Modifiers
     * @param isArgument Whether this variable is argument
     * @param parentEnvironment Parent environment
     *
     * @throws NullPointerException If either id, dataType or modifiers is null
     */
    public VariableValueImpl(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<Modifier> modifiers, boolean isArgument, VariableDeclarationEnvironment parentEnvironment) throws NullPointerException {
        super(null, modifiers);

        if (id == null) throw new NullPointerException("Id can't be null");
        if (dataType == null) throw new NullPointerException("DataType can't be null");
        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");

        this.id = id;
        this.dataType = dataType;
        this.parentEnvironment = parentEnvironment;
        setValue(value);
        this.rawValue = null;
        this.isConstant = isConstant;
        this.isArgument = isArgument;
    }

    /**
     * @param id Id
     * @param dataType DataType
     * @param rawValue Raw value
     * @param isConstant Whether value is constant
     * @param modifiers Modifiers
     * @param isArgument Whether this variable is argument
     * @param parentEnvironment Parent environment
     *
     * @throws NullPointerException If either id, dataType or modifiers is null
     */
    public VariableValueImpl(String id, DataType dataType, Expression rawValue, boolean isConstant, Set<Modifier> modifiers, boolean isArgument, VariableDeclarationEnvironment parentEnvironment) throws NullPointerException {
        super(null, modifiers);

        if (id == null) throw new NullPointerException("Id can't be null");
        if (dataType == null) throw new NullPointerException("DataType can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");

        this.id = id;
        this.dataType = dataType;
        this.parentEnvironment = parentEnvironment;
        this.value = null;
        this.rawValue = rawValue;
        this.isConstant = isConstant;
        this.modifiers = modifiers;
        this.isArgument = isArgument;
    }



    @Override
    public RuntimeValue<?> getValue() {
        if (rawValue != null) {
            if (value != null) throw new RuntimeException("Invalid state of variable value");
            setValue(parentEnvironment.getFileEnvironment().getParent().getContext().getInterpreter().evaluate(rawValue, parentEnvironment));
            rawValue = null;
        }

        return value;
    }

    @Override
    public void setValue(RuntimeValue<?> value) throws InvalidSyntaxException, InvalidValueException {
        if (isConstant && this.value != null && this.value.getFinalValue() != null) {
            throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);
        }

        if (value != null && !dataType.isMatches(value, parentEnvironment.getFileEnvironment())) {
            throw new InvalidValueException("Variable with id " + id + " requires data type " + dataType.getId());
        }
        this.value = value;
    }



    @Override
    public boolean isAccessible(Environment environment) {
        Identifier identifier = new VariableIdentifier(id);

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getParentEnvironment(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}