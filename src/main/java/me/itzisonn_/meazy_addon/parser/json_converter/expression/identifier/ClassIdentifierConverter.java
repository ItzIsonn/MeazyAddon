package me.itzisonn_.meazy_addon.parser.json_converter.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class ClassIdentifierConverter extends Converter<ClassIdentifier> {
    public ClassIdentifierConverter() {
        super(AddonMain.getIdentifier("class_identifier"));
    }

    @Override
    public ClassIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new ClassIdentifier(getElement(object, "id").getAsString());
    }

    @Override
    public JsonElement serialize(ClassIdentifier classIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", classIdentifier.getId());

        return result;
    }
}