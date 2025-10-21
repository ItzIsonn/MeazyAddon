package me.itzisonn_.meazy_addon.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.value.VariableValue;

import java.util.HashSet;
import java.util.Set;

public class VariableDeclarationEnvironmentImpl implements VariableDeclarationEnvironment {
    @Getter
    protected final Environment parent;
    protected final boolean isShared;
    protected final Set<VariableValue> variables;

    public VariableDeclarationEnvironmentImpl(Environment parent, boolean isShared) {
        this.parent = parent;
        this.isShared = isShared;
        variables = new HashSet<>();
    }

    public VariableDeclarationEnvironmentImpl(Environment parent) {
        this(parent, false);
    }

    @Override
    public boolean isShared() {
        if (isShared) return true;
        if (parent != null) return parent.isShared();
        return false;
    }

    @Override
    public void declareVariable(VariableValue value) {
        if (value.isArgument()) {
            if (getVariable(value.getId()) != null) {
                throw new InvalidSyntaxException("Variable with id " + value.getId() + " already exists");
            }
        }
        else if (getVariableDeclarationEnvironment(value.getId()) != null) {
            throw new InvalidSyntaxException("Variable with id " + value.getId() + " already exists");
        }
        variables.add(value);
    }

    @Override
    public Set<VariableValue> getVariables() {
        return new HashSet<>(variables);
    }
}