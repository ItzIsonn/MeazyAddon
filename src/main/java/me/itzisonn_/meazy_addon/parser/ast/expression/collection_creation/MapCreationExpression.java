package me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MapCreationExpression implements Expression {
    private final Map<Expression, Expression> map;

    public MapCreationExpression(Map<Expression, Expression> map) {
        this.map = map;
    }

    @Override
    public String toCodeString() {
        StringBuilder mapBuilder = new StringBuilder();
        List<Expression> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < map.size(); i++) {
            Expression key = keys.get(i);
            mapBuilder.append(key.toCodeString()).append("=").append(map.get(key).toCodeString());
            if (i != map.size() - 1) mapBuilder.append(", ");
        }

        return "{" + mapBuilder + "}";
    }
}
