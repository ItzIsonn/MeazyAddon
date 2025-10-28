package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.ClassValue;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class EmptyClassValueImpl extends ClassValueImpl {
    public EmptyClassValueImpl(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(baseClasses, environment);
    }

    public EmptyClassValueImpl(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }



    @Override
    public void setupEnvironment(RuntimeContext context, ClassEnvironment classEnvironment) {}

    @Override
    public ClassValue newInstance(ClassEnvironment classEnvironment) {
        return new EmptyClassValueImpl(getBaseClasses(), classEnvironment);
    }
}