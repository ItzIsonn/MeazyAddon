package me.itzisonn_.meazy_addon.runtime.value.native_class.collection;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

import java.util.Collection;

public class InnerCollectionValue<T extends Collection<RuntimeValue<?>>> extends RuntimeValueImpl<T> {
    protected InnerCollectionValue(T value) {
        super(value);
    }
}
