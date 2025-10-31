package me.itzisonn_.meazy_addon.runtime.value.impl.function;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.native_annotation.Argument;
import me.itzisonn_.meazy.runtime.native_annotation.Function;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class RuntimeFunctionValueImpl extends FunctionValueImpl {
    private final List<Statement> body;
    private final Method nativeMethod;

    public RuntimeFunctionValueImpl(String id, List<ParameterExpression> parameters, List<Statement> body, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(id, parameters, returnDataType, parentEnvironment, modifiers);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;

        if (!getModifiers().contains(AddonModifiers.NATIVE())) {
            nativeMethod = null;
            return;
        }

        for (Class<?> nativeClass : getParentEnvironment().getFileEnvironment().getNativeClasses()) {
            methods:
            for (Method method : nativeClass.getMethods()) {
                if (!method.isAnnotationPresent(Function.class)) continue;
                Function functionAnnotation = method.getAnnotation(Function.class);

                if (!(functionAnnotation.value().isEmpty() && method.getName().equals(getId())) && !functionAnnotation.value().equals(getId())) continue;
                if (!method.getName().equals(getId())) continue;

                if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    throw new RuntimeException("Can't call non-public static native method with id " + method.getName());
                }
                if (!method.canAccess(null)) {
                    throw new RuntimeException("Can't call non-accessible native method with id " + method.getName());
                }
                if (!method.getReturnType().equals(Void.TYPE) && !RuntimeValue.class.isAssignableFrom(method.getReturnType())) {
                    throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                }

                for (int i = 0; i < method.getParameterCount(); i++) {
                    Parameter parameter = method.getParameters()[i];
                    if (i < getParameters().size()) {
                        if (!parameter.isAnnotationPresent(Argument.class)) continue methods;
                        if (!RuntimeValue.class.isAssignableFrom(parameter.getType())) continue methods;
                        continue;
                    }

                    if (!RuntimeContext.class.isAssignableFrom(parameter.getType()) &&
                            !FunctionEnvironment.class.isAssignableFrom(parameter.getType()) &&
                            !Environment.class.isAssignableFrom(parameter.getType())) continue methods;
                }

                nativeMethod = method;
                return;
            }
        }

        throw new RuntimeException("Can't find native method with id " + getId());
    }



    @Override
    public RuntimeValue<?> run(RuntimeContext context, FunctionEnvironment functionEnvironment, Environment callEnvironment, List<RuntimeValue<?>> args) {
        if (nativeMethod != null) {
            List<Object> methodArgs = new ArrayList<>(args);

            for (int i = 0; i < nativeMethod.getParameterCount(); i++) {
                Parameter parameter = nativeMethod.getParameters()[i];
                if (i < getParameters().size()) continue;

                if (RuntimeContext.class.isAssignableFrom(parameter.getType())) methodArgs.add(context);
                else if (FunctionEnvironment.class.isAssignableFrom(parameter.getType())) methodArgs.add(functionEnvironment);
                else if (Environment.class.isAssignableFrom(parameter.getType())) methodArgs.add(callEnvironment);
                else throw new RuntimeException("Failed to call native method with id " + getId());
            }

            try {
                Object object = nativeMethod.invoke(null, methodArgs.toArray());

                if (nativeMethod.getReturnType().equals(Void.TYPE)) {
                    if (getReturnDataType() != null) throw new RuntimeException("Invalid return value for native method with id " + nativeMethod.getName());
                    return null;
                }
                else return (RuntimeValue<?>) object;
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to call native method with id " + nativeMethod.getName(), e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }

        Interpreter interpreter = context.getInterpreter();

        for (int i = 0; i < getParameters().size(); i++) {
            ParameterExpression parameter = getParameters().get(i);

            functionEnvironment.declareVariable(new VariableValueImpl(
                    parameter.getId(),
                    parameter.getDataType(),
                    args.get(i),
                    parameter.isConstant(),
                    new HashSet<>(),
                    true,
                    functionEnvironment
            ));
        }

        RuntimeValue<?> result = null;
        boolean hasReturnStatement = false;
        for (int i = 0; i < body.size(); i++) {
            Statement statement = body.get(i);
            if (statement instanceof ReturnStatement) {
                hasReturnStatement = true;
                result = interpreter.evaluate(statement, functionEnvironment);
                if (i + 1 < body.size()) throw new RuntimeException("Return statement must be last in body");
                break;
            }
            RuntimeValue<?> value = interpreter.evaluate(statement, functionEnvironment);
            if (value instanceof ReturnInfoValue returnInfoValue) {
                hasReturnStatement = true;
                result = returnInfoValue.getFinalRuntimeValue();
                break;
            }
        }

        if ((result == null || result instanceof NullValue) && getReturnDataType() != null) {
            throw new RuntimeException(hasReturnStatement ?
                    "Function specified return value's data type but return statement is empty" : "Missing return statement");
        }

        return result;
    }
}
