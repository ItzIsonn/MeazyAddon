package me.itzisonn_.meazy_addon.lexer;

import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;

public final class AddonTokenTypeSets {
    private static boolean isInit = false;

    private AddonTokenTypeSets() {}



    public static TokenTypeSet KEYWORDS() {
        return Registries.TOKEN_TYPE_SETS.getEntry(AddonMain.getIdentifier("keywords")).getValue();
    }

    public static TokenTypeSet OPERATOR_ASSIGN() {
        return Registries.TOKEN_TYPE_SETS.getEntry(AddonMain.getIdentifier("operator_assign")).getValue();
    }

    public static TokenTypeSet OPERATOR_POSTFIX() {
        return Registries.TOKEN_TYPE_SETS.getEntry(AddonMain.getIdentifier("operator_postfix")).getValue();
    }

    public static TokenTypeSet MEMBER_ACCESS() {
        return Registries.TOKEN_TYPE_SETS.getEntry(AddonMain.getIdentifier("member_access")).getValue();
    }



    private static void register(String id, TokenTypeSet tokenTypeSet) {
        Registries.TOKEN_TYPE_SETS.register(AddonMain.getIdentifier(id), tokenTypeSet);
    }

    /**
     * Initializes {@link Registries#TOKEN_TYPE_SETS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#TOKEN_TYPE_SETS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("TokenTypeSets have already been initialized!");
        isInit = true;

        register("keywords", new TokenTypeSet(
                AddonTokenTypes.VARIABLE(),
                AddonTokenTypes.FUNCTION(),
                AddonTokenTypes.CLASS(),
                AddonTokenTypes.CONSTRUCTOR(),
                AddonTokenTypes.BASE(),
                AddonTokenTypes.NEW(),
                AddonTokenTypes.IF(),
                AddonTokenTypes.ELSE(),
                AddonTokenTypes.FOR(),
                AddonTokenTypes.IN(),
                AddonTokenTypes.WHILE(),
                AddonTokenTypes.RETURN(),
                AddonTokenTypes.CONTINUE(),
                AddonTokenTypes.BREAK(),
                AddonTokenTypes.IS(),
                AddonTokenTypes.IS_LIKE(),
                AddonTokenTypes.NULL(),
                AddonTokenTypes.BOOLEAN(),
                AddonTokenTypes.THIS()
        ));

        register("operator_assign", new TokenTypeSet(
                AddonTokenTypes.PLUS_ASSIGN(),
                AddonTokenTypes.MINUS_ASSIGN(),
                AddonTokenTypes.MULTIPLY_ASSIGN(),
                AddonTokenTypes.DIVIDE_ASSIGN(),
                AddonTokenTypes.PERCENT_ASSIGN(),
                AddonTokenTypes.POWER_ASSIGN()
        ));

        register("operator_postfix", new TokenTypeSet(
                AddonTokenTypes.DOUBLE_PLUS(),
                AddonTokenTypes.DOUBLE_MINUS()
        ));

        register("member_access", new TokenTypeSet(
                AddonTokenTypes.DOT(),
                AddonTokenTypes.QUESTION_DOT()
        ));
    }
}
