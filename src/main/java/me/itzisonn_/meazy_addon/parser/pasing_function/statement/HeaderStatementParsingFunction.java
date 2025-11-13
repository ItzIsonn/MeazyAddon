package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class HeaderStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public HeaderStatementParsingFunction() {
        super("header_statement");
    }

    @Override
    public Statement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.IMPORT())) {
            return parser.parse(AddonMain.getIdentifier("import_statement"), ImportStatement.class);
        }
        if (parser.getCurrent().getType().equals(AddonTokenTypes.USING())) {
            return parser.parse(AddonMain.getIdentifier("using_statement"), UsingStatement.class);
        }

        return null;
    }
}
