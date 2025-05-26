package me.itzisonn_.meazy_addon.runtime.value;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;

/**
 * Represents null value
 */
public class NullValue implements RuntimeValue<Object> {
    @Override
    public Object getValue() {
        return null;
    }
}