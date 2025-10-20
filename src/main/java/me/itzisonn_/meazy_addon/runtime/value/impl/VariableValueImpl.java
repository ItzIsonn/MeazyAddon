package me.itzisonn_.meazy_addon.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;

import java.util.Set;

/**
 * Implementation of {@link VariableValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class VariableValueImpl extends RuntimeValueImpl<RuntimeValue<?>> implements VariableValue {
    private final String id;
    private final DataType dataType;
    private RuntimeValue<?> value;
    private final boolean isConstant;
    private final Set<Modifier> modifiers;
    private final boolean isArgument;
    private final Environment parentEnvironment;

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
    public VariableValueImpl(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<Modifier> modifiers, boolean isArgument, Environment parentEnvironment) throws NullPointerException {
        super(null);

        if (id == null) throw new NullPointerException("Id can't be null");
        if (dataType == null) throw new NullPointerException("DataType can't be null");
        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");

        this.id = id;
        this.dataType = dataType;
        this.parentEnvironment = parentEnvironment;
        setValue(value);
        this.isConstant = isConstant;
        this.modifiers = modifiers;
        this.isArgument = isArgument;
    }

    public void setValue(RuntimeValue<?> value) throws InvalidSyntaxException, InvalidValueException {
        if (isConstant && this.value != null && this.value.getFinalValue() != null) {
            throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);
        }

        if (!dataType.isMatches(value, parentEnvironment.getFileEnvironment())) throw new InvalidValueException("Variable with id " + id + " requires data type " + dataType.getId());
        this.value = value;
    }
}