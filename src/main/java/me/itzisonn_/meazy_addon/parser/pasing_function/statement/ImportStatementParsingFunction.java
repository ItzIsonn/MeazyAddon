package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class ImportStatementParsingFunction extends AbstractParsingFunction<ImportStatement> {
    public ImportStatementParsingFunction() {
        super("import_statement");
    }

    @Override
    public ImportStatement parse(ParsingContext context, Object... extra) {
        context.getParser().next(AddonTokenTypes.IMPORT(), Text.translatable("meazy_addon:parser.expected.keyword", "import"));
        return new ImportStatement(ParsingHelper.parseString(context));
    }
}
