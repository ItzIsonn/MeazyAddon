package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FunctionDeclarationStatementParsingFunction extends AbstractParsingFunction<FunctionDeclarationStatement> {
    public FunctionDeclarationStatementParsingFunction() {
        super("function_declaration_statement");
    }

    @Override
    public FunctionDeclarationStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        parser.getCurrentAndNext(AddonTokenTypes.FUNCTION(), "Expected function keyword");

        String classId = null;
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after function keyword").getValue();
        if (parser.getCurrent().getType().equals(AddonTokenTypes.DOT())) {
            parser.getCurrentAndNext();
            classId = id;
            id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after function keyword").getValue();
        }

        List<CallArgExpression> args = ParsingHelper.parseCallArgs(context);
        DataType dataType = ParsingHelper.parseDataType(context);

        if (modifiers.contains(AddonModifiers.ABSTRACT()) || modifiers.contains(AddonModifiers.NATIVE())) {
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
            return new FunctionDeclarationStatement(modifiers, id, args, new ArrayList<>(), dataType);
        }

        List<Statement> body;
        if (parser.getCurrent().getType().equals(AddonTokenTypes.ARROW())) {
            parser.getCurrentAndNext();
            body = new ArrayList<>(List.of(parser.parse(AddonMain.getIdentifier("statement"))));
        }
        else {
            parser.moveOverOptionalNewLines();
            parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open function body");
            body = ParsingHelper.parseBody(context);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close function body");
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
        }

        return new FunctionDeclarationStatement(modifiers, id, classId, args, body, dataType);
    }
}
