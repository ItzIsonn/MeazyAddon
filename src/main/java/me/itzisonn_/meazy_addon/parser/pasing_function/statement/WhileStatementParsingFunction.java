package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
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

        parser.getCurrentAndNext(AddonTokenTypes.WHILE(), Text.translatable("meazy_addon:parser.expected.keyword", "while"));

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.start", "left_parenthesis", "while_condition"));
        Expression condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.end", "right_parenthesis", "while_condition"));

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), Text.translatable("meazy_addon:parser.expected.start", "left_brace", "while_body"));
        List<Statement> body = ParsingHelper.parseBody(context);
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), Text.translatable("meazy_addon:parser.expected.end", "right_brace", "while_body"));

        return new WhileStatement(condition, body);
    }
}
