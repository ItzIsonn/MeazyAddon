package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link ClassValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValueImpl extends RuntimeValueImpl<Object> implements ClassValue {
    protected final Set<String> baseClasses;
    protected final ClassEnvironment environment;

    /**
     * @param baseClasses Base classes
     * @param environment Environment
     *
     * @throws NullPointerException If either baseClasses or environment is null
     */
    public ClassValueImpl(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(null);

        if (baseClasses == null) throw new NullPointerException("BaseClasses can't be null");
        if (environment == null) throw new NullPointerException("Environment can't be null");

        this.baseClasses = new HashSet<>(baseClasses);
        this.environment = environment;
    }

    /**
     * Constructor with empty baseClasses
     *
     * @param environment Environment
     * @throws NullPointerException If given environment is null
     */
    public ClassValueImpl(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }

    public boolean isMatches(Object value) {
        if (value instanceof ClassValue classValue) return classValue.getId().equals(getId());
        return false;
    }

    public boolean isLikeMatches(Object value) {
        if (isMatches(value)) return true;

        if (value instanceof ClassValue classValue) {
            for (String baseClassString : classValue.getBaseClasses()) {
                ClassValue baseClassValue = classValue.getEnvironment().getGlobalEnvironment().getClass(baseClassString);
                if (baseClassValue == null) continue;
                if (isLikeMatches(baseClassValue)) return true;
            }
        }
        return false;
    }

    public String getId() {
        return environment.getId();
    }

    public Set<Modifier> getModifiers() {
        return environment.getModifiers();
    }

    @Override
    public String toString() {
        return "Class(" + getId() + ")";
    }
}
