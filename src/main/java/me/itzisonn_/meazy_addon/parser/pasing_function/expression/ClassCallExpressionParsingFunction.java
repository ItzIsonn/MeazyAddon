package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.CallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class ClassCallExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public ClassCallExpressionParsingFunction() {
        super("class_call_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.NEW())) {
            int line = parser.getCurrentAndNext().getLine();
            Expression expression = parser.parseAfter(AddonMain.getIdentifier("class_call_expression"), Expression.class);

            if (expression instanceof CallExpression callExpression) {
                return new ClassCallExpression(new ClassIdentifier(callExpression.getCaller().getId()), callExpression.getArgs());
            }

            throw new InvalidSyntaxException(line, Text.translatable("meazy_addon:parser.exception.class_creation"));
        }

        return parser.parseAfter(AddonMain.getIdentifier("class_call_expression"), Expression.class);
    }
}
