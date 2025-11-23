package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.AssignmentStatement;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class AssignmentStatementParsingFunction extends AbstractParsingFunction<AssignmentStatement> {
    public AssignmentStatementParsingFunction() {
        super("assignment_statement");
    }

    @Override
    public AssignmentStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Expression left = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
            parser.next();
            Expression value = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            return new AssignmentStatement(left, value);
        }
        else if (AddonTokenTypeSets.OPERATOR_ASSIGN().contains(parser.getCurrent().getType())) {
            Token token = parser.getCurrentAndNext();
            Expression value = new OperatorExpression(
                    left,
                    parser.parse(AddonMain.getIdentifier("expression"), Expression.class),
                    token.getValue().replaceAll("=$", ""), OperatorType.INFIX);
            return new AssignmentStatement(left, value);
        }

        return null;
    }
}
