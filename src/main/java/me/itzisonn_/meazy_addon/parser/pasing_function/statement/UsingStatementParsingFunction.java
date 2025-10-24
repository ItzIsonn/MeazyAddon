package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
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

        parser.getCurrentAndNext(AddonTokenTypes.USING(), "Expected using keyword");
        String nativeClass = parser.getCurrentAndNext(AddonTokenTypes.STRING(), "Expected native class name after using keyword").getValue();
        return new UsingStatement(nativeClass.substring(1, nativeClass.length() - 1));
    }
}
