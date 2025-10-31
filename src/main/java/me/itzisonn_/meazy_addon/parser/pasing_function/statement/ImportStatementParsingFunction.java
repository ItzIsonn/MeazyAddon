package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class ImportStatementParsingFunction extends AbstractParsingFunction<ImportStatement> {
    public ImportStatementParsingFunction() {
        super("import_statement");
    }

    @Override
    public ImportStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.next(AddonTokenTypes.IMPORT(), Text.translatable("meazy_addon:parser.expected.keyword", "import"));
        String file = parser.getCurrentAndNext(AddonTokenTypes.STRING(), Text.translatable("meazy_addon:parser.expected.after_keyword", "string", "import")).getValue();

        return new ImportStatement(file.substring(1, file.length() - 1));
    }
}
