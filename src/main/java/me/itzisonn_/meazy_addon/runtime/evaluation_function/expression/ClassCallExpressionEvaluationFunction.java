package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
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
        Environment extraEnvironment;
        if (extra.length == 0) extraEnvironment = environment;
        else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
        else extraEnvironment = environment;

        Interpreter interpreter = context.getInterpreter();
        List<RuntimeValue<?>> args = classCallExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());
        RuntimeValue<?> rawClass = interpreter.evaluate(classCallExpression.getCaller(), environment);

        if (!(rawClass instanceof ClassValue classValue)) throw new InvalidCallException("Can't call " + rawClass.getClass().getName() + " because it's not a class");
        if (classValue.getModifiers().contains(AddonModifiers.ABSTRACT())) throw new InvalidCallException("Can't create instance of an abstract class " + classValue.getId());
        if (classValue.getModifiers().contains(AddonModifiers.ENUM())) throw new InvalidCallException("Can't create instance of an enum class " + classValue.getId());

        return EvaluationHelper.callClassValue(context, classValue, extraEnvironment, args);
    }
}
