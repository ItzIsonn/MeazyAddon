package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.PostfixExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.parser.operator.AddonOperators;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.number.IntValue;

public class PostfixExpressionEvaluationFunction extends AbstractEvaluationFunction<PostfixExpression> {
    public PostfixExpressionEvaluationFunction() {
        super("postfix_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(PostfixExpression postfixExpression, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();

        Operator operator = null;
        if (postfixExpression.getOperator().equals("++")) operator = AddonOperators.parse("+", OperatorType.INFIX);
        else if (postfixExpression.getOperator().equals("--")) operator = AddonOperators.parse("-", OperatorType.INFIX);

        if (operator == null) throw new EvaluationException(Text.translatable("meazy_addon:runtime.unknown_postfix_operator"));

        if (postfixExpression.getValue() instanceof VariableIdentifier variableIdentifier) {
            RuntimeValue<?> variableValue = interpreter.evaluate(variableIdentifier, environment).getFinalRuntimeValue();
            RuntimeValue<?> newValue = operator.calculate(environment, variableValue, new IntValue(1));

            environment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), newValue);
            return variableValue;
        }

        if (postfixExpression.getValue() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = interpreter.evaluate(memberExpression, environment);

            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> newValue = operator.calculate(environment, variableValue.getValue(), new IntValue(1));
                variableValue.setValue(newValue);
                return newValue;
            }

            throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_assign_value", memberExpressionValue.getClass().getName()));
        }

        throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_assign_value", postfixExpression.getValue().getClass().getName()));
    }
}
