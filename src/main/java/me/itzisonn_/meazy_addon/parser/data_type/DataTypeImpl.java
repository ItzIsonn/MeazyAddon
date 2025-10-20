package me.itzisonn_.meazy_addon.parser.data_type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

/**
 * Implementation of {@link DataType}
 */
@Getter
@EqualsAndHashCode
public class DataTypeImpl implements DataType {
    private final String id;
    private final boolean isNullable;

    /**
     * @param id Id
     * @param isNullable Whether this data type accepts null values
     *
     * @throws NullPointerException If given id is null
     */
    public DataTypeImpl(String id, boolean isNullable) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        this.id = id;
        this.isNullable = isNullable;
    }

    @Override
    public boolean isMatches(RuntimeValue<?> value, FileEnvironment fileEnvironment) throws NullPointerException {
        if (value == null) return true;

        value = value.getFinalRuntimeValue();
        if (value instanceof NullValue) return isNullable;

        if (fileEnvironment == null) throw new NullPointerException("GlobalEnvironment can't be null");
        ClassValue classValue = fileEnvironment.getClass(id);
        return classValue != null && classValue.isLikeMatches(fileEnvironment, value);
    }

    @Override
    public String toString() {
        return id + (isNullable ? "?" : "");
    }
}