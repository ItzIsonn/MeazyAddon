package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.operator.AddonOperators;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class PowerExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public PowerExpressionParsingFunction() {
        super("power_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Expression left = parser.parseAfter(AddonMain.getIdentifier("power_expression"), Expression.class);

        while (parser.getCurrent().getType().equals(AddonTokenTypes.POWER())) {
            parser.next();
            Expression right = parser.parseAfter(AddonMain.getIdentifier("power_expression"), Expression.class);
            left = new OperatorExpression(left, right, AddonOperators.POWER());
        }

        return left;
    }
}
