package me.itzisonn_.meazy_addon.runtime.evaluation_function.expression;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy_addon.runtime.evaluation_function.AbstractEvaluationFunction;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.MapClassNative;

import java.util.HashMap;
import java.util.Map;

public class MapCreationExpressionEvaluationFunction extends AbstractEvaluationFunction<MapCreationExpression> {
    public MapCreationExpressionEvaluationFunction() {
        super("map_creation_expression");
    }

    @Override
    public RuntimeValue<?> evaluate(MapCreationExpression mapCreationExpression, RuntimeContext context, Environment environment, Object... extra) {
        Map<RuntimeValue<?>, RuntimeValue<?>> map = new HashMap<>();
        Interpreter interpreter = context.getInterpreter();

        for (Expression key : mapCreationExpression.getMap().keySet()) {
            Expression value = mapCreationExpression.getMap().get(key);
            map.put(interpreter.evaluate(key, environment), interpreter.evaluate(value, environment));
        }

        return MapClassNative.newMap(environment, context, map);
    }
}
