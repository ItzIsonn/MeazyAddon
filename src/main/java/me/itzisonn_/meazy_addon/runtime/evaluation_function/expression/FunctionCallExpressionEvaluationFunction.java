package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
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
        Environment extraEnvironment;
        if (extra.length == 0) extraEnvironment = environment;
        else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
        else extraEnvironment = environment;

        Interpreter interpreter = context.getInterpreter();
        List<RuntimeValue<?>> args = functionCallExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());

        RuntimeValue<?> function = interpreter.evaluate(functionCallExpression.getCaller(), environment, extraEnvironment, args);
        if (!(function instanceof FunctionValue functionValue)) {
            throw new InvalidCallException("Can't call " + function.getValue() + " because it's not a function");
        }

        return EvaluationHelper.callFunction(context, extraEnvironment, functionValue, args);
    }
}
