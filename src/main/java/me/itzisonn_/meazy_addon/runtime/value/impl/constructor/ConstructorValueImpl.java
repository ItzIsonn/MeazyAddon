package me.itzisonn_.meazy_addon.runtime.value.impl.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.constructor.ConstructorValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.RuntimeValueImpl;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ConstructorValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValueImpl extends RuntimeValueImpl<Object> implements ConstructorValue {
    protected final List<CallArgExpression> args;
    protected final ConstructorDeclarationEnvironment parentEnvironment;
    protected final Set<Modifier> modifiers;

    /**
     * @param args Args
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, parentEnvironment or modifiers is null
     */
    public ConstructorValueImpl(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(null);

        if (args == null) throw new NullPointerException("Args can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");
        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");

        this.args = args;
        this.parentEnvironment = parentEnvironment;
        this.modifiers = modifiers;
    }
}
