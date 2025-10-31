package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class BaseCallStatementParsingFunction extends AbstractParsingFunction<BaseCallStatement> {
    public BaseCallStatementParsingFunction() {
        super("base_call_statement");
    }

    @Override
    public BaseCallStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.next(AddonTokenTypes.BASE(), Text.translatable("meazy_addon:parser.expected.start_statement", "base", "base_call"));
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected.after_keyword", "id", "base")).getValue();

        return new BaseCallStatement(id, ParsingHelper.parseArgs(context));
    }
}
