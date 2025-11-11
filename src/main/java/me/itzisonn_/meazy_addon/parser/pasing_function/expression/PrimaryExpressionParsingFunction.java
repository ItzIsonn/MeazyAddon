package me.itzisonn_.meazy_addon.parser.pasing_function.expression;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.*;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

public class PrimaryExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public PrimaryExpressionParsingFunction() {
        super("primary_expression");
    }

    @Override
    public Expression parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Token token = parser.getCurrent();
        TokenType tokenType = token.getType();

        if (tokenType.equals(AddonTokenTypes.ID())) {
            if (parser.getTokens().size() > parser.getPos() + 1 && parser.getTokens().get(parser.getPos() + 1).getType().equals(AddonTokenTypes.LEFT_PARENTHESIS())) {
                String id = parser.getCurrentAndNext().getValue();
                if (Character.isUpperCase(id.charAt(0))) return new ClassIdentifier(id);
                else return new FunctionIdentifier(id);
            }

            if (parser.getPos() > 0 && parser.getTokens().get(parser.getPos() - 1).getType().equals(AddonTokenTypes.DOT())) {
                return new VariableIdentifier(parser.getCurrentAndNext().getValue());
            }

            String id = parser.getCurrentAndNext().getValue();
            if (Character.isUpperCase(id.charAt(0))) return new ClassIdentifier(id);
            else return new VariableIdentifier(id);
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
        if (tokenType.equals(AddonTokenTypes.LEFT_PARENTHESIS())) {
            parser.getCurrentAndNext();
            Expression value = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected", "right_parenthesis"));
            return value;
        }

        throw new InvalidStatementException(token.getLine(), Text.translatable("meazy_addon:parser.exception.cant_parse", tokenType.getId()));
    }
}
