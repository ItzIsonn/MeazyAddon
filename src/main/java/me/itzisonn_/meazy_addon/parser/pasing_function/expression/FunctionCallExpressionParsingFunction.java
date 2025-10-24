package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

public class FunctionCallExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public FunctionCallExpressionParsingFunction() {
        super("function_call_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Expression expression = parser.parseAfter(AddonMain.getIdentifier("function_call_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) {
            if (!(expression instanceof Identifier identifier)) throw new InvalidSyntaxException("Can't call non-identifier");
            return new FunctionCallExpression(new FunctionIdentifier(identifier.getId()), ParsingHelper.parseArgs(context));
        }

        return expression;
    }
}
