package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.CallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.InvalidCallException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;

import java.util.List;
import java.util.stream.Collectors;

public class CallExpressionEvaluationFunction extends AbstractEvaluationFunction<CallExpression> {
    public CallExpressionEvaluationFunction() {
        super("call_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(CallExpression callExpression, RuntimeContext context, Environment environment, Object... extra) {
        Environment callEnvironment;
        if (extra.length == 0) callEnvironment = environment;
        else if (extra[0] instanceof Environment extraEnv) callEnvironment = extraEnv;
        else callEnvironment = environment;

        Interpreter interpreter = context.getInterpreter();
        List<RuntimeValue<?>> args = callExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, callEnvironment)).collect(Collectors.toList());

        if (callExpression.getCaller() instanceof FunctionIdentifier) {
            RuntimeValue<?> function = interpreter.evaluate(callExpression.getCaller(), environment, callEnvironment, args);
            if (!(function instanceof FunctionValue functionValue)) {
                throw new InvalidCallException(Text.translatable("meazy_addon:runtime.function.cant_call", function.getClass().getName()));
            }

            return EvaluationHelper.callFunction(context, callEnvironment, functionValue, args);
        }

        if (callExpression.getCaller() instanceof ClassIdentifier) {
            RuntimeValue<?> rawClass = interpreter.evaluate(callExpression.getCaller(), environment);

            if (!(rawClass instanceof ClassValue classValue)) throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.instance.not_class", rawClass.getClass().getName()));
            if (classValue.getModifiers().contains(AddonModifiers.ABSTRACT())) throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.instance.abstract", classValue.getId()));
            if (classValue.getModifiers().contains(AddonModifiers.ENUM())) throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.instance.enum", classValue.getId()));

            return EvaluationHelper.callClassValue(context, classValue, callEnvironment, args);
        }

        throw new InvalidCallException(Text.translatable("meazy_addon:runtime.unknown_call_identifier", callExpression.getCaller().getClass().getName()));
    }
}
