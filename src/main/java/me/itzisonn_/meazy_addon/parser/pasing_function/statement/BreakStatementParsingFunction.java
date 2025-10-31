package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.BreakStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class BreakStatementParsingFunction extends AbstractParsingFunction<BreakStatement> {
    public BreakStatementParsingFunction() {
        super("break_statement");
    }

    @Override
    public BreakStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        parser.next(AddonTokenTypes.BREAK(), Text.translatable("meazy_addon:parser.expected.keyword", "break"));
        parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "break"));
        return new BreakStatement();
    }
}
