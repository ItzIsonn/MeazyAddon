package me.itzisonn_.meazy_addon.runtime.value.impl.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ConstructorClassIdentifier;
import me.itzisonn_.meazy_addon.runtime.value.impl.ModifierableRuntimeValueImpl;
import me.itzisonn_.registry.RegistryEntry;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValueImpl extends ModifierableRuntimeValueImpl<Object> implements ConstructorValue {
    protected final List<ParameterExpression> parameters;
    protected final ConstructorDeclarationEnvironment parentEnvironment;

    public ConstructorValueImpl(List<ParameterExpression> parameters, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(null, modifiers);

        if (parameters == null) throw new NullPointerException("Parameters can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");

        this.parameters = parameters;
        this.parentEnvironment = parentEnvironment;
    }



    @Override
    public boolean isAccessible(Environment environment) {
        if (!(parentEnvironment instanceof ClassEnvironment classEnvironment)) return true;
        Identifier identifier = new ConstructorClassIdentifier(classEnvironment.getId());

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getParentEnvironment(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
