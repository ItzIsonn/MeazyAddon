package me.itzisonn_.meazy_addon.runtime.value.impl.constructor;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Constructor;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.BaseClassIdValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
public class RuntimeConstructorValueImpl extends ConstructorValueImpl {
    private final List<Statement> body;
    private final Method nativeMethod;

    public RuntimeConstructorValueImpl(List<ParameterExpression> parameters, List<Statement> body, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(parameters, parentEnvironment, modifiers);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;

        if (!getModifiers().contains(AddonModifiers.NATIVE())) {
            nativeMethod = null;
            return;
        }

        for (Class<?> nativeClass : getParentEnvironment().getFileEnvironment().getNativeClasses()) {
            methods:
            for (Method method : nativeClass.getMethods()) {
                if (!method.isAnnotationPresent(Constructor.class)) continue;

                if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    throw new RuntimeException("Can't call non-public static native constructor");
                }
                if (!method.canAccess(null)) {
                    throw new RuntimeException("Can't call non-accessible native constructor");
                }
                if (!method.getReturnType().equals(Void.TYPE)) {
                    throw new RuntimeException("Return value of native constructor with id " + method.getName() + " is invalid");
                }

                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];
                    if (i < getParameters().size()) {
                        if (!parameter.isAnnotationPresent(Argument.class)) continue methods;
                        if (!RuntimeValue.class.isAssignableFrom(parameter.getType())) continue methods;
                        continue;
                    }

                    if (!RuntimeContext.class.isAssignableFrom(parameter.getType()) &&
                            !ConstructorEnvironment.class.isAssignableFrom(parameter.getType()) &&
                            !Environment.class.isAssignableFrom(parameter.getType())) continue methods;
                }

                nativeMethod = method;
                return;
            }
        }

        throw new RuntimeException("Can't find native constructor with parameters " + parameters);
    }



    @Override
    public Set<String> run(RuntimeContext context, ConstructorEnvironment constructorEnvironment, Environment callEnvironment, List<RuntimeValue<?>> args) {
        if (nativeMethod != null) {
            List<Object> methodArgs = new ArrayList<>(args);

            for (int i = 0; i < nativeMethod.getParameterCount(); i++) {
                Parameter parameter = nativeMethod.getParameters()[i];
                if (i < getParameters().size()) continue;

                if (RuntimeContext.class.isAssignableFrom(parameter.getType())) methodArgs.add(context);
                else if (ConstructorEnvironment.class.isAssignableFrom(parameter.getType())) methodArgs.add(constructorEnvironment);
                else if (Environment.class.isAssignableFrom(parameter.getType())) methodArgs.add(callEnvironment);
                else throw new RuntimeException("Failed to call native constructor with parameters " + getParameters());
            }

            try {
                nativeMethod.invoke(null, methodArgs.toArray());
                return null;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to call native method with id " + nativeMethod.getName(), e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }

        Interpreter interpreter = context.getInterpreter();
        ClassEnvironment classEnvironment = (ClassEnvironment) constructorEnvironment.getParent();
        Set<String> calledBaseClasses = new HashSet<>();

        for (int i = 0; i < getParameters().size(); i++) {
            ParameterExpression parameter = getParameters().get(i);

            constructorEnvironment.declareVariable(new VariableValueImpl(
                    parameter.getId(),
                    parameter.getDataType(),
                    args.get(i),
                    parameter.isConstant(),
                    new HashSet<>(),
                    true,
                    constructorEnvironment
            ));
        }

        for (Statement statement : body) {
            if (statement instanceof BaseCallStatement) {
                RuntimeValue<?> value = interpreter.evaluate(statement, constructorEnvironment, classEnvironment);
                if (!(value instanceof BaseClassIdValue baseClassIdValue))
                    throw new RuntimeException("Unknown error occurred");
                calledBaseClasses.add(baseClassIdValue.getValue());
            }
            else interpreter.evaluate(statement, constructorEnvironment);
        }

        return calledBaseClasses;
    }
}