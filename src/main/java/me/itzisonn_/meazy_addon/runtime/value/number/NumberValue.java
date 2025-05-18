package me.itzisonn_.meazy_addon.runtime.value.number;

import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

public abstract class NumberValue<T extends Number> extends RuntimeValueImpl<T> {
    protected NumberValue(T value) {
        super(value);
    }
}