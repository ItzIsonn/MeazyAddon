package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

import java.util.HashMap;
import java.util.Map;

public class MapCreationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public MapCreationExpressionParsingFunction() {
        super("map_creation_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
            parser.getCurrentAndNext();
            Map<Expression, Expression> map = new HashMap<>();

            while (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                Expression key = parser.parse(AddonMain.getIdentifier("list_creation_expression"), Expression.class);
                Expression value;

                if (parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
                    parser.getCurrentAndNext();
                    value = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
                }
                else value = new NullLiteral();

                map.put(key, value);

                if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                    parser.getCurrentAndNext(AddonTokenTypes.COMMA(), Text.translatable("meazy_addon:parser.expected.separator_expression", "comma", "map_creation"));
                }
            }

            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), Text.translatable("meazy_addon:parser.expected.end_expression", "right_brace", "map_creation"));
            return new MapCreationExpression(map);
        }

        return parser.parseAfter(AddonMain.getIdentifier("map_creation_expression"), Expression.class);
    }
}
