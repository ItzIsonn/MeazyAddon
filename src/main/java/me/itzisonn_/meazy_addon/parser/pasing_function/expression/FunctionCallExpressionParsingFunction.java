package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
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

        if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PARENTHESIS())) {
            if (!(expression instanceof Identifier identifier)) {
                throw new InvalidSyntaxException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.exception.call_not_identifier"));
            }

            return new FunctionCallExpression(new FunctionIdentifier(identifier.getId()), ParsingHelper.parseArgs(context));
        }

        return expression;
    }
}
