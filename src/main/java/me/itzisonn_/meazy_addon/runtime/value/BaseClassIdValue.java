package me.itzisonn_.meazy_addon.runtime.value;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

@EqualsAndHashCode(callSuper = true)
public class BaseClassIdValue extends RuntimeValueImpl<String> {
    public BaseClassIdValue(String id) {
        super(id);
    }
}
