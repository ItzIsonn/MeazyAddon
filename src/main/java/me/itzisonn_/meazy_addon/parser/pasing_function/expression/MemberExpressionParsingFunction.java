package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.CallExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class MemberExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public MemberExpressionParsingFunction() {
        super("member_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Expression object = parser.parseAfter(AddonMain.getIdentifier("member_expression"), Expression.class);

        while (AddonTokenTypeSets.MEMBER_ACCESS().contains(parser.getCurrent().getType())) {
            boolean isNullSafe = parser.getCurrentAndNext().getType().equals(AddonTokenTypes.QUESTION_DOT());
            Expression member = parser.parseAfter(AddonMain.getIdentifier("member_expression"), Expression.class);

            if (!(member instanceof Identifier) && !(member instanceof CallExpression)) {
                throw new UnexpectedTokenException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.exception.member_expression"));
            }

            object = new MemberExpression(object, member, isNullSafe);
        }

        return object;
    }
}
