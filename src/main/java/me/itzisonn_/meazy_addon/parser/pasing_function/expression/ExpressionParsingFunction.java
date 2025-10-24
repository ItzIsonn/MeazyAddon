package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class ExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public ExpressionParsingFunction() {
        super("expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        return parser.parseAfter(AddonMain.getIdentifier("expression"), Expression.class);
    }
}
