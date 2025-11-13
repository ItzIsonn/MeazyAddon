package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy_addon.runtime.EvaluationException;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.EmptyClassValueImpl;

import java.util.stream.Collectors;

public class ThisLiteralEvaluationFunction extends AbstractEvaluationFunction<ThisLiteral> {
    public ThisLiteralEvaluationFunction() {
        super("this_literal");
    }

    @Override
    public RuntimeValue<?> evaluate(ThisLiteral thisLiteral, RuntimeContext context, Environment environment, Object... extra) {
        Environment parent = environment.getParent(env -> env instanceof ClassEnvironment);
        if (!(parent instanceof ClassEnvironment classEnvironment)) throw new EvaluationException(Text.translatable("meazy_addon:runtime.this.not_inside_class"));
        if (environment.isShared()) throw new EvaluationException(Text.translatable("meazy_addon:runtime.this.shared"));
        return new EmptyClassValueImpl(classEnvironment.getBaseClasses().stream().map(ClassEnvironment::getId).collect(Collectors.toSet()), classEnvironment);
    }
}
