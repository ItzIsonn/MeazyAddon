package me.itzisonn_.meazy_addon.runtime.evaluation_function.statement;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy_addon.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.EvaluationHelper;
import me.itzisonn_.meazy_addon.runtime.value.BaseClassIdValue;

import java.util.List;
import java.util.stream.Collectors;

public class BaseCallStatementEvaluationFunction extends AbstractEvaluationFunction<BaseCallStatement> {
    public BaseCallStatementEvaluationFunction() {
        super("base_call_statement");
    }

    @Override
    public RuntimeValue<?> evaluate(BaseCallStatement baseCallStatement, RuntimeContext context, Environment environment, Object... extra) {
        ClassEnvironment classEnvironment;
        if (extra.length == 1 && extra[0] instanceof ClassEnvironment env) classEnvironment = env;
        else throw new InvalidSyntaxException("Unknown error occurred");

        if (!(environment instanceof ConstructorEnvironment constructorEnvironment)) {
            throw new InvalidSyntaxException("Can't use BaseCallStatement in this environment");
        }

        Interpreter interpreter = context.getInterpreter();
        ClassValue baseClassValue = environment.getFileEnvironment().getClass(baseCallStatement.getId());
        List<RuntimeValue<?>> args = baseCallStatement.getArgs().stream().map(expression -> interpreter.evaluate(expression, environment)).collect(Collectors.toList());

        classEnvironment.addBaseClass(EvaluationHelper.initClassEnvironment(context, baseClassValue, constructorEnvironment, args));
        return new BaseClassIdValue(baseCallStatement.getId());
    }
}
