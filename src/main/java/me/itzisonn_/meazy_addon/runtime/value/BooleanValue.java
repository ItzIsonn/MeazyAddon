package me.itzisonn_.meazy_addon.runtime.value;

import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

public class BooleanValue extends RuntimeValueImpl<Boolean> {
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);

    public static BooleanValue of(boolean b) {
        if (b) return TRUE;
        return FALSE;
    }

    private BooleanValue(boolean value) {
        super(value);
    }
}