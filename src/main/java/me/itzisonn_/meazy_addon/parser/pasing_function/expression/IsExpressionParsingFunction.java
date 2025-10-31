package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class IsExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public IsExpressionParsingFunction() {
        super("is_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Expression value = parser.parseAfter(AddonMain.getIdentifier("is_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.IS()) || parser.getCurrent().getType().equals(AddonTokenTypes.IS_LIKE())) {
            boolean isLike = parser.getCurrentAndNext().getType().equals(AddonTokenTypes.IS_LIKE());
            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected.after_keyword", "id", "is")).getValue();
            return new IsExpression(value, id, isLike);
        }

        return value;
    }
}
