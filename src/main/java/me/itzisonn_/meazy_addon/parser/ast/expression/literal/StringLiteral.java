package me.itzisonn_.meazy_addon.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Expression;

@Getter
public class StringLiteral implements Expression {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toCodeString() {
        return "\"" + value + "\"";
    }
}
