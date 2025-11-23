package me.itzisonn_.meazy_addon.runtime;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationFunction;

/**
 * Is thrown when {@link EvaluationFunction} can't evaluate expression because of unknown operator
 */
public class UnsupportedOperatorException extends TextException {
    /**
     * Supers message in format 'Can't evaluate expression with operator {@code operator}'
     * @param operator Operator
     */
    public UnsupportedOperatorException(String operator) {
        super(Text.translatable("meazy_addon:runtime.cant_evaluate_with_operator", operator));
    }
}
