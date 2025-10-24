package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.IfStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.ArrayList;
import java.util.List;

public class IfStatementParsingFunction extends AbstractParsingFunction<IfStatement> {
    public IfStatementParsingFunction() {
        super("if_statement");
    }

    @Override
    public IfStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(AddonTokenTypes.IF(), "Expected if keyword");

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open if condition");
        Expression condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close if condition");
        parser.moveOverOptionalNewLines();

        List<Statement> body = new ArrayList<>();
        if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
            parser.getCurrentAndNext();
            body = ParsingHelper.parseBody(context);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
        }
        else body.add(parser.parse(AddonMain.getIdentifier("statement")));

        IfStatement elseStatement = null;
        if (parser.getCurrent().getType().equals(AddonTokenTypes.ELSE())) {
            parser.getCurrentAndNext();
            if (parser.getCurrent().getType().equals(AddonTokenTypes.IF())) {
                elseStatement = parser.parse(AddonMain.getIdentifier("if_statement"), IfStatement.class);
            }
            else {
                List<Statement> elseBody = new ArrayList<>();
                parser.moveOverOptionalNewLines();
                if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                    parser.getCurrentAndNext();
                    elseBody = ParsingHelper.parseBody(context);
                    parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                    parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
                }
                else {
                    elseBody.add(parser.parse(AddonMain.getIdentifier("statement")));
                }

                elseStatement = new IfStatement(null, elseBody, null);
            }
        }

        return new IfStatement(condition, body, elseStatement);
    }
}
