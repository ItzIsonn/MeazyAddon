package me.itzisonn_.meazy_addon.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.value.ModifierableRuntimeValue;

import java.util.Set;

/**
 * Implementation of {@link ModifierableRuntimeValue}
 * @param <T> Type of stored value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ModifierableRuntimeValueImpl<T> extends RuntimeValueImpl<T> implements ModifierableRuntimeValue<T> {
    protected Set<Modifier> modifiers;

    /**
     * @param value Value to store
     */
    public ModifierableRuntimeValueImpl(T value, Set<Modifier> modifiers) {
        super(value);

        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");
        this.modifiers = Set.copyOf(modifiers);
    }
}