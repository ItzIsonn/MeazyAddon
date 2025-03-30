package me.itzisonn_.meazy_addon.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy.parser.ast.ParenthesisExpression;

@Getter
public class IsExpression implements Expression, ParenthesisExpression {
    private final Expression value;
    private final String dataType;
    private final boolean isLike;

    public IsExpression(Expression value, String dataType, boolean isLike) {
        this.value = value;
        this.dataType = dataType;
        this.isLike = isLike;
    }

    @Override
    public String toCodeString() {
        String isLikeString = isLike ? "Like" : "";
        return value.toCodeString() + " is" + isLikeString + " " + dataType;
    }
}