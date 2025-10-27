package me.itzisonn_.meazy_addon.runtime.value;

import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

/**
 * Represents null value
 */
public class NullValue extends RuntimeValueImpl<Object> {
    public NullValue() {
        super(null);
    }
}