package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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



    public boolean isMatches(Object value) {
        if (getModifiers().contains(AddonModifiers.NATIVE())) {
            for (Class<?> nativeClass : getEnvironment().getFileEnvironment().getNativeClasses()) {
                Method method;
                try {
                    method = nativeClass.getDeclaredMethod("isMatches", Object.class, ClassEnvironment.class);
                }
                catch (NoSuchMethodException e) {
                    continue;
                }

                if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    throw new InvalidSyntaxException("Can't call non-static native method to check whether class with id " + getEnvironment().getId() + " matches value");
                }
                if (!method.canAccess(null)) {
                    throw new InvalidSyntaxException("Can't call non-accessible native method to check whether class with id " + getEnvironment().getId() + " matches value");
                }
                if (!boolean.class.isAssignableFrom(method.getReturnType())) {
                    throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                }

                try {
                    Object object = method.invoke(null, value, getEnvironment());
                    return (boolean) object;
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to call native method", e);
                }
            }
        }

        if (value instanceof ClassValue classValue) return classValue.getId().equals(getId());
        return false;
    }

    public boolean isLikeMatches(FileEnvironment fileEnvironment, Object value) {
        if (isMatches(value)) return true;

        if (value instanceof ClassValue classValue) {
            for (String baseClassString : classValue.getBaseClasses()) {
                ClassValue baseClassValue = fileEnvironment.getClass(baseClassString);
                if (baseClassValue == null) continue;
                if (isLikeMatches(fileEnvironment, baseClassValue)) return true;
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
