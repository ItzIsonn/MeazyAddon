package me.itzisonn_.meazy_addon.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy.parser.ast.ParenthesisExpression;

@Getter
public class AssignmentExpression implements Expression, ParenthesisExpression {
    private final Expression id;
    private final Expression value;

    public AssignmentExpression(Expression id, Expression value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toCodeString() {
        return id.toCodeString() + " = " + value.toCodeString();
    }
}
