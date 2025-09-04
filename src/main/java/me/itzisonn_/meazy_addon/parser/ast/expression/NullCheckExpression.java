package me.itzisonn_.meazy_addon.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.ParenthesisExpression;

@Getter
public class NullCheckExpression implements Expression, ParenthesisExpression {
    private final Expression checkExpression;
    private final Expression nullExpression;

    public NullCheckExpression(Expression checkExpression, Expression nullExpression) {
        this.checkExpression = checkExpression;
        this.nullExpression = nullExpression;
    }

    @Override
    public String toCodeString() {
        return checkExpression.toCodeString() + " ?: " + nullExpression.toCodeString();
    }
}
