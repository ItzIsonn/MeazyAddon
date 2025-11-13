package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;

public class MemberExpressionEvaluationFunction extends AbstractEvaluationFunction<MemberExpression> {
    public MemberExpressionEvaluationFunction() {
        super("member_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(MemberExpression memberExpression, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();
        RuntimeValue<?> value = interpreter.evaluate(memberExpression.getObject(), environment).getFinalRuntimeValue();

        if (value instanceof NullValue) {
            if (memberExpression.isNullSafe()) return value;
            else throw new EvaluationException(Text.translatable("meazy_addon:runtime.member.null"));
        }

        if (value instanceof ClassValue classValue) {
            return interpreter.evaluate(memberExpression.getMember(), classValue.getEnvironment(), environment);
        }

        throw new EvaluationException(Text.translatable("meazy_addon:runtime.member.not_class", value));
    }
}
