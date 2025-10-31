package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
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
        parser.next(AddonTokenTypes.FUNCTION(), Text.translatable("meazy_addon:parser.expected.keyword", "function"));

        String classId = null;
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected.after_keyword", "id", "function")).getValue();
        if (parser.getCurrent().getType().equals(AddonTokenTypes.DOT())) {
            parser.next();
            classId = id;
            id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected", "id")).getValue();
        }

        List<ParameterExpression> parameters = ParsingHelper.parseParameters(context);
        DataType dataType = ParsingHelper.parseDataType(context);

        if (modifiers.contains(AddonModifiers.ABSTRACT()) || modifiers.contains(AddonModifiers.NATIVE())) {
            parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "function_declaration"));
            return new FunctionDeclarationStatement(modifiers, id, parameters, new ArrayList<>(), dataType);
        }

        List<Statement> body;
        if (parser.getCurrent().getType().equals(AddonTokenTypes.ARROW())) {
            parser.next();
            body = new ArrayList<>(List.of(parser.parse(AddonMain.getIdentifier("statement"))));
        }
        else {
            parser.moveOverOptionalNewLines();
            parser.next(AddonTokenTypes.LEFT_BRACE(), Text.translatable("meazy_addon:parser.expected.start", "left_brace", "function_body"));
            body = ParsingHelper.parseBody(context);
            parser.next(AddonTokenTypes.RIGHT_BRACE(), Text.translatable("meazy_addon:parser.expected.end", "right_brace", "function_body"));
            parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "function_declaration"));
        }

        return new FunctionDeclarationStatement(modifiers, id, classId, parameters, body, dataType);
    }
}
