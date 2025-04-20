package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;

@Getter
public class UsingStatement implements Statement {
    private final String className;

    public UsingStatement(String className) {
        this.className = className;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        return "using \"" + className + "\"";
    }
}
