package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;

@Getter
public class ImportStatement implements Statement {
    private final String file;

    public ImportStatement(String file) {
        this.file = file;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        return "import \"" + file + "\"";
    }
}
