package me.itzisonn_.meazy_addon.runtime.evaluation_function;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy.runtime.value.classes.RuntimeClassValue;
import me.itzisonn_.meazy.runtime.value.constructor.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.constructor.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.constructor.RuntimeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.BaseClassIdValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class EvaluationHelper {
    public static final List<FunctionDeclarationStatement> extensionFunctions = new ArrayList<>();

    private EvaluationHelper() {}

    public static RuntimeValue<?> evaluateAssignmentExpression(RuntimeContext context, AssignmentExpression assignmentExpression, Environment environment) {
        Interpreter interpreter = context.getInterpreter();

        if (assignmentExpression.getId() instanceof VariableIdentifier variableIdentifier) {
            RuntimeValue<?> value = interpreter.evaluate(assignmentExpression.getValue(), environment).getFinalRuntimeValue();
            environment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
            return value;
        }

        if (assignmentExpression.getId() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = interpreter.evaluate(memberExpression, environment);
            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> value = interpreter.evaluate(assignmentExpression.getValue(), environment).getFinalRuntimeValue();
                variableValue.setValue(value);
                return value;
            }
            throw new InvalidSyntaxException("Can't assign value to not variable " + memberExpressionValue);
        }

        throw new InvalidSyntaxException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    public static boolean parseCondition(RuntimeContext context, Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = context.getInterpreter().evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, boolean isDefault, FileEnvironment fileEnvironment) {
        String defaultString = isDefault ? ". It's probably an Addon's error" : "";

        if (returnValue == null) {
            if (returnDataType != null) {
                throw new InvalidSyntaxException("Didn't find return value but function with id " + functionId + " must return value" + defaultString);
            }
            return null;
        }
        if (returnDataType == null) {
            throw new InvalidSyntaxException("Found return value but function with id " + functionId + " must return nothing" + defaultString);
        }

        if (!returnDataType.isMatches(returnValue, fileEnvironment)) {
            throw new InvalidSyntaxException("Returned value's data type is different from specified (" + returnDataType.getId() + ")" + defaultString);
        }

        return returnValue;
    }

    public static boolean hasRepeatedBaseClasses(Set<String> baseClassesList, List<String> baseClasses, FileEnvironment fileEnvironment) {
        for (String baseClass : baseClassesList) {
            if (baseClasses.contains(baseClass)) {
                return true;
            }
            baseClasses.add(baseClass);

            ClassValue classValue = fileEnvironment.getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException("Class with id " + baseClass + " doesn't exist");
            }
            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't inherit final class with id " + baseClass);

            boolean check = hasRepeatedBaseClasses(classValue.getBaseClasses(), baseClasses, fileEnvironment);
            if (check) return true;
        }

        return false;
    }

    public static boolean hasRepeatedVariables(Set<String> baseClassesList, List<String> variables, FileEnvironment fileEnvironment) {
        for (String baseClass : baseClassesList) {
            ClassValue classValue = fileEnvironment.getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException("Class with id " + baseClass + " doesn't exist");
            }

            for (VariableValue variableValue : classValue.getEnvironment().getVariables()) {
                if (variableValue.getModifiers().contains(AddonModifiers.PRIVATE())) continue;
                if (variables.contains(variableValue.getId())) {
                    return true;
                }
                variables.add(variableValue.getId());
            }

            boolean check = hasRepeatedVariables(classValue.getBaseClasses(), variables, fileEnvironment);
            if (check) return true;
        }

        return false;
    }

    private static boolean hasRepeatedFunctions(Set<ClassEnvironment> baseClassesList, List<FunctionValue> functions) {
        for (ClassEnvironment classEnvironment : baseClassesList) {
            for (FunctionValue functionValue : getFinalFunctions(classEnvironment)) {
                if (functionValue.getModifiers().contains(AddonModifiers.PRIVATE())) continue;
                if (functionValue.getModifiers().contains(AddonModifiers.ABSTRACT())) {
                    if (functions.stream().anyMatch(function -> function.isLike(functionValue) && !function.getModifiers().contains(AddonModifiers.ABSTRACT()))) {
                        return true;
                    }
                }
                else if (functions.stream().anyMatch(function -> function.isLike(functionValue))) {
                    return true;
                }
                functions.add(functionValue);
            }

            boolean check = hasRepeatedFunctions(classEnvironment.getBaseClasses(), functions);
            if (check) return true;
        }

        return false;
    }

    private static List<FunctionValue> getFinalFunctions(ClassEnvironment classEnvironment) {
        List<FunctionValue> functionValues = new ArrayList<>();
        for (FunctionValue functionValue : classEnvironment.getFunctions()) {
            if (!functionValue.isOverridden()) functionValues.add(functionValue);
        }

        return functionValues;
    }

    public static ClassValue callClassValue(RuntimeContext context, ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = initClassEnvironment(context, classValue, callEnvironment, args);

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            if (runtimeClassValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : runtimeClassValue.getEnvironment().getFileEnvironment().getNativeClasses()) {
                    Method method;
                    try {
                        method = nativeClass.getDeclaredMethod("newInstance", Set.class, ClassEnvironment.class, List.class);
                    }
                    catch (NoSuchMethodException e) {
                        continue;
                    }

                    if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                        throw new InvalidSyntaxException("Can't call non-public static native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!method.canAccess(null)) {
                        throw new InvalidSyntaxException("Can't call non-accessible native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
                        return (ClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }
            }

            return new RuntimeClassValueImpl(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
        }

        if (classValue instanceof NativeClassValue nativeClassValue) return nativeClassValue.newInstance(nativeClassValue.getBaseClasses(), classEnvironment);

        throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
    }

    public static ClassEnvironment initClassEnvironment(RuntimeContext context, ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getFileEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);
        Interpreter interpreter = context.getInterpreter();

        Set<String> calledBaseClasses = new HashSet<>();
        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            for (Statement statement : runtimeClassValue.getBody()) {
                interpreter.evaluate(statement, classEnvironment);
            }
            for (FunctionDeclarationStatement functionDeclarationStatement : extensionFunctions) {
                if (functionDeclarationStatement.getClassId().equals(runtimeClassValue.getId())) {
                    interpreter.evaluate(new FunctionDeclarationStatement(
                            functionDeclarationStatement.getModifiers(),
                            functionDeclarationStatement.getId(),
                            null,
                            functionDeclarationStatement.getArgs(),
                            functionDeclarationStatement.getBody(),
                            functionDeclarationStatement.getReturnDataType()
                    ), classEnvironment);
                }
            }

            if (classEnvironment.hasConstructor()) {
                ConstructorValue rawConstructor = classEnvironment.getConstructor(args);
                if (rawConstructor == null) throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");

                if (rawConstructor instanceof RuntimeConstructorValue runtimeConstructorValue) {
                    if (runtimeConstructorValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                        ArrayList<Class<?>> params = new ArrayList<>(Collections.nCopies(runtimeConstructorValue.getArgs().size(), RuntimeValue.class));
                        params.add(ConstructorEnvironment.class);
                        Class<?>[] array = params.toArray(Class[]::new);

                        boolean hasFound = false;
                        for (Class<?> nativeClass : runtimeConstructorValue.getParentEnvironment().getFileEnvironment().getNativeClasses()) {
                            Method method;
                            try {
                                method = nativeClass.getDeclaredMethod("constructor", array);
                            }
                            catch (NoSuchMethodException e) {
                                continue;
                            }

                            if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                                throw new InvalidSyntaxException("Can't call non-public static native constructor");
                            }
                            if (!method.canAccess(null)) {
                                throw new InvalidSyntaxException("Can't call non-accessible native constructor");
                            }
                            if (!method.getReturnType().equals(Void.TYPE)) {
                                throw new RuntimeException("Return value of native constructor with id " + method.getName() + " is invalid");
                            }

                            try {
                                ArrayList<Object> constructorArgs = new ArrayList<>(args);
                                constructorArgs.add(constructorEnvironment);
                                method.invoke(null, constructorArgs.toArray());
                                hasFound = true;
                            }
                            catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("Failed to call native constructor", e);
                            }
                        }

                        if (!hasFound) throw new InvalidSyntaxException("Can't find native constructor");
                    }

                    if (runtimeConstructorValue.getModifiers().contains(AddonModifiers.PRIVATE()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            return classEnv.getId().equals(classValue.getId());
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has private access");
                    }

                    if (runtimeConstructorValue.getModifiers().contains(AddonModifiers.PROTECTED()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            if (classEnv.getId().equals(classValue.getId())) return true;

                            ClassValue parentClassValue = callEnvironment.getFileEnvironment().getClass(classEnv.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnv.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(classValue.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has protected access");
                    }

                    if (!runtimeConstructorValue.getModifiers().contains(AddonModifiers.OPEN()) && classEnvironment.getParentFile() != null &&
                            !classEnvironment.getParentFile().equals(callEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open constructor from different file (" + callEnvironment.getParentFile().getName() + ")");
                    }

                    for (int i = 0; i < runtimeConstructorValue.getArgs().size(); i++) {
                        CallArgExpression callArgExpression = runtimeConstructorValue.getArgs().get(i);

                        constructorEnvironment.declareVariable(new VariableValueImpl(
                                callArgExpression.getId(),
                                callArgExpression.getDataType(),
                                args.get(i),
                                callArgExpression.isConstant(),
                                new HashSet<>(),
                                true,
                                constructorEnvironment
                        ));
                    }

                    for (Statement statement : runtimeConstructorValue.getBody()) {
                        if (statement instanceof BaseCallStatement) {
                            RuntimeValue<?> value = interpreter.evaluate(statement, constructorEnvironment, classEnvironment);
                            if (!(value instanceof BaseClassIdValue baseClassIdValue)) throw new RuntimeException("Unknown error occurred");
                            if (!classValue.getBaseClasses().contains(baseClassIdValue.getValue()))
                                throw new InvalidSyntaxException("Can't call base class " + baseClassIdValue.getValue() + " because it's not base class of class " + classValue.getId());
                            calledBaseClasses.add(baseClassIdValue.getValue());
                        }
                        else interpreter.evaluate(statement, constructorEnvironment);
                    }
                }
            }
            else if (!args.isEmpty()) {
                throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");
            }
        }
        else if (classValue instanceof NativeClassValue nativeClassValue) {
            nativeClassValue.setupEnvironment(classEnvironment);

            if (classEnvironment.hasConstructor()) {
                RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                if (rawConstructor == null) throw new InvalidCallException("Class with id " + nativeClassValue.getId() + " doesn't have requested constructor");

                if (rawConstructor instanceof NativeConstructorValue nativeConstructorValue) {
                    if (nativeConstructorValue.getModifiers().contains(AddonModifiers.PRIVATE()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            return classEnv.getId().equals(nativeClassValue.getId());
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has private access");
                    }

                    if (nativeConstructorValue.getModifiers().contains(AddonModifiers.PROTECTED()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            if (classEnv.getId().equals(classValue.getId())) return true;

                            ClassValue parentClassValue = callEnvironment.getFileEnvironment().getClass(classEnv.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnv.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(classValue.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has protected access");
                    }

                    if (!nativeConstructorValue.getModifiers().contains(AddonModifiers.OPEN()) &&
                            !classEnvironment.getParentFile().equals(callEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open constructor from different file (" + callEnvironment.getParentFile().getName() + ")");
                    }

                    nativeConstructorValue.run(args, context, constructorEnvironment);
                }
            }
            else if (!args.isEmpty()) throw new InvalidCallException("Class with id " + nativeClassValue.getId() + " doesn't have requested constructor");
        }
        else throw new RuntimeException("Can't init ClassEnvironment of class value " + classValue.getClass().getName());

        for (VariableValue variableValue : classEnvironment.getVariables()) {
            if (variableValue.isConstant() && variableValue.getValue() == null) {
                throw new InvalidSyntaxException("Empty constant variable with id " + variableValue.getId() + " hasn't been initialized");
            }
        }

        for (String baseClass : classValue.getBaseClasses()) {
            if (calledBaseClasses.contains(baseClass)) continue;
            ClassValue baseClassValue = callEnvironment.getFileEnvironment().getClass(baseClass);
            classEnvironment.addBaseClass(initClassEnvironment(context, baseClassValue, constructorEnvironment, new ArrayList<>()));
        }

        for (FunctionValue value : classEnvironment.getFunctions()) {
            for (ClassEnvironment baseClass : classEnvironment.getDeepBaseClasses()) {
                for (FunctionValue baseClassFunction : baseClass.getFunctions()) {
                    if (baseClassFunction.isLike(value) && !baseClassFunction.getModifiers().contains(AddonModifiers.PRIVATE())) {
                        if (baseClassFunction.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't override final function with id " + baseClassFunction.getId());
                        baseClassFunction.setOverridden();
                    }
                }
            }
        }

        if (hasRepeatedFunctions(classEnvironment.getBaseClasses(), new ArrayList<>(classEnvironment.getFunctions()))) {
            throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " has repeated functions");
        }

        if (!classEnvironment.getModifiers().contains(AddonModifiers.ABSTRACT())) {
            for (ClassEnvironment baseClass : classEnvironment.getBaseClasses()) {
                for (FunctionValue functionValue : getFinalFunctions(baseClass)) {
                    if (functionValue.getModifiers().contains(AddonModifiers.ABSTRACT())) {
                        throw new InvalidSyntaxException("Abstract function with id " + functionValue.getId() + " in class with id " + classEnvironment.getId() + " hasn't been initialized");
                    }
                }
            }
        }

        return classEnvironment;
    }

    public static ClassValue callEmptyClassValue(RuntimeContext context, ClassValue classValue) {
        ClassEnvironment classEnvironment = initEmptyClassEnvironment(context, classValue);

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            if (runtimeClassValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : classEnvironment.getFileEnvironment().getNativeClasses()) {
                    Method method;
                    try {
                        method = nativeClass.getDeclaredMethod("newInstance", Set.class, ClassEnvironment.class, List.class);
                    }
                    catch (NoSuchMethodException e) {
                        continue;
                    }

                    if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                        throw new InvalidSyntaxException("Can't call non-public static native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!method.canAccess(null)) {
                        throw new InvalidSyntaxException("Can't call non-accessible native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
                        return (ClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }
            }

            return new RuntimeClassValueImpl(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
        }
        if (classValue instanceof NativeClassValue nativeClassValue) return nativeClassValue.newInstance(nativeClassValue.getBaseClasses(), classEnvironment);

        throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
    }

    public static ClassEnvironment initEmptyClassEnvironment(RuntimeContext context, ClassValue classValue) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getFileEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);
        Interpreter interpreter = context.getInterpreter();

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            for (Statement statement : runtimeClassValue.getBody()) {
                interpreter.evaluate(statement, classEnvironment);
            }
            for (FunctionDeclarationStatement functionDeclarationStatement : extensionFunctions) {
                if (functionDeclarationStatement.getClassId().equals(runtimeClassValue.getId())) {
                    interpreter.evaluate(new FunctionDeclarationStatement(
                            functionDeclarationStatement.getModifiers(),
                            functionDeclarationStatement.getId(),
                            null,
                            functionDeclarationStatement.getArgs(),
                            functionDeclarationStatement.getBody(),
                            functionDeclarationStatement.getReturnDataType()
                    ), classEnvironment);
                }
            }
        }
        else if (classValue instanceof NativeClassValue nativeClassValue) {
            nativeClassValue.setupEnvironment(classEnvironment);
        }
        else throw new RuntimeException("Can't init ClassEnvironment of class value " + classValue.getClass().getName());

        for (String baseClass : classValue.getBaseClasses()) {
            ClassValue baseClassValue = classEnvironment.getFileEnvironment().getClass(baseClass);
            classEnvironment.addBaseClass(initClassEnvironment(context, baseClassValue, constructorEnvironment, new ArrayList<>()));
        }

        for (FunctionValue value : classEnvironment.getFunctions()) {
            for (ClassEnvironment baseClass : classEnvironment.getDeepBaseClasses()) {
                for (FunctionValue baseClassFunction : baseClass.getFunctions()) {
                    if (baseClassFunction.isLike(value) && !baseClassFunction.getModifiers().contains(AddonModifiers.PRIVATE())) {
                        if (baseClassFunction.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't override final function with id " + baseClassFunction.getId());
                        baseClassFunction.setOverridden();
                    }
                }
            }
        }

        if (hasRepeatedFunctions(classEnvironment.getBaseClasses(), new ArrayList<>(classEnvironment.getFunctions()))) {
            throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " has repeated functions");
        }

        if (!classEnvironment.getModifiers().contains(AddonModifiers.ABSTRACT())) {
            for (ClassEnvironment baseClass : classEnvironment.getBaseClasses()) {
                for (FunctionValue functionValue : getFinalFunctions(baseClass)) {
                    if (functionValue.getModifiers().contains(AddonModifiers.ABSTRACT())) {
                        throw new InvalidSyntaxException("Abstract function with id " + functionValue.getId() + " in class with id " + classEnvironment.getId() + " hasn't been initialized");
                    }
                }
            }
        }

        return classEnvironment;
    }

    public static RuntimeValue<?> callFunction(RuntimeContext context, Environment callEnvironment, FunctionValue functionValue, List<RuntimeValue<?>> args) {
        if (functionValue.getArgs().size() != args.size()) {
            throw new InvalidCallException("Expected " + functionValue.getArgs().size() + " args but found " + args.size());
        }

        FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                functionValue.getParentEnvironment(),
                functionValue.getModifiers().contains(AddonModifiers.SHARED()));

        RuntimeValue<?> result = functionValue.run(context, functionEnvironment, callEnvironment, args);
        if (result != null) result = result.getFinalRuntimeValue();

        return checkReturnValue(
                result,
                functionValue.getReturnDataType(),
                functionValue.getId(),
                functionValue instanceof NativeFunctionValue,
                functionEnvironment.getFileEnvironment());
    }
}
