package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConstructorDeclarationStatementParsingFunction extends AbstractParsingFunction<ConstructorDeclarationStatement> {
    public ConstructorDeclarationStatementParsingFunction() {
        super("constructor_declaration_statement");
    }

    @Override
    public ConstructorDeclarationStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);
        parser.getCurrentAndNext(AddonTokenTypes.CONSTRUCTOR(), "Expected constructor keyword");

        List<ParameterExpression> parameters = ParsingHelper.parseParameters(context);

        if (modifiers.contains(AddonModifiers.NATIVE())) {
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");
            return new ConstructorDeclarationStatement(modifiers, parameters, new ArrayList<>());
        }

        boolean hasNewLine = parser.getCurrent().getType().equals(TokenTypes.NEW_LINE());
        parser.moveOverOptionalNewLines();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE()) && hasNewLine) {
            return new ConstructorDeclarationStatement(modifiers, parameters, new ArrayList<>());
        }

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open constructor body");
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
        parser.moveOverOptionalNewLines();

        List<Statement> body = new ArrayList<>();
        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            if (parser.getCurrent().getType().equals(AddonTokenTypes.BASE())) body.add(parser.parse(AddonMain.getIdentifier("base_call_statement")));
            else body.add(parser.parse(AddonMain.getIdentifier("statement")));
            parser.moveOverOptionalNewLines();
        }

        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close constructor body");
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");

        return new ConstructorDeclarationStatement(modifiers, parameters, body);
    }
}
