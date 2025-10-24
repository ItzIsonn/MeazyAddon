package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

import java.util.ArrayList;
import java.util.List;

public class BaseCallStatementParsingFunction extends AbstractParsingFunction<BaseCallStatement> {
    public BaseCallStatementParsingFunction() {
        super("base_call_statement");
    }

    @Override
    public BaseCallStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(AddonTokenTypes.BASE(), "Expected BASE to start base call statement");

        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after base keyword").getValue();

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<Expression> args = new ArrayList<>();
        if (parser.getCurrent().getType() != AddonTokenTypes.RIGHT_PAREN()) {
            args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.getCurrentAndNext();
                args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
            }
        }
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return new BaseCallStatement(id, args);
    }
}
