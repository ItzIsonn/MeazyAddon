package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.native_annotation.NewInstance;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class RuntimeClassValueImpl extends ClassValueImpl {
    private final List<Statement> body;
    private final Method nativeMethod;

    public RuntimeClassValueImpl(Set<String> baseClasses, ClassEnvironment environment, List<Statement> body) throws NullPointerException {
        super(baseClasses, environment);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;

        if (!getModifiers().contains(AddonModifiers.NATIVE())) {
            nativeMethod = null;
            return;
        }

        for (Class<?> nativeClass : getEnvironment().getFileEnvironment().getNativeClasses()) {
            methods:
            for (Method method : nativeClass.getMethods()) {
                if (!method.isAnnotationPresent(NewInstance.class)) continue;

                if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    throw new RuntimeException("Can't call non-public static native method to create new instance of class with id " + getEnvironment().getId());
                }
                if (!method.canAccess(null)) {
                    throw new RuntimeException("Can't call non-accessible native method to create new instance of class with id " + getEnvironment().getId());
                }
                if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                    throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                }

                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];

                    if (!Set.class.isAssignableFrom(parameter.getType()) &&
                            !ClassEnvironment.class.isAssignableFrom(parameter.getType()) &&
                            !List.class.isAssignableFrom(parameter.getType())) continue methods;
                }

                nativeMethod = method;
                return;
            }
        }

        nativeMethod = null;
    }



    @Override
    public void setupEnvironment(RuntimeContext context, ClassEnvironment classEnvironment) {
        Interpreter interpreter = context.getInterpreter();

        for (Statement statement : body) {
            interpreter.evaluate(statement, classEnvironment);
        }
    }

    @Override
    public ClassValue newInstance(ClassEnvironment classEnvironment) {
        if (nativeMethod != null) {
            List<Object> methodArgs = new ArrayList<>();

            for (int i = 0; i < nativeMethod.getParameterCount(); i++) {
                Parameter parameter = nativeMethod.getParameters()[i];

                if (Set.class.isAssignableFrom(parameter.getType())) methodArgs.add(getBaseClasses());
                else if (ClassEnvironment.class.isAssignableFrom(parameter.getType())) methodArgs.add(classEnvironment);
                else if (List.class.isAssignableFrom(parameter.getType())) methodArgs.add(body);
                else throw new RuntimeException("Failed to call native method with id " + getId());
            }

            try {
                Object object = nativeMethod.invoke(null, methodArgs.toArray());
                return (ClassValue) object;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to call native method with id " + nativeMethod.getName(), e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }

        return new RuntimeClassValueImpl(getBaseClasses(), classEnvironment, body);
    }
}