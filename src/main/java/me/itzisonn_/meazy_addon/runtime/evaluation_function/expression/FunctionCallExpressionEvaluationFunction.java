package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.runtime.InvalidCallException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionCallExpressionEvaluationFunction extends AbstractEvaluationFunction<FunctionCallExpression> {
    public FunctionCallExpressionEvaluationFunction() {
        super("function_call_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(FunctionCallExpression functionCallExpression, RuntimeContext context, Environment environment, Object... extra) {
        Environment callEnvironment;
        if (extra.length == 0) callEnvironment = environment;
        else if (extra[0] instanceof Environment extraEnv) callEnvironment = extraEnv;
        else callEnvironment = environment;

        Interpreter interpreter = context.getInterpreter();
        List<RuntimeValue<?>> args = functionCallExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, callEnvironment)).collect(Collectors.toList());

        RuntimeValue<?> function = interpreter.evaluate(functionCallExpression.getCaller(), environment, callEnvironment, args);
        if (!(function instanceof FunctionValue functionValue)) {
            throw new InvalidCallException(Text.translatable("meazy_addon:runtime.function.cant_call", function.getClass().getName()));
        }

        return EvaluationHelper.callFunction(context, callEnvironment, functionValue, args);
    }
}
