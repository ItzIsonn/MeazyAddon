package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.InvalidStatementException;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.Set;

public class GlobalStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public GlobalStatementParsingFunction() {
        super("global_statement");
    }

    @Override
    public Statement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.parseModifiers(context);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.CLASS())) {
            return parser.parse(AddonMain.getIdentifier("class_declaration_statement"), ClassDeclarationStatement.class, modifiers);
        }
        if (parser.getCurrent().getType().equals(AddonTokenTypes.FUNCTION())) {
            return parser.parse(AddonMain.getIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
        }
        if (parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
            return parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
        }

        throw new InvalidStatementException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.exception.global_statement"));
    }
}
