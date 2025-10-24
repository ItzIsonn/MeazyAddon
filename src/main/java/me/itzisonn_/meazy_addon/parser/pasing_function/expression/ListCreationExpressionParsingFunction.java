package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

import java.util.ArrayList;
import java.util.List;

public class ListCreationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public ListCreationExpressionParsingFunction() {
        super("list_creation_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACKET())) {
            parser.getCurrentAndNext();

            List<Expression> list = new ArrayList<>();
            while (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACKET())) {
                list.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
                if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACKET())) parser.getCurrentAndNext(AddonTokenTypes.COMMA(), "Expected comma as a separator between list elements");
            }
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACKET(), "Expected right bracket to close list creation");

            return new ListCreationExpression(list);
        }

        return parser.parseAfter(AddonMain.getIdentifier("list_creation_expression"), Expression.class);
    }
}
