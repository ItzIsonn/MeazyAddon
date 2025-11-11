package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;

public class IsExpressionEvaluationFunction extends AbstractEvaluationFunction<IsExpression> {
    public IsExpressionEvaluationFunction() {
        super("is_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(IsExpression isExpression, RuntimeContext context, Environment environment, Object... extra) {
        RuntimeValue<?> value = context.getInterpreter().evaluate(isExpression.getValue(), environment).getFinalRuntimeValue();

        ClassValue classValue = environment.getFileEnvironment().getClass(isExpression.getDataType());
        if (classValue == null) throw new InvalidIdentifierException(Text.translatable("meazy_addon:runtime.class.doesnt_exist", isExpression.getDataType()));

        if (isExpression.isLike()) return BooleanValue.of(classValue.isLikeMatches(environment.getFileEnvironment(), value.getFinalRuntimeValue()));
        return BooleanValue.of(classValue.isMatches(value.getFinalRuntimeValue()));
    }
}
