package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.parser.ast.statement.ForStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.HashSet;
import java.util.List;

public class ForStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public ForStatementParsingFunction() {
        super("for_statement");
    }

    @Override
    public Statement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.next(AddonTokenTypes.FOR(), Text.translatable("meazy_addon:parser.expected.keyword", "for"));
        parser.next(AddonTokenTypes.LEFT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.start", "left_parenthesis", "for_condition"));

        int lineNumber = parser.getCurrent().getLine();

        VariableDeclarationStatement variableDeclarationStatement = parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), true);
        if (variableDeclarationStatement.getValue() != null && !(variableDeclarationStatement.getValue() instanceof NullLiteral)) {
            throw new InvalidSyntaxException(lineNumber, Text.translatable("meazy_addon:parser.exception.foreach_variable_wth_value"));
        }

        parser.next(AddonTokenTypes.IN(), Text.translatable("meazy_addon:parser.expected.after_statement", "in", "variable_declaration"));
        Expression collection = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);

        parser.next(AddonTokenTypes.RIGHT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.end", "right_parenthesis", "for_condition"));
        parser.moveOverOptionalNewLines();

        parser.next(AddonTokenTypes.LEFT_BRACE(), Text.translatable("meazy_addon:parser.expected.start", "left_brace", "for_body"));
        List<Statement> body = ParsingHelper.parseBody(context);
        parser.next(AddonTokenTypes.RIGHT_BRACE(), Text.translatable("meazy_addon:parser.expected.end", "right_brace", "for_body"));

        return new ForStatement(variableDeclarationStatement, collection, body);
    }
}
