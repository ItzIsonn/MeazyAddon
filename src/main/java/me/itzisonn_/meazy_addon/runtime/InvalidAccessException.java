package me.itzisonn_.meazy_addon.runtime;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.interpreter.EvaluationFunction;

/**
 * Is thrown when {@link EvaluationFunction} can't access member
 */
public class InvalidAccessException extends TextException {
    /**
     * @param text Text
     */
    public InvalidAccessException(Text text) {
        super(text);
    }
}
