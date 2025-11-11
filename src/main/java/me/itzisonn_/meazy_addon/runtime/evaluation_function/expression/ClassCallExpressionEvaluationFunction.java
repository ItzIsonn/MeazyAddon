package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;

import java.util.List;
import java.util.stream.Collectors;

public class ClassCallExpressionEvaluationFunction extends AbstractEvaluationFunction<ClassCallExpression> {
    public ClassCallExpressionEvaluationFunction() {
        super("class_call_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(ClassCallExpression classCallExpression, RuntimeContext context, Environment environment, Object... extra) {
        Environment callEnvironment;
        if (extra.length == 0) callEnvironment = environment;
        else if (extra[0] instanceof Environment extraEnv) callEnvironment = extraEnv;
        else callEnvironment = environment;

        Interpreter interpreter = context.getInterpreter();
        List<RuntimeValue<?>> args = classCallExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, callEnvironment)).collect(Collectors.toList());
        RuntimeValue<?> rawClass = interpreter.evaluate(classCallExpression.getCaller(), environment);

        if (!(rawClass instanceof ClassValue classValue)) throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.cant_call", rawClass.getClass().getName()));
        if (classValue.getModifiers().contains(AddonModifiers.ABSTRACT())) throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.instance.abstract", classValue.getId()));
        if (classValue.getModifiers().contains(AddonModifiers.ENUM())) throw new InvalidCallException(Text.translatable("meazy_addon:runtime.class.instance.enum", classValue.getId()));

        return EvaluationHelper.callClassValue(context, classValue, callEnvironment, args);
    }
}
