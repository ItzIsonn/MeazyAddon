package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.parser.ast.statement.BreakStatement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class BreakStatementConverter extends Converter<BreakStatement> {
    public BreakStatementConverter() {
        super(RegistryIdentifier.ofDefault("break_statement"));
    }

    @Override
    public BreakStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new BreakStatement();
    }

    @Override
    public JsonElement serialize(BreakStatement breakStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        return getJsonObject();
    }
}