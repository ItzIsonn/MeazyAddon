package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.ImportStatement;

import java.lang.reflect.Type;

public class ImportStatementConverter extends Converter<ImportStatement> {
    public ImportStatementConverter() {
        super(AddonMain.getIdentifier("import_statement"));
    }

    @Override
    public ImportStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new ImportStatement(getElement(object, "file").getAsString());
    }

    @Override
    public JsonElement serialize(ImportStatement importStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();
        result.addProperty("file", importStatement.getFile());
        return result;
    }
}