package me.itzisonn_.meazy_addon.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Expression;

import java.util.List;

@Getter
public class BaseCallStatement implements Statement {
    protected final String id;
    protected final List<Expression> args;

    public BaseCallStatement(String id, List<Expression> args) {
        this.id = id;
        this.args = args;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            argsBuilder.append(args.get(i).toCodeString());
            if (i != args.size() - 1) argsBuilder.append(", ");
        }

        return "base " + id + "(" + argsBuilder + ")";
    }
}