package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class InversionExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public InversionExpressionParsingFunction() {
        super("inversion_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.INVERSION())) {
            parser.next();
            Expression expression = parser.parseAfter(AddonMain.getIdentifier("inversion_expression"), Expression.class);
            return new OperatorExpression(expression, null, AddonOperators.INVERSION());
        }

        return parser.parseAfter(AddonMain.getIdentifier("inversion_expression"), Expression.class);
    }
}
