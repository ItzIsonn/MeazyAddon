package me.itzisonn_.meazy_addon.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.ParenthesisExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.parser.AddonOperators;

@Getter
public class OperatorExpression implements Expression, ParenthesisExpression {
    private final Expression left;
    private final Expression right;
    private final Operator operator;

    public OperatorExpression(Expression left, Expression right, Operator operator) {
        this.left = left;
        this.right = right;

        if (operator == null) throw new NullPointerException("Operator can't be null");
        this.operator = operator;
    }

    public OperatorExpression(Expression left, Expression right, String operator, OperatorType operatorType) {
        this(left, right, AddonOperators.parse(operator, operatorType));
    }

    public OperatorType getType() {
        return operator.getOperatorType();
    }

    @Override
    public String toCodeString() {
        return switch (operator.getOperatorType()) {
            case PREFIX -> operator.getSymbol() + left.toCodeString();
            case INFIX -> left.toCodeString() + " " + operator.getSymbol() + " " + right.toCodeString();
            case SUFFIX -> left.toCodeString() + operator.getSymbol();
        };
    }
}
