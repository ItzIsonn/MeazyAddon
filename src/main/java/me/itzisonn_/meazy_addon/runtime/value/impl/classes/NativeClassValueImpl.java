package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link NativeClassValue}
 */
@EqualsAndHashCode(callSuper = true)
public class NativeClassValueImpl extends ClassValueImpl implements NativeClassValue {
    /**
     * @param baseClasses Base classes
     * @param environment Class environment
     *
     * @throws NullPointerException If either baseClasses or environment is null
     */
    public NativeClassValueImpl(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(baseClasses, environment);
    }

    /**
     * Constructor with empty baseClasses
     *
     * @param environment Class environment
     * @throws NullPointerException If given environment is null
     */
    public NativeClassValueImpl(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }

    public NativeClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment) {
        return new NativeClassValueImpl(baseClasses, classEnvironment);
    }

    public void setupEnvironment(ClassEnvironment classEnvironment) {}
}