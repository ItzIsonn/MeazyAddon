package me.itzisonn_.meazy_addon.parser.ast.expression.literal;

import me.itzisonn_.meazy.parser.ast.Expression;

public class NullLiteral implements Expression {
    @Override
    public String toCodeString() {
        return "null";
    }
}
