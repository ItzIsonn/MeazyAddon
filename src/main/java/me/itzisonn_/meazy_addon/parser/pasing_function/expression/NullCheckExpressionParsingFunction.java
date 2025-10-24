package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.NullCheckExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class NullCheckExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public NullCheckExpressionParsingFunction() {
        super("null_check_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Expression checkExpression = parser.parseAfter(AddonMain.getIdentifier("null_check_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.QUESTION_COLON())) {
            parser.getCurrentAndNext();
            Expression nullExpression = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            return new NullCheckExpression(checkExpression, nullExpression);
        }

        return checkExpression;
    }
}
