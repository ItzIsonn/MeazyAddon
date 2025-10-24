package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.WhileStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.List;

public class WhileStatementParsingFunction extends AbstractParsingFunction<WhileStatement> {
    public WhileStatementParsingFunction() {
        super("while_statement");
    }

    @Override
    public WhileStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(AddonTokenTypes.WHILE(), "Expected while keyword");

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open while condition");
        Expression condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close while condition");

        parser.moveOverOptionalNewLines();
        parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open while body");
        List<Statement> body = ParsingHelper.parseBody(context);
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close while body");

        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the while statement");

        return new WhileStatement(condition, body);
    }
}
