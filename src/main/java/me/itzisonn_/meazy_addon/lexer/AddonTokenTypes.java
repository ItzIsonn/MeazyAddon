package me.itzisonn_.meazy_addon.lexer;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;

/**
 * All basic TokenTypes
 *
 * @see Registries#TOKEN_TYPES
 */
public final class AddonTokenTypes {
    private static boolean isInit = false;

    private AddonTokenTypes() {}



    public static TokenType VARIABLE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("variable")).getValue();
    }

    public static TokenType FUNCTION() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("function")).getValue();
    }

    public static TokenType CLASS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("class")).getValue();
    }

    public static TokenType CONSTRUCTOR() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("constructor")).getValue();
    }

    public static TokenType BASE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("base")).getValue();
    }

    public static TokenType NEW() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("new")).getValue();
    }

    public static TokenType IF() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("if")).getValue();
    }

    public static TokenType ELSE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("else")).getValue();
    }

    public static TokenType FOR() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("for")).getValue();
    }

    public static TokenType IN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("in")).getValue();
    }

    public static TokenType WHILE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("while")).getValue();
    }

    public static TokenType RETURN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("return")).getValue();
    }

    public static TokenType CONTINUE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("continue")).getValue();
    }

    public static TokenType BREAK() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("break")).getValue();
    }

    public static TokenType IS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("is")).getValue();
    }

    public static TokenType IS_LIKE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("is_like")).getValue();
    }



    public static TokenType COMMENT() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("comment")).getValue();
    }

    public static TokenType MULTI_LINE_COMMENT() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("multi_line_comment")).getValue();
    }



    public static TokenType LEFT_PAREN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("left_paren")).getValue();
    }

    public static TokenType RIGHT_PAREN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("right_paren")).getValue();
    }

    public static TokenType LEFT_BRACE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("left_brace")).getValue();
    }

    public static TokenType RIGHT_BRACE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("right_brace")).getValue();
    }

    public static TokenType LEFT_BRACKET() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("left_bracket")).getValue();
    }

    public static TokenType RIGHT_BRACKET() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("right_bracket")).getValue();
    }

    public static TokenType COLON() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("colon")).getValue();
    }

    public static TokenType SEMICOLON() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("semicolon")).getValue();
    }

    public static TokenType COMMA() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("comma")).getValue();
    }

    public static TokenType DOT() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("dot")).getValue();
    }

    public static TokenType QUESTION() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("question")).getValue();
    }

    public static TokenType QUESTION_DOT() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("question_dot")).getValue();
    }

    public static TokenType QUESTION_COLON() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("question_colon")).getValue();
    }

    public static TokenType ARROW() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("arrow")).getValue();
    }



    public static TokenType ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("assign")).getValue();
    }

    public static TokenType PLUS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("plus")).getValue();
    }

    public static TokenType MINUS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("minus")).getValue();
    }

    public static TokenType MULTIPLY() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("multiply")).getValue();
    }

    public static TokenType DIVIDE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("divide")).getValue();
    }

    public static TokenType PERCENT() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("percent")).getValue();
    }

    public static TokenType POWER() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("power")).getValue();
    }

    public static TokenType PLUS_ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("plus_assign")).getValue();
    }

    public static TokenType MINUS_ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("minus_assign")).getValue();
    }

    public static TokenType MULTIPLY_ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("multiply_assign")).getValue();
    }

    public static TokenType DIVIDE_ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("divide_assign")).getValue();
    }

    public static TokenType PERCENT_ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("percent_assign")).getValue();
    }

    public static TokenType POWER_ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("power_assign")).getValue();
    }

    public static TokenType DOUBLE_PLUS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("double_plus")).getValue();
    }

    public static TokenType DOUBLE_MINUS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("double_minus")).getValue();
    }



    public static TokenType AND() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("and")).getValue();
    }

    public static TokenType OR() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("or")).getValue();
    }

    public static TokenType INVERSION() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("inversion")).getValue();
    }

    public static TokenType EQUALS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("equals")).getValue();
    }

    public static TokenType NOT_EQUALS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("not_equals")).getValue();
    }

    public static TokenType GREATER() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("greater")).getValue();
    }

    public static TokenType GREATER_OR_EQUALS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("greater_or_equals")).getValue();
    }

    public static TokenType LESS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("less")).getValue();
    }

    public static TokenType LESS_OR_EQUALS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("less_or_equals")).getValue();
    }



    public static TokenType NULL() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("null")).getValue();
    }

    public static TokenType NUMBER() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("number")).getValue();
    }

    public static TokenType STRING() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("string")).getValue();
    }

    public static TokenType BOOLEAN() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("boolean")).getValue();
    }

    public static TokenType THIS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("this")).getValue();
    }

    public static TokenType ID() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("id")).getValue();
    }



    private static void register(TokenType tokenType) {
        Registries.TOKEN_TYPES.register(AddonMain.getIdentifier(tokenType.getId()), tokenType);
    }

    /**
     * Initializes {@link Registries#TOKEN_TYPES} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#TOKEN_TYPES} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("TokenTypes have already been initialized!");
        isInit = true;

        register(new TokenType("variable", "var|val", false));
        register(new TokenType("function", "function|fun", false));
        register(new TokenType("class", "class", false));
        register(new TokenType("constructor", "constructor", false));
        register(new TokenType("base", "base", false));
        register(new TokenType("new", "new", false));
        register(new TokenType("if", "if", false));
        register(new TokenType("else", "else", false));
        register(new TokenType("for", "for", false));
        register(new TokenType("in", "in", false));
        register(new TokenType("while", "while", false));
        register(new TokenType("return", "return", false));
        register(new TokenType("continue", "continue", false));
        register(new TokenType("break", "break", false));
        register(new TokenType("is", "is", false));
        register(new TokenType("is_like", "isLike", false));

        register(new TokenType("comment", "\\/\\/[^\n]*", true));
        register(new TokenType("multi_line_comment", "\\/\\*(?:(?!\\*\\/).)*\\*\\/", true));

        register(new TokenType("left_paren", "\\(", false));
        register(new TokenType("right_paren", "\\)", false));
        register(new TokenType("left_brace", "\\{", false));
        register(new TokenType("right_brace", "\\}", false));
        register(new TokenType("left_bracket", "\\[", false));
        register(new TokenType("right_bracket", "\\]", false));
        register(new TokenType("colon", ":", false));
        register(new TokenType("semicolon", ";", false));
        register(new TokenType("comma", ",", false));
        register(new TokenType("dot", "\\.", false));
        register(new TokenType("question", "\\?", false));
        register(new TokenType("question_dot", "\\?\\.", false));
        register(new TokenType("question_colon", "\\?:", false));
        register(new TokenType("arrow", "->", false));

        register(new TokenType("assign", "=", false));
        register(new TokenType("plus", "\\+", false));
        register(new TokenType("minus", "-", false));
        register(new TokenType("multiply", "\\*", false));
        register(new TokenType("divide", "\\/", false));
        register(new TokenType("percent", "%", false));
        register(new TokenType("power", "\\^", false));
        register(new TokenType("plus_assign", "\\+=", false));
        register(new TokenType("minus_assign", "-=", false));
        register(new TokenType("multiply_assign", "\\*=", false));
        register(new TokenType("divide_assign", "\\/=", false));
        register(new TokenType("percent_assign", "%=", false));
        register(new TokenType("power_assign", "\\^=", false));
        register(new TokenType("double_plus", "\\+\\+", false));
        register(new TokenType("double_minus", "--", false));

        register(new TokenType("and", "&&", false));
        register(new TokenType("or", "\\|\\|", false));
        register(new TokenType("inversion", "!", false));
        register(new TokenType("equals", "==", false));
        register(new TokenType("not_equals", "!=", false));
        register(new TokenType("greater", ">", false));
        register(new TokenType("greater_or_equals", ">=", false));
        register(new TokenType("less", "<", false));
        register(new TokenType("less_or_equals", "<=", false));

        register(new TokenType("null", "null", false));
        register(new TokenType("number", "(0|([1-9][0-9]*))(\\.[0-9]+)?", false));
        register(new TokenType("string", "\"[^\"]*\"", false));
        register(new TokenType("boolean", "true|false", false));
        register(new TokenType("this", "this", false));
        register(new TokenType("id", Utils.IDENTIFIER_REGEX, false) {
            @Override
            public boolean canMatch(String string) {
                for (TokenType tokenType : AddonTokenTypeSets.KEYWORDS().getTokenTypes()) {
                    if (tokenType.getPattern().matcher(string).matches()) return false;
                }
                return true;
            }
        });
    }
}
