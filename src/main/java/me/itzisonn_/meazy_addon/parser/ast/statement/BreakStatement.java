package me.itzisonn_.meazy_addon.parser.ast.statement;

import me.itzisonn_.meazy.parser.ast.Statement;

public class BreakStatement implements Statement {
    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        return "break";
    }
}
