package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class AdditionExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public AdditionExpressionParsingFunction() {
        super("addition_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Expression left = parser.parseAfter(AddonMain.getIdentifier("addition_expression"), Expression.class);

        while (parser.getCurrent().getType().equals(AddonTokenTypes.PLUS()) || parser.getCurrent().getType().equals(AddonTokenTypes.MINUS())) {
            String operator = parser.getCurrentAndNext().getValue();
            Expression right = parser.parse(AddonMain.getIdentifier("addition_expression"), Expression.class);
            left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
        }

        return left;
    }
}
