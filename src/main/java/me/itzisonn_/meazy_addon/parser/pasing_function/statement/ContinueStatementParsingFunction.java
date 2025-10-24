package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.ContinueStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class ContinueStatementParsingFunction extends AbstractParsingFunction<ContinueStatement> {
    public ContinueStatementParsingFunction() {
        super("continue_statement");
    }

    @Override
    public ContinueStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(AddonTokenTypes.CONTINUE(), "Expected continue keyword");
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the continue statement");
        return new ContinueStatement();
    }
}
