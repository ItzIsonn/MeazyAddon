package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class AssignmentExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public AssignmentExpressionParsingFunction() {
        super("assignment_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Expression left = parser.parseAfter(AddonMain.getIdentifier("assignment_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
            parser.getCurrentAndNext();
            Expression value = parser.parse(AddonMain.getIdentifier("assignment_expression"), Expression.class);
            return new AssignmentExpression(left, value);
        }
        else if (AddonTokenTypeSets.OPERATOR_ASSIGN().contains(parser.getCurrent().getType())) {
            Token token = parser.getCurrentAndNext();
            Expression value = new OperatorExpression(
                    left,
                    parser.parse(AddonMain.getIdentifier("assignment_expression"), Expression.class),
                    token.getValue().replaceAll("=$", ""), OperatorType.INFIX);
            return new AssignmentExpression(left, value);
        }

        return left;
    }
}
