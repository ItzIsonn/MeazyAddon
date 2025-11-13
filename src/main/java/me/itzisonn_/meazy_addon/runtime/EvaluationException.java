package me.itzisonn_.meazy_addon.runtime;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationFunction;

/**
 * Is thrown by {@link EvaluationFunction}
 */
public class EvaluationException extends TextException {
    /**
     * @param text Text
     */
    public EvaluationException(Text text) {
        super(text);
    }
}
