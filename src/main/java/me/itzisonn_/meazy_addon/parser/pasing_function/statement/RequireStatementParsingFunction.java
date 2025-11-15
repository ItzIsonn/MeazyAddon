package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.RequireStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class RequireStatementParsingFunction extends AbstractParsingFunction<RequireStatement> {
    public RequireStatementParsingFunction() {
        super("require_statement");
    }

    @Override
    public RequireStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.next(AddonTokenTypes.REQUIRE(), Text.translatable("meazy_addon:parser.expected.keyword", "require"));
        String id = ParsingHelper.parseString(context);

        Version version;
        if (parser.getCurrent().getType().equals(AddonTokenTypes.STRING())) version = Version.of(ParsingHelper.parseString(context));
        else version = null;

        return new RequireStatement(id, version);
    }
}
