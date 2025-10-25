package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.native_class.collection.ListClassNative;

import java.util.List;
import java.util.stream.Collectors;

public class ListCreationExpressionEvaluationFunction extends AbstractEvaluationFunction<ListCreationExpression> {
    public ListCreationExpressionEvaluationFunction() {
        super("list_creation_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(ListCreationExpression listCreationExpression, RuntimeContext context, Environment environment, Object... extra) {
        Interpreter interpreter = context.getInterpreter();
        List<RuntimeValue<?>> list = listCreationExpression.getList().stream().map(expression -> interpreter.evaluate(expression, environment)).collect(Collectors.toList());
        return ListClassNative.newList(environment, context, list);
    }
}
