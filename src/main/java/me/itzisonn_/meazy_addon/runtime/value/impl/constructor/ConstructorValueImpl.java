package me.itzisonn_.meazy_addon.runtime.value.impl.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.constructor.ConstructorValue;
import me.itzisonn_.meazy_addon.runtime.value.impl.ModifierableRuntimeValueImpl;
import me.itzisonn_.registry.RegistryEntry;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ConstructorValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValueImpl extends ModifierableRuntimeValueImpl<Object> implements ConstructorValue {
    protected final List<CallArgExpression> args;
    protected final ConstructorDeclarationEnvironment parentEnvironment;

    /**
     * @param args Args
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, parentEnvironment or modifiers is null
     */
    public ConstructorValueImpl(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(null, modifiers);

        if (args == null) throw new NullPointerException("Args can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");

        this.args = args;
        this.parentEnvironment = parentEnvironment;
    }



    @Override
    public boolean isAccessible(Environment environment) {
        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getParentEnvironment(), null, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
