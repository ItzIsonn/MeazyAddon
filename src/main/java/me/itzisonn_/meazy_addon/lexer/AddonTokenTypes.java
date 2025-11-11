package me.itzisonn_.meazy_addon.lexer;

import me.itzisonn_.meazy.lexer.NativeCanMatch;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;

/**
 * All basic TokenTypes
 *
 * @see Registries#TOKEN_TYPES
 */
public final class AddonTokenTypes {
    private AddonTokenTypes() {}



    public static TokenType REQUIRE() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("require")).getValue();
    }

    public static TokenType IMPORT() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("import")).getValue();
    }

    public static TokenType USING() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("using")).getValue();
    }

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



    public static TokenType LEFT_PARENTHESIS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("left_parenthesis")).getValue();
    }

    public static TokenType RIGHT_PARENTHESIS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("right_parenthesis")).getValue();
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

    public static TokenType MINUS() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("minus")).getValue();
    }

    public static TokenType POWER() {
        return Registries.TOKEN_TYPES.getEntry(AddonMain.getIdentifier("power")).getValue();
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



    @NativeCanMatch
    public static boolean canMatchId(String string) {
        for (TokenType tokenType : AddonTokenTypeSets.KEYWORDS().getTokenTypes()) {
            if (tokenType.getPattern().matcher(string).matches()) return false;
        }
        return true;
    }
}
