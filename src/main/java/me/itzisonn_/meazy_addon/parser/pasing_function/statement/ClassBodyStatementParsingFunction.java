package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.Set;

public class ClassBodyStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public ClassBodyStatementParsingFunction() {
        super("class_body_statement");
    }

    @Override
    public Statement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.parseModifiers(context);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.FUNCTION())) {
            return parser.parse(AddonMain.getIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
        }
        if (parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
            VariableDeclarationStatement variableDeclarationStatement =
                    parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, true);

            parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "variable_declaration"));
            parser.moveOverOptionalNewLines();
            return variableDeclarationStatement;
        }
        if (parser.getCurrent().getType().equals(AddonTokenTypes.CONSTRUCTOR())) {
            return parser.parse(AddonMain.getIdentifier("constructor_declaration_statement"), ConstructorDeclarationStatement.class, modifiers);
        }

        throw new InvalidStatementException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.expected.statement", "class_body"));
    }
}
