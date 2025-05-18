package me.itzisonn_.meazy_addon.runtime.value.statement_info;

import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

public abstract class StatementInfoValue<T> extends RuntimeValueImpl<T> {
    protected StatementInfoValue(T value) {
        super(value);
    }
}
