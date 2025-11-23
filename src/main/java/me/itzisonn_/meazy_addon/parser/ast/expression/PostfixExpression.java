package me.itzisonn_.meazy_addon.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

@Getter
public class PostfixExpression implements Expression {
    private final Expression value;
    private final String operator;

    public PostfixExpression(Expression value, String operator) {
        if (value == null) throw new NullPointerException("Value can't be null");
        if (operator == null) throw new NullPointerException("Operator can't be null");

        this.value = value;
        this.operator = operator;
    }

    @Override
    public String toCodeString() {
        return value.toCodeString() + operator;
    }
}
