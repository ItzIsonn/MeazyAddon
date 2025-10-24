package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;

import java.util.stream.Collectors;

public class ThisLiteralEvaluationFunction extends AbstractEvaluationFunction<ThisLiteral> {
    public ThisLiteralEvaluationFunction() {
        super("this_literal");
    }

    @Override
    public RuntimeValue<?> evaluate(ThisLiteral thisLiteral, RuntimeContext context, Environment environment, Object... extra) {
        Environment parent = environment.getParent(env -> env instanceof ClassEnvironment);
        if (!(parent instanceof ClassEnvironment classEnvironment)) throw new RuntimeException("Can't use 'this' keyword not inside a class");
        if (environment.isShared()) throw new RuntimeException("Can't use 'this' keyword inside a shared environment");
        return new NativeClassValueImpl(
                classEnvironment.getBaseClasses().stream().map(ClassEnvironment::getId).collect(Collectors.toSet()),
                classEnvironment);
    }
}
