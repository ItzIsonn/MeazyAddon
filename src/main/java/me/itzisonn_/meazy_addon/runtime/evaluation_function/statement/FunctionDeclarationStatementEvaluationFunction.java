package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.RuntimeFunctionValueImpl;

public class FunctionDeclarationStatementEvaluationFunction extends AbstractEvaluationFunction<FunctionDeclarationStatement> {
    public FunctionDeclarationStatementEvaluationFunction() {
        super("function_declaration_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(FunctionDeclarationStatement functionDeclarationStatement, RuntimeContext context, Environment environment, Object... extra) {
        if (functionDeclarationStatement.getClassId() != null) {
            ClassValue classValue = environment.getFileEnvironment().getClass(functionDeclarationStatement.getClassId());
            if (classValue == null) throw new InvalidIdentifierException("Can't find class with id " + functionDeclarationStatement.getClassId());
            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidIdentifierException("Can't extend final class with id " + functionDeclarationStatement.getClassId());
            if (!EvaluationHelper.extensionFunctions.contains(functionDeclarationStatement)) EvaluationHelper.extensionFunctions.add(functionDeclarationStatement);
            return null;
        }
        if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
            throw new InvalidSyntaxException("Can't declare function in this environment");
        }

        for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
            if (!modifier.canUse(functionDeclarationStatement, context, environment)) throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
        }

        FunctionValue functionValue = new RuntimeFunctionValueImpl(
                functionDeclarationStatement.getId(),
                functionDeclarationStatement.getParameters(),
                functionDeclarationStatement.getBody(),
                functionDeclarationStatement.getReturnDataType(),
                functionDeclarationEnvironment,
                functionDeclarationStatement.getModifiers());

        if (functionDeclarationStatement.getModifiers().contains(AddonModifiers.OPERATOR())) {
            if (!(environment instanceof ClassEnvironment classEnvironment)) {
                throw new InvalidSyntaxException("Can't declare operator function not inside a class");
            }

            Operator operator = AddonOperators.parseById(functionValue.getId());
            if (operator == null) {
                throw new InvalidSyntaxException("Can't declare operator function because operator " + functionValue.getId() + " doesn't exist");
            }

            int args = operator.getOperatorType() == OperatorType.INFIX ? 1 : 0;
            if (functionValue.getParameters().size() != args) {
                throw new InvalidSyntaxException("Function for operator " + functionValue.getId() + " must have " + args + " args");
            }

            if (functionValue.getReturnDataType() == null) {
                throw new InvalidSyntaxException("Operator function must return value");
            }

            classEnvironment.declareOperatorFunction(functionValue);
        }
        else functionDeclarationEnvironment.declareFunction(functionValue);

        return null;
    }
}
