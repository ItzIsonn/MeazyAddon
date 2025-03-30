package me.itzisonn_.meazy_addon.parser.json_converter.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramConverter extends Converter<Program> {
    public ProgramConverter() {
        super(RegistryIdentifier.ofDefault("program"));
    }

    @Override
    public Program deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        String version = getElement(object, "version").getAsString();

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new Program(version, body);
    }

    @Override
    public JsonElement serialize(Program program, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("version", program.getVersion());

        JsonArray body = new JsonArray();
        for (Statement statement : program.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }
}