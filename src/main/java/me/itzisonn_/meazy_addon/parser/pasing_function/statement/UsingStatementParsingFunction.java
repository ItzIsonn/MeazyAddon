package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.UsingStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class UsingStatementParsingFunction extends AbstractParsingFunction<UsingStatement> {
    public UsingStatementParsingFunction() {
        super("using_statement");
    }

    @Override
    public UsingStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.next(AddonTokenTypes.USING(), Text.translatable("meazy_addon:parser.expected.keyword", "using"));
        String nativeClass = parser.getCurrentAndNext(AddonTokenTypes.STRING(), Text.translatable("meazy_addon:parser.expected.after_keyword", "string", "using")).getValue();

        return new UsingStatement(nativeClass.substring(1, nativeClass.length() - 1));
    }
}
