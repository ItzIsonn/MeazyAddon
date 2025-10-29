package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.native_annotation.IsMatches;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.impl.ModifierableRuntimeValueImpl;
import me.itzisonn_.registry.RegistryEntry;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValueImpl extends ModifierableRuntimeValueImpl<Object> implements ClassValue {
    protected final Set<String> baseClasses;
    protected final ClassEnvironment environment;
    private final Method nativeMethod;

    public ClassValueImpl(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        if (baseClasses == null) throw new NullPointerException("BaseClasses can't be null");
        if (environment == null) throw new NullPointerException("Environment can't be null");

        super(null, environment.getModifiers());

        this.baseClasses = new HashSet<>(baseClasses);
        this.environment = environment;

        if (!getModifiers().contains(AddonModifiers.NATIVE())) {
            nativeMethod = null;
            return;
        }

        for (Class<?> nativeClass : getEnvironment().getFileEnvironment().getNativeClasses()) {
            methods:
            for (Method method : nativeClass.getMethods()) {
                if (!method.isAnnotationPresent(IsMatches.class)) continue;

                if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    throw new InvalidSyntaxException("Can't call non-static native method to check whether class with id " + getEnvironment().getId() + " matches value");
                }
                if (!method.canAccess(null)) {
                    throw new InvalidSyntaxException("Can't call non-accessible native method to check whether class with id " + getEnvironment().getId() + " matches value");
                }
                if (!boolean.class.isAssignableFrom(method.getReturnType())) {
                    throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                }

                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];
                    if (!Object.class.equals(parameter.getType()) && !ClassEnvironment.class.isAssignableFrom(parameter.getType())) continue methods;
                }

                nativeMethod = method;
                return;
            }
        }

        nativeMethod = null;
    }



    public boolean isMatches(Object value) {
        if (nativeMethod != null) {
            List<Object> methodArgs = new ArrayList<>();

            for (int i = 0; i < nativeMethod.getParameterCount(); i++) {
                Parameter parameter = nativeMethod.getParameters()[i];

                if (Object.class.equals(parameter.getType())) methodArgs.add(value);
                else if (ClassEnvironment.class.isAssignableFrom(parameter.getType())) methodArgs.add(getEnvironment());
                else throw new InvalidSyntaxException("Failed to call native method with id " + nativeMethod.getName());
            }

            try {
                Object object = nativeMethod.invoke(null, methodArgs.toArray());
                return (boolean) object;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to call native method with id " + nativeMethod.getName(), e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
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



    @Override
    public boolean isAccessible(Environment environment) {
        Identifier identifier = new ClassIdentifier(getId());

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getEnvironment().getParent(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
