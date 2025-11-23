package me.itzisonn_.meazy_addon.runtime.evaluation_function;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.InvalidAccessException;
import me.itzisonn_.meazy_addon.runtime.InvalidCallException;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;

import java.util.*;

public final class EvaluationHelper {
    public static final List<FunctionDeclarationStatement> extensionFunctions = new ArrayList<>();

    private EvaluationHelper() {}



    public static boolean parseCondition(RuntimeContext context, Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = context.getInterpreter().evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.condition_must_be_boolean"));
        }

        return booleanValue.getValue();
    }

    public static boolean hasRepeatedBaseClasses(Set<String> baseClassesList, List<String> baseClasses, FileEnvironment fileEnvironment) {
        for (String baseClass : baseClassesList) {
            if (baseClasses.contains(baseClass)) {
                return true;
            }
            baseClasses.add(baseClass);

            ClassValue classValue = fileEnvironment.getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.class.doesnt_exist", baseClass));
            }
            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) {
                throw new InvalidAccessException(Text.translatable("meazy_addon:runtime.class.cant_inherit", baseClass));
            }

            boolean check = hasRepeatedBaseClasses(classValue.getBaseClasses(), baseClasses, fileEnvironment);
            if (check) return true;
        }

        return false;
    }

    public static boolean hasRepeatedVariables(Set<String> baseClassesList, List<String> variables, FileEnvironment fileEnvironment) {
        for (String baseClass : baseClassesList) {
            ClassValue classValue = fileEnvironment.getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.class.doesnt_exist", baseClass));
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
                if (constructorValue == null) {
                    throw new InvalidCallException(Text.translatable("meazy_addon:runtime.constructor.doesnt_exist", classValue.getId()));
                }

                if (!constructorValue.isAccessible(callEnvironment)) {
                    throw new InvalidCallException(Text.translatable("meazy_addon:runtime.constructor.cant_access", classValue.getId()));
                }

                Set<String> constructorCalled = constructorValue.run(context, constructorEnvironment, callEnvironment, args);
                calledBaseClasses.addAll(constructorCalled);
            }
            else if (!args.isEmpty()) {
                throw new InvalidCallException(Text.translatable("meazy_addon:runtime.constructor.doesnt_exist", classValue.getId()));
            }

            for (VariableValue variableValue : classEnvironment.getVariables()) {
                if (variableValue.isConstant() && variableValue.getValue() == null) {
                    throw new InvalidCallException(Text.translatable("meazy_addon:runtime.variable.not_initialized", variableValue.getId()));
                }
            }
        }

        for (String baseClass : calledBaseClasses) {
            if (!classValue.getBaseClasses().contains(baseClass)) {
                throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.cant_call_base", baseClass, classValue.getId()));
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
                        if (baseClassFunction.getModifiers().contains(AddonModifiers.FINAL())) {
                            throw new InvalidAccessException(Text.translatable("meazy_addon:runtime.function.cant_override", baseClassFunction.getId()));
                        }
                        baseClassFunction.setOverridden();
                    }
                }
            }
        }

        if (hasRepeatedFunctions(classEnvironment.getBaseClasses(), new ArrayList<>(classEnvironment.getFunctions()))) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.class.repeated.functions", classValue.getId()));
        }

        if (!classEnvironment.getModifiers().contains(AddonModifiers.ABSTRACT())) {
            for (ClassEnvironment baseClass : classEnvironment.getBaseClasses()) {
                for (FunctionValue functionValue : getFinalFunctions(baseClass)) {
                    if (functionValue.getModifiers().contains(AddonModifiers.ABSTRACT())) {
                        throw new EvaluationException(Text.translatable("meazy_addon:runtime.function.abstract_not_initialized", functionValue.getId(), classValue.getId()));
                    }
                }
            }
        }

        return classEnvironment;
    }

    public static RuntimeValue<?> callFunction(RuntimeContext context, Environment callEnvironment, FunctionValue functionValue, List<RuntimeValue<?>> args) {
        if (functionValue.getParameters().size() != args.size()) {
            throw new InvalidCallException(Text.translatable("meazy_addon:runtime.function.parameters_dont_match", functionValue.getId(), functionValue.getParameters().size(), args.size()));
        }

        FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                functionValue.getParentEnvironment(),
                functionValue.getModifiers().contains(AddonModifiers.SHARED()));

        RuntimeValue<?> result = functionValue.run(context, functionEnvironment, callEnvironment, args);
        if (result != null) result = result.getFinalRuntimeValue();

        if (result == null) {
            if (functionValue.getReturnDataType() != null) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.return_value.must_return", functionValue.getId()));
            }
            return null;
        }
        if (functionValue.getReturnDataType() == null) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.return_value.must_not_return", functionValue.getId()));
        }

        if (!functionValue.getReturnDataType().isMatches(result, functionEnvironment.getFileEnvironment())) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.return_value.different_data_type", functionValue.getId(), functionValue.getReturnDataType().toString()));
        }

        return result;
    }
}
