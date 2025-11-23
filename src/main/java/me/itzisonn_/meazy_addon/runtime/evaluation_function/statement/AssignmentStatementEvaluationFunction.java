package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.statement.AssignmentStatement;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;

public class AssignmentStatementEvaluationFunction extends AbstractEvaluationFunction<AssignmentStatement> {
    public AssignmentStatementEvaluationFunction() {
        super("assignment_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(AssignmentStatement assignmentStatement, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();

        if (assignmentStatement.getId() instanceof VariableIdentifier variableIdentifier) {
            RuntimeValue<?> value = interpreter.evaluate(assignmentStatement.getValue(), environment).getFinalRuntimeValue();
            environment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
            return null;
        }

        if (assignmentStatement.getId() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = interpreter.evaluate(memberExpression, environment);
            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> value = interpreter.evaluate(assignmentStatement.getValue(), environment).getFinalRuntimeValue();
                variableValue.setValue(value);
                return null;
            }
            throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_assign_value", memberExpressionValue));
        }

        throw new EvaluationException(Text.translatable("meazy_addon:runtime.cant_assign_value", assignmentStatement.getId().getClass().getName()));
    }
}
