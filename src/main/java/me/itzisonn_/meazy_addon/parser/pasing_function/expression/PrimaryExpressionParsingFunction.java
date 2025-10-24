package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class PrimaryExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public PrimaryExpressionParsingFunction() {
        super("primary_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        TokenType tokenType = parser.getCurrent().getType();

        if (tokenType.equals(AddonTokenTypes.ID())) {
            if ((parser.getPos() != 0 && parser.getTokens().get(parser.getPos() - 1).getType().equals(AddonTokenTypes.NEW())) ||
                    (parser.getTokens().size() > parser.getPos() + 1 && parser.getTokens().get(parser.getPos() + 1).getType().equals(AddonTokenTypes.DOT()) && parser.getPos() != 0 && !parser.getTokens().get(parser.getPos() - 1).getType().equals(AddonTokenTypes.DOT())))
                return new ClassIdentifier(parser.getCurrentAndNext().getValue());
            else if (parser.getTokens().size() > parser.getPos() + 1 && parser.getTokens().get(parser.getPos() + 1).getType().equals(AddonTokenTypes.LEFT_PAREN())) {
                return new FunctionIdentifier(parser.getCurrentAndNext().getValue());
            }
            else return new VariableIdentifier(parser.getCurrentAndNext().getValue());
        }
        if (tokenType.equals(AddonTokenTypes.NULL())) {
            parser.getCurrentAndNext();
            return new NullLiteral();
        }
        if (tokenType.equals(AddonTokenTypes.NUMBER())) return new NumberLiteral(parser.getCurrentAndNext().getValue());
        if (tokenType.equals(AddonTokenTypes.STRING())) {
            String value = parser.getCurrentAndNext().getValue();
            return new StringLiteral(value.substring(1, value.length() - 1));
        }
        if (tokenType.equals(AddonTokenTypes.BOOLEAN())) return new BooleanLiteral(Boolean.parseBoolean(parser.getCurrentAndNext().getValue()));
        if (tokenType.equals(AddonTokenTypes.THIS())) {
            parser.getCurrentAndNext();
            return new ThisLiteral();
        }
        if (tokenType.equals(AddonTokenTypes.LEFT_PAREN())) {
            parser.getCurrentAndNext();
            Expression value = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis");
            return value;
        }

        throw new InvalidStatementException("Can't parse token with type " + tokenType.getId());
    }
}
