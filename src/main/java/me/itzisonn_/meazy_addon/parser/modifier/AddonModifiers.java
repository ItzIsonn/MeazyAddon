package me.itzisonn_.meazy_addon.parser.modifier;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.registry.RegistryEntry;

/**
 * Addon modifiers registrar
 *
 * @see Registries#MODIFIERS
 */
public final class AddonModifiers {
    private static boolean hasRegistered = false;

    private AddonModifiers() {}



    public static Modifier PRIVATE() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("private")).getValue();
    }

    public static Modifier PROTECTED() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("protected")).getValue();
    }

    public static Modifier OPEN() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("open")).getValue();
    }

    public static Modifier SHARED() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("shared")).getValue();
    }

    public static Modifier ABSTRACT() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("abstract")).getValue();
    }

    public static Modifier GET() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("get")).getValue();
    }

    public static Modifier SET() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("set")).getValue();
    }

    public static Modifier DATA() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("data")).getValue();
    }

    public static Modifier FINAL() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("final")).getValue();
    }

    public static Modifier OPERATOR() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("operator")).getValue();
    }

    public static Modifier ENUM() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("enum")).getValue();
    }

    public static Modifier NATIVE() {
        return Registries.MODIFIERS.getEntry(AddonMain.getIdentifier("native")).getValue();
    }



    /**
     * Finds registered Modifier with given id
     *
     * @param id Id of Modifier
     * @return Modifier with given id or null
     */
    public static Modifier parse(String id) {
        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            if (id.equals(entry.getValue().getId())) return entry.getValue();
        }

        return null;
    }



    /**
     * Initializes {@link Registries#MODIFIERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#MODIFIERS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("Modifiers have already been initialized");
        hasRegistered = true;

        register(new PrivateModifier());
        register(new ProtectedModifier());
        register(new OpenModifier());
        register(new SharedModifier());
        register(new AbstractModifier());
        register(new GetModifier());
        register(new SetModifier());
        register(new DataModifier());
        register(new FinalModifier());
        register(new OperatorModifier());
        register(new EnumModifier());
        register(new NativeModifier());
    }

    private static void register(Modifier modifier) {
        Registries.MODIFIERS.register(AddonMain.getIdentifier(modifier.getId()), modifier);
    }
}
