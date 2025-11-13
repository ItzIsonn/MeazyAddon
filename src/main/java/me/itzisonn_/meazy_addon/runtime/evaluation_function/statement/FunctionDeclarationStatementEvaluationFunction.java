package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.operator.AddonOperators;
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
            if (classValue == null) {
                throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.class.doesnt_exist", functionDeclarationStatement.getClassId()));
            }

            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.class.cant_extend", functionDeclarationStatement.getClassId()));
            }

            if (!EvaluationHelper.extensionFunctions.contains(functionDeclarationStatement)) {
                EvaluationHelper.extensionFunctions.add(functionDeclarationStatement);
            }
            return null;
        }

        if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_use_statement", "function_declaration"));
        }

        for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
            if (!modifier.canUse(functionDeclarationStatement, context, environment)) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_use_modifier", modifier.getId()));
            }
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
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.function.operator.outside_class", functionValue.getId()));
            }

            Operator operator = AddonOperators.parseById(functionValue.getId());
            if (operator == null) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.function.operator.doesnt_exist", functionValue.getId()));
            }

            int args = operator.getOperatorType() == OperatorType.INFIX ? 1 : 0;
            if (functionValue.getParameters().size() != args) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.function.operator.parameters_dont_match", functionValue.getId(), args));
            }

            if (functionValue.getReturnDataType() == null) {
                throw new EvaluationException(Text.translatable("meazy_addon:runtime.function.operator.no_return_value"));
            }

            classEnvironment.declareOperatorFunction(functionValue);
        }
        else functionDeclarationEnvironment.declareFunction(functionValue);

        return null;
    }
}
