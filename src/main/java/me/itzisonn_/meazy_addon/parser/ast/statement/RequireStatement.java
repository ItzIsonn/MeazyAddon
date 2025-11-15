package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.version.Version;

@Getter
public class RequireStatement implements Statement {
    private final String id;
    private final Version version;

    public RequireStatement(String id, Version version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        return "require \"" + id + "\"" + (version == null ? "" : " \"" + version + "\"");
    }
}
