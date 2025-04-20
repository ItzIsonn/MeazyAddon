package me.itzisonn_.meazy_addon.parser;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.registry.RegistryEntry;

/**
 * All basic Modifiers
 *
 * @see Registries#MODIFIERS
 */
public final class AddonModifiers {
    private static boolean isInit = false;

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



    private static void register(Modifier modifier) {
        Registries.MODIFIERS.register(AddonMain.getIdentifier(modifier.getId()), modifier);
    }

    /**
     * Initializes {@link Registries#MODIFIERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#MODIFIERS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Modifiers have already been initialized");
        isInit = true;

        register(new Modifier("private") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(ABSTRACT()) || modifierStatement.getModifiers().contains(PROTECTED()) ||
                        modifierStatement.getModifiers().contains(OPEN())) return false;

                if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                    modifierStatement instanceof ConstructorDeclarationStatement) {
                    return environment instanceof ClassEnvironment;
                }
                return false;
            }
        });

        register(new Modifier("protected") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(PRIVATE()) || modifierStatement.getModifiers().contains(OPEN())) return false;

                if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                        modifierStatement instanceof ConstructorDeclarationStatement) {
                    return environment instanceof ClassEnvironment;
                }
                return false;
            }
        });

        register(new Modifier("open") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(PRIVATE()) || modifierStatement.getModifiers().contains(PROTECTED())) return false;

                if (environment instanceof GlobalEnvironment) {
                    return modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                            modifierStatement instanceof ClassDeclarationStatement;
                }

                if (environment instanceof ClassEnvironment classEnvironment && classEnvironment.getModifiers().contains(OPEN())) {
                    return modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                            modifierStatement instanceof ConstructorDeclarationStatement;
                }

                return false;
            }
        });

        register(new Modifier("shared") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(ABSTRACT())) return false;

                if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement) {
                    return environment instanceof ClassEnvironment;
                }
                return false;
            }
        });

        register(new Modifier("abstract") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(PRIVATE()) || modifierStatement.getModifiers().contains(SHARED()) ||
                        modifierStatement.getModifiers().contains(FINAL()) || modifierStatement.getModifiers().contains(ENUM())) return false;

                if (modifierStatement instanceof ClassDeclarationStatement) return true;
                if (modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment classEnvironment) {
                    return classEnvironment.getModifiers().contains(AddonModifiers.ABSTRACT());
                }
                return false;
            }
        });

        register(new Modifier("get") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                return modifierStatement instanceof VariableDeclarationStatement && environment instanceof ClassEnvironment;
            }
        });

        register(new Modifier("set") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                return modifierStatement instanceof VariableDeclarationStatement variableDeclarationStatement && !variableDeclarationStatement.isConstant()
                        && environment instanceof ClassEnvironment;
            }
        });

        register(new Modifier("data") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                return modifierStatement instanceof ClassDeclarationStatement;
            }
        });

        register(new Modifier("final") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(ABSTRACT())) return false;

                if (modifierStatement instanceof ClassDeclarationStatement) return true;
                return modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment;
            }
        });

        register(new Modifier("operator") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(ABSTRACT()) || modifierStatement.getModifiers().contains(PRIVATE()) ||
                    modifierStatement.getModifiers().contains(PROTECTED()) || modifierStatement.getModifiers().contains(SHARED())) return false;

                return modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment;
            }
        });

        register(new Modifier("enum") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement.getModifiers().contains(ABSTRACT())) return false;

                return modifierStatement instanceof ClassDeclarationStatement;
            }
        });

        register(new Modifier("native") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (environment.getGlobalEnvironment().getNativeClasses().isEmpty()) return false;

                return modifierStatement instanceof FunctionDeclarationStatement || modifierStatement instanceof ConstructorDeclarationStatement ||
                        modifierStatement instanceof ClassDeclarationStatement;
            }
        });
    }
}
