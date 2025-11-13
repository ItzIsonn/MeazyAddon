package me.itzisonn_.meazy_addon.parser;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

public class InvalidStatementException extends TextException {
    public InvalidStatementException(int lineNumber, Text text) {
        super(Text.translatable("meazy:parser.invalid_statement", lineNumber, text));
    }
}
