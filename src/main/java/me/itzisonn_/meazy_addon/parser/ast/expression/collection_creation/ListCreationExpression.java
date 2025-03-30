package me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Expression;

import java.util.List;

@Getter
public class ListCreationExpression implements Expression {
    private final List<Expression> list;

    public ListCreationExpression(List<Expression> list) {
        this.list = list;
    }

    @Override
    public String toCodeString() {
        StringBuilder listBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            listBuilder.append(list.get(i).toCodeString());
            if (i != list.size() - 1) listBuilder.append(", ");
        }

        return "[" + listBuilder + "]";
    }
}
