package me.itzisonn_.meazy_addon.runtime.evaluation_function;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;

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
            throw new RuntimeException("Can't assign value to not variable " + memberExpressionValue);
        }

        throw new RuntimeException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    public static boolean parseCondition(RuntimeContext context, Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = context.getInterpreter().evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, FileEnvironment fileEnvironment) {
        if (returnValue == null) {
            if (returnDataType != null) {
                throw new RuntimeException("Didn't find return value but function with id " + functionId + " must return value");
            }
            return null;
        }
        if (returnDataType == null) {
            throw new RuntimeException("Found return value but function with id " + functionId + " must return nothing");
        }

        if (!returnDataType.isMatches(returnValue, fileEnvironment)) {
            throw new RuntimeException("Returned value's data type is different from specified (" + returnDataType.getId() + ")");
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
        return classValue.newInstance(classEnvironment);
    }

    public static ClassValue callUninitializedClassValue(RuntimeContext context, ClassValue classValue, Environment callEnvironment) {
        return callClassValue(context, classValue, callEnvironment, null);
    }

    public static ClassEnvironment initClassEnvironment(RuntimeContext context, ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getFileEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);
        Interpreter interpreter = context.getInterpreter();

        classValue.setupEnvironment(context, classEnvironment);

        for (FunctionDeclarationStatement functionDeclarationStatement : extensionFunctions) {
            if (functionDeclarationStatement.getClassId().equals(classValue.getId())) {
                interpreter.evaluate(new FunctionDeclarationStatement(
                        functionDeclarationStatement.getModifiers(),
                        functionDeclarationStatement.getId(),
                        null,
                        functionDeclarationStatement.getParameters(),
                        functionDeclarationStatement.getBody(),
                        functionDeclarationStatement.getReturnDataType()
                ), classEnvironment);
            }
        }

        Set<String> calledBaseClasses = new HashSet<>();

        if (args != null) {
            if (classEnvironment.hasConstructor()) {
                ConstructorValue constructorValue = classEnvironment.getConstructor(args);
                if (constructorValue == null) throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");

                if (!constructorValue.isAccessible(callEnvironment)) {
                    throw new InvalidAccessException("Can't access requested constructor because of its modifiers");
                }

                Set<String> constructorCalled = constructorValue.run(context, constructorEnvironment, callEnvironment, args);
                calledBaseClasses.addAll(constructorCalled);
            }
            else if (!args.isEmpty()) {
                throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");
            }

            for (VariableValue variableValue : classEnvironment.getVariables()) {
                if (variableValue.isConstant() && variableValue.getValue() == null) {
                    throw new RuntimeException("Empty constant variable with id " + variableValue.getId() + " hasn't been initialized");
                }
            }
        }

        for (String baseClass : calledBaseClasses) {
            if (!classValue.getBaseClasses().contains(baseClass)) throw new RuntimeException("Can't call base class " + baseClass + " because it's not base class of class " + classValue.getId());
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
                        throw new RuntimeException("Abstract function with id " + functionValue.getId() + " in class with id " + classEnvironment.getId() + " hasn't been initialized");
                    }
                }
            }
        }

        return classEnvironment;
    }

    public static RuntimeValue<?> callFunction(RuntimeContext context, Environment callEnvironment, FunctionValue functionValue, List<RuntimeValue<?>> args) {
        if (functionValue.getParameters().size() != args.size()) {
            throw new InvalidCallException("Expected " + functionValue.getParameters().size() + " args but found " + args.size());
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
                functionEnvironment.getFileEnvironment());
    }
}
