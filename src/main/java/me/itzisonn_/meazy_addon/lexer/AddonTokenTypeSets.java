package me.itzisonn_.meazy_addon.lexer;

import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;

public final class AddonTokenTypeSets {
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
}
