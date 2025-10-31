package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
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

        while (AddonTokenTypeSets.COMPARISON().contains(parser.getCurrent().getType())) {
            String operator = parser.getCurrentAndNext().getValue();
            Expression right = parser.parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);
            left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
        }

        return left;
    }
}
