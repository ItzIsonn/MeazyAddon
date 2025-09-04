package me.itzisonn_.meazy_addon.parser.ast.expression.literal;

import me.itzisonn_.meazy.parser.ast.expression.Expression;

public class ThisLiteral implements Expression {
    @Override
    public String toCodeString() {
        return "this";
    }
}
