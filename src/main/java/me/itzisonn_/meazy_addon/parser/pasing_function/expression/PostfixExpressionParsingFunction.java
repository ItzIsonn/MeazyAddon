package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
import me.itzisonn_.meazy_addon.parser.ast.expression.PostfixExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class PostfixExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public PostfixExpressionParsingFunction() {
        super("postfix_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Expression id = parser.parseAfter(AddonMain.getIdentifier("postfix_expression"), Expression.class);

        if (AddonTokenTypeSets.OPERATOR_POSTFIX().contains(parser.getCurrent().getType())) {
            Token token = parser.getCurrentAndNext();
            return new PostfixExpression(id, token.getValue());
        }

        return id;
    }
}
