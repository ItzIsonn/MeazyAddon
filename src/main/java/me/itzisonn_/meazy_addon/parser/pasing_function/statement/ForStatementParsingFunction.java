package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.statement.ForStatement;
import me.itzisonn_.meazy_addon.parser.ast.statement.ForeachStatement;
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

        parser.getCurrentAndNext(AddonTokenTypes.FOR(), "Expected for keyword");

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open for condition");

        if (parser.currentLineHasToken(AddonTokenTypes.IN())) {
            VariableDeclarationStatement variableDeclarationStatement = parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), true);
            if (variableDeclarationStatement.getDeclarationInfos().size() > 1) {
                throw new InvalidSyntaxException("Foreach statement can declare only one variable");
            }
            parser.getCurrentAndNext(AddonTokenTypes.IN(), "Expected IN after variable declaration");
            Expression collection = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);

            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

            parser.moveOverOptionalNewLines();
            parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
            List<Statement> body = ParsingHelper.parseBody(context);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

            return new ForeachStatement(variableDeclarationStatement, collection, body);
        }

        VariableDeclarationStatement variableDeclarationStatement = null;
        if (!parser.getCurrent().getType().equals(AddonTokenTypes.SEMICOLON())) {
            variableDeclarationStatement = parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), false);
        }
        parser.getCurrentAndNext(AddonTokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

        Expression condition = null;
        if (!parser.getCurrent().getType().equals(AddonTokenTypes.SEMICOLON())) {
            condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
        }
        parser.getCurrentAndNext(AddonTokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

        AssignmentExpression assignmentExpression = null;
        if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
            if (parser.parse(AddonMain.getIdentifier("assignment_expression"), Expression.class) instanceof AssignmentExpression expression) {
                assignmentExpression = expression;
            }
            else throw new InvalidSyntaxException("Expected assignment expression as for statement's arg");
        }
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

        parser.moveOverOptionalNewLines();
        parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
        List<Statement> body = ParsingHelper.parseBody(context);
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

        return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
    }
}
