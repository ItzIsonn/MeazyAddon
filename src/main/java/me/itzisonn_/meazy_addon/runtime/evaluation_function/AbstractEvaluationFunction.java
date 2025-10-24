package me.itzisonn_.meazy_addon.runtime.evaluation_function;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationFunction;

@Getter
public abstract class AbstractEvaluationFunction<T extends Statement> implements EvaluationFunction<T> {
    private final String id;

    protected AbstractEvaluationFunction(String id) {
        this.id = id;
    }
}
