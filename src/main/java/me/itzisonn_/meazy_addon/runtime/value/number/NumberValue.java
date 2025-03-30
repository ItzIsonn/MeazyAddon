package me.itzisonn_.meazy_addon.runtime.value.number;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;

public abstract class NumberValue<T extends Number> extends RuntimeValue<T> {
    protected NumberValue(T value) {
        super(value);
    }
}