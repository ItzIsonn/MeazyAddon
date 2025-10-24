package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class LogicalExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public LogicalExpressionParsingFunction() {
        super("logical_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Expression left = parser.parseAfter(AddonMain.getIdentifier("logical_expression"), Expression.class);

        TokenType current = parser.getCurrent().getType();
        while (current.equals(AddonTokenTypes.AND()) || current.equals(AddonTokenTypes.OR())) {
            String operator = parser.getCurrentAndNext().getValue();
            Expression right = parser.parseAfter(AddonMain.getIdentifier("logical_expression"), Expression.class);
            left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

            current = parser.getCurrent().getType();
        }

        return left;
    }
}
