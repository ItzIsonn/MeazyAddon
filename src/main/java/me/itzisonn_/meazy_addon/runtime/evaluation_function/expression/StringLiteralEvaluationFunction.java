package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.native_class.primitive.StringClassNative;

public class StringLiteralEvaluationFunction extends AbstractEvaluationFunction<StringLiteral> {
    public StringLiteralEvaluationFunction() {
        super("string_literal");
    }

    @Override
    public RuntimeValue<?> evaluate(StringLiteral stringLiteral, RuntimeContext context, Environment environment, Object... extra) {
        return StringClassNative.newString(environment, stringLiteral.getValue());
    }
}
