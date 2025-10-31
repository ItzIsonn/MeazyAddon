package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class ReturnStatementParsingFunction extends AbstractParsingFunction<ReturnStatement> {
    public ReturnStatementParsingFunction() {
        super("return_statement");
    }

    @Override
    public ReturnStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        parser.next(AddonTokenTypes.RETURN(), Text.translatable("meazy_addon:parser.expected.keyword", "return"));

        Expression expression = null;
        if (!parser.getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
            expression = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
        }
        parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "return"));

        return new ReturnStatement(expression);
    }
}
