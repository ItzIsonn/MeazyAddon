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

public class ComparisonExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public ComparisonExpressionParsingFunction() {
        super("comparison_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Expression left = parser.parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);

        TokenType current = parser.getCurrent().getType();
        while (current.equals(AddonTokenTypes.EQUALS()) || current.equals(AddonTokenTypes.NOT_EQUALS()) || current.equals(AddonTokenTypes.GREATER()) ||
                current.equals(AddonTokenTypes.GREATER_OR_EQUALS()) || current.equals(AddonTokenTypes.LESS()) || current.equals(AddonTokenTypes.LESS_OR_EQUALS())) {
            String operator = parser.getCurrentAndNext().getValue();
            Expression right = parser.parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);
            left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

            current = parser.getCurrent().getType();
        }

        return left;
    }
}
