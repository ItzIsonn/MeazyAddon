package me.itzisonn_.meazy_addon.runtime.value;

import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

/**
 * Represents null value
 */
public class NullValue extends RuntimeValueImpl<Object> {
    public static final NullValue INSTANCE = new NullValue();

    private NullValue() {
        super(null);
    }
}