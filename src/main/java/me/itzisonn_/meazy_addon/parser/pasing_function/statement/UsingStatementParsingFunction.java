package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.UsingStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class UsingStatementParsingFunction extends AbstractParsingFunction<UsingStatement> {
    public UsingStatementParsingFunction() {
        super("using_statement");
    }

    @Override
    public UsingStatement parse(ParsingContext context, Object... extra) {
        context.getParser().next(AddonTokenTypes.USING(), Text.translatable("meazy_addon:parser.expected.keyword", "using"));
        return new UsingStatement(ParsingHelper.parseString(context));
    }
}
