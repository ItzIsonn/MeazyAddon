package me.itzisonn_.meazy_addon.parser.json_converter.expression.collection_creation;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapCreationExpressionConverter extends Converter<MapCreationExpression> {
    public MapCreationExpressionConverter() {
        super(RegistryIdentifier.ofDefault("map_creation_expression"));
    }

    @Override
    public MapCreationExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Map<Expression, Expression> map = new HashMap<>();
        if (object.get("map") != null) {
            JsonArray pairsList = getElement(object, "map").getAsJsonArray();

            for (JsonElement pairElement : pairsList) {
                JsonObject pair = pairElement.getAsJsonObject();
                map.put(jsonDeserializationContext.deserialize(pair.get("key"), Expression.class),
                        jsonDeserializationContext.deserialize(pair.get("value"), Expression.class));
            }
        }

        return new MapCreationExpression(map);
    }

    @Override
    public JsonElement serialize(MapCreationExpression mapCreationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray map = new JsonArray();
        for (Expression key : mapCreationExpression.getMap().keySet()) {
            JsonObject pair = new JsonObject();
            pair.add("key", jsonSerializationContext.serialize(key));
            pair.add("value", jsonSerializationContext.serialize(mapCreationExpression.getMap().get(key)));
            map.add(pair);
        }
        result.add("map", map);

        return result;
    }
}