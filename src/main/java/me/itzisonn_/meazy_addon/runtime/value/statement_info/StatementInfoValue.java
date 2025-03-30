package me.itzisonn_.meazy_addon.runtime.value.statement_info;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;

public abstract class StatementInfoValue<T> extends RuntimeValue<T> {
    protected StatementInfoValue(T value) {
        super(value);
    }
}
