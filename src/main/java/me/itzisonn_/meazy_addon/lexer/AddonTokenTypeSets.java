package me.itzisonn_.meazy_addon.lexer;

import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;

import static me.itzisonn_.meazy_addon.lexer.AddonTokenTypes.*;

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
        if (isInit) throw new IllegalStateException("TokenTypeSets have already been initialized");
        isInit = true;

        register("keywords", new TokenTypeSet(
                REQUIRE(),
                IMPORT(),
                VARIABLE(),
                FUNCTION(),
                CLASS(),
                CONSTRUCTOR(),
                BASE(),
                NEW(),
                IF(),
                ELSE(),
                FOR(),
                IN(),
                WHILE(),
                RETURN(),
                CONTINUE(),
                BREAK(),
                IS(),
                IS_LIKE(),
                NULL(),
                BOOLEAN(),
                THIS()
        ));

        register("operator_assign", new TokenTypeSet(
                PLUS_ASSIGN(),
                MINUS_ASSIGN(),
                MULTIPLY_ASSIGN(),
                DIVIDE_ASSIGN(),
                PERCENT_ASSIGN(),
                POWER_ASSIGN()
        ));

        register("operator_postfix", new TokenTypeSet(
                DOUBLE_PLUS(),
                DOUBLE_MINUS()
        ));

        register("member_access", new TokenTypeSet(
                DOT(),
                QUESTION_DOT()
        ));
    }
}
