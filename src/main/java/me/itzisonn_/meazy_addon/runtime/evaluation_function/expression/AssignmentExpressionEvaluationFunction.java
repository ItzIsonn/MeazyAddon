package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;

public class AssignmentExpressionEvaluationFunction extends AbstractEvaluationFunction<AssignmentExpression> {
    public AssignmentExpressionEvaluationFunction() {
        super("assignment_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(AssignmentExpression assignmentExpression, RuntimeContext context, Environment environment, Object... extra) {
        return EvaluationHelper.evaluateAssignmentExpression(context, assignmentExpression, environment);
    }
}
