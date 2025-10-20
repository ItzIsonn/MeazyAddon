package me.itzisonn_.meazy_addon.runtime.value.impl.constructor;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.constructor.NativeConstructorValue;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link NativeConstructorValue}
 */
public abstract class NativeConstructorValueImpl extends ConstructorValueImpl implements NativeConstructorValue {
    /**
     * @param args Args
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, parentEnvironment or modifiers is null
     */
    public NativeConstructorValueImpl(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(args, parentEnvironment, modifiers);
    }
}