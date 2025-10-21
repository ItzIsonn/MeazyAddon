package me.itzisonn_.meazy_addon.runtime.environment;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;

public class LoopEnvironmentImpl extends VariableDeclarationEnvironmentImpl implements LoopEnvironment {
    public LoopEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent, isShared);
    }

    public LoopEnvironmentImpl(Environment parent) {
        super(parent);
    }

    @Override
    public void clearVariables() {
        variables.clear();
    }
}