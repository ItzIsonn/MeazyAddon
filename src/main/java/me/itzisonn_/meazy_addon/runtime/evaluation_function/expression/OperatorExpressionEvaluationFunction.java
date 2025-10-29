package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.UnsupportedOperatorException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.registry.RegistryEntry;

import java.util.List;

public class OperatorExpressionEvaluationFunction extends AbstractEvaluationFunction<OperatorExpression> {
    public OperatorExpressionEvaluationFunction() {
        super("operator_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(OperatorExpression operatorExpression, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();
        RuntimeValue<?> left = interpreter.evaluate(operatorExpression.getLeft(), environment).getFinalRuntimeValue();
        RuntimeValue<?> right = operatorExpression.getRight() != null ? interpreter.evaluate(operatorExpression.getRight(), environment).getFinalRuntimeValue() : null;

        if (operatorExpression.getType() == OperatorType.INFIX && (left == null || right == null)) {
            throw new RuntimeException("Infix expression must contain both parts");
        }
        if (operatorExpression.getType() != OperatorType.INFIX) {
            if (left == null) {
                if (right == null) throw new RuntimeException("Prefix and suffix expression must contain only one part");
                left = right;
                right = null;
            }
            else if (right != null) throw new RuntimeException("Prefix and suffix expression must contain only one part");
        }

        ClassValue classValue;
        List<RuntimeValue<?>> args;
        if (left instanceof ClassValue leftClassValue) {
            classValue = leftClassValue;
            args = right == null ? List.of() : List.of(right);
        }
        else if (right instanceof ClassValue rightClassValue) {
            classValue = rightClassValue;
            args = List.of(left);
        }
        else {
            classValue = null;
            args = null;
        }
        if (classValue != null) {
            RegistryEntry<Operator> entry = Registries.OPERATORS.getEntry(operatorExpression.getOperator());
            if (entry != null) {
                FunctionValue operatorFunction = classValue.getEnvironment().getOperatorFunction(entry.getIdentifier().getId(), args);
                if (operatorFunction != null) return EvaluationHelper.callFunction(context, environment, operatorFunction, args);
            }
        }

        RuntimeValue<?> result = operatorExpression.getOperator().calculate(environment, left, right);
        if (result == null) throw new UnsupportedOperatorException(operatorExpression.getOperator().getSymbol());

        return result;
    }
}
