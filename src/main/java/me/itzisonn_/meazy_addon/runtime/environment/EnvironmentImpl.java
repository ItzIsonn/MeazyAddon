package me.itzisonn_.meazy_addon.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.Environment;

public class EnvironmentImpl implements Environment {
    @Getter
    protected final Environment parent;
    protected final boolean isShared;

    public EnvironmentImpl(Environment parent, boolean isShared) {
        this.parent = parent;
        this.isShared = isShared;
    }

    public EnvironmentImpl(Environment parent) {
        this(parent, false);
    }

    @Override
    public boolean isShared() {
        if (isShared) return true;
        if (parent != null) return parent.isShared();
        return false;
    }
}