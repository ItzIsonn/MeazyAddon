package me.itzisonn_.meazy_addon.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

/**
 * Implementation of {@link RuntimeValue}
 * @param <T> Type of stored value
 */
@Getter
@EqualsAndHashCode
public class RuntimeValueImpl<T> implements RuntimeValue<T> {
    private final T value;

    /**
     * @param value Value to store
     */
    public RuntimeValueImpl(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}