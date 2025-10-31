package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class NegationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public NegationExpressionParsingFunction() {
        super("negation_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.MINUS())) {
            parser.next();
            Expression expression = parser.parseAfter(AddonMain.getIdentifier("negation_expression"), Expression.class);
            return new OperatorExpression(expression, null, AddonOperators.NEGATION());
        }

        return parser.parseAfter(AddonMain.getIdentifier("negation_expression"), Expression.class);
    }
}
