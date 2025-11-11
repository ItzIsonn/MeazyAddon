package me.itzisonn_.meazy_addon.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements ClassEnvironment {
    @Getter
    protected final String id;
    protected final Set<VariableValue> variables;
    protected final Set<ConstructorValue> constructors;
    protected final Set<ClassEnvironment> baseClasses;
    protected final Set<Modifier> modifiers;
    protected final Set<FunctionValue> operatorFunctions;

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, boolean isShared, String id, Set<Modifier> modifiers) {
        super(parent, isShared);
        this.id = id;
        variables = new HashSet<>();
        constructors = new HashSet<>();
        baseClasses = new HashSet<>();
        this.modifiers = modifiers;
        operatorFunctions = new HashSet<>();
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, boolean isShared, String id) {
        this(parent, isShared, id, new HashSet<>());
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, String id, Set<Modifier> modifiers) {
        this(parent, false, id, modifiers);
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, String id) {
        this(parent, false, id);
    }



    @Override
    public void declareVariable(VariableValue value) {
        if (getVariable(value.getId()) != null) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.variable.already_exists", value.getId()));
        }
        variables.add(value);
    }

    @Override
    public VariableValue getVariable(String id) {
        VariableValue variableValue = ClassEnvironment.super.getVariable(id);
        if (variableValue != null) return variableValue;

        for (ClassEnvironment baseClass : baseClasses) {
            VariableValue variable = baseClass.getVariable(id);
            if (variable != null) return variable;
        }

        return null;
    }

    @Override
    public VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) {
        if (ClassEnvironment.super.getVariable(id) != null) return this;

        for (ClassEnvironment baseClass : baseClasses) {
            VariableValue variable = baseClass.getVariable(id);
            if (variable != null) return baseClass;
        }

        return super.getVariableDeclarationEnvironment(id);
    }

    @Override
    public Set<VariableValue> getVariables() {
        return new HashSet<>(variables);
    }



    @Override
    public void declareOperatorFunction(FunctionValue value) {
        List<ParameterExpression> parameters = value.getParameters();

        main:
        for (FunctionValue functionValue : operatorFunctions) {
            if (functionValue.getId().equals(value.getId())) {
                List<ParameterExpression> otherParameters = functionValue.getParameters();
                if (parameters.size() != otherParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
                }

                throw new EvaluationException(Text.translatable("meazy_addon:runtime.function.operator.already_exists", value.getId()));
            }
        }

        operatorFunctions.add(value);
    }

    @Override
    public Set<FunctionValue> getOperatorFunctions() {
        return new HashSet<>(operatorFunctions);
    }



    @Override
    public FunctionValue getFunction(String id, List<RuntimeValue<?>> args) {
        FunctionValue functionValue = super.getFunction(id, args);
        if (functionValue != null) return functionValue;

        for (ClassEnvironment baseClass : baseClasses) {
            FunctionValue function = baseClass.getFunction(id, args);
            if (function != null) return function;
        }

        return null;
    }

    @Override
    public FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) {
        if (super.getFunction(id, args) != null) return this;

        for (ClassEnvironment baseClass : baseClasses) {
            FunctionValue function = baseClass.getFunction(id, args);
            if (function != null) return baseClass;
        }

        return super.getFunctionDeclarationEnvironment(id, args);
    }



    @Override
    public void declareConstructor(ConstructorValue value) {
        List<ParameterExpression> parameters = value.getParameters();

        main:
        for (ConstructorValue constructorValue : constructors) {
            List<ParameterExpression> otherParameters = constructorValue.getParameters();
            if (parameters.size() != otherParameters.size()) continue;

            for (int i = 0; i < parameters.size(); i++) {
                if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
            }

            throw new EvaluationException(Text.translatable("meazy_addon:runtime.constructor.already_exists"));
        }

        constructors.add(value);
    }

    @Override
    public Set<ConstructorValue> getConstructors() {
        return new HashSet<>(constructors);
    }



    @Override
    public void addBaseClass(ClassEnvironment classEnvironment) {
        baseClasses.add(classEnvironment);
    }

    @Override
    public Set<ClassEnvironment> getBaseClasses() {
        return new HashSet<>(baseClasses);
    }



    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}