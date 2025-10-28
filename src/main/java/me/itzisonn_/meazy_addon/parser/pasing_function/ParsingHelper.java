package me.itzisonn_.meazy_addon.parser.pasing_function;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;

import java.util.*;

public final class ParsingHelper {
    private ParsingHelper() {}

    public static Set<Modifier> parseModifiers(ParsingContext context) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = new HashSet<>();

        while (parser.getCurrent().getType().equals(AddonTokenTypes.ID())) {
            String id = parser.getCurrent().getValue();
            Modifier modifier = AddonModifiers.parse(id);
            if (modifier == null) {
                if (modifiers.isEmpty()) return modifiers;
                throw new InvalidStatementException("Modifier with id " + id + " doesn't exist");
            }
            parser.getCurrentAndNext();
            modifiers.add(modifier);
        }

        return modifiers;
    }

    public static Map<String, Version> parseRequiredAddons(ParsingContext context) {
        Parser parser = context.getParser();
        Map<String, Version> requiredAddons = new HashMap<>();

        parser.moveOverOptionalNewLines();

        while (parser.getCurrent().getType().equals(AddonTokenTypes.REQUIRE())) {
            parser.getCurrentAndNext();

            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected id after require keyword").getValue();
            Version version;
            if (parser.getCurrent().getType().equals(AddonTokenTypes.STRING())) {
                String value = parser.getCurrentAndNext().getValue();
                version = Version.of(value.substring(1, value.length() - 1));
            }
            else version = null;
            requiredAddons.put(id, version);

            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line in the end of the require statement");
            parser.moveOverOptionalNewLines();
        }

        return requiredAddons;
    }

    @SuppressWarnings("unchecked")
    public static Set<Modifier> getModifiersFromExtra(Object[] extra) {
        if (extra.length == 0) throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        if (!(extra[0] instanceof Set<?> set)) throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        try {
            return (Set<Modifier>) set;
        }
        catch (ClassCastException ignore) {
            throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        }
    }

    public static List<ParameterExpression> parseParameters(ParsingContext context) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open parameters");
        List<ParameterExpression> parameters = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
            parameters.add(parser.parse(AddonMain.getIdentifier("parameter_expression"), ParameterExpression.class));

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.getCurrentAndNext();
                parameters.add(parser.parse(AddonMain.getIdentifier("parameter_expression"), ParameterExpression.class));
            }
        }

        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close parameters");
        return parameters;
    }

    public static List<Expression> parseArgs(ParsingContext context) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<Expression> args = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
            args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.getCurrentAndNext();
                args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
            }
        }

        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    public static DataType parseDataType(ParsingContext context) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.COLON())) {
            parser.getCurrentAndNext();
            String dataTypeId = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Must specify variable's data type after colon").getValue();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.QUESTION())) {
                parser.getCurrentAndNext();
                return Registries.DATA_TYPE_FACTORY.getEntry().getValue().create(dataTypeId, true);
            }
            return Registries.DATA_TYPE_FACTORY.getEntry().getValue().create(dataTypeId, false);
        }
        return null;
    }

    public static List<Statement> parseBody(ParsingContext context) {
        Parser parser = context.getParser();

        List<Statement> body = new ArrayList<>();
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
        parser.moveOverOptionalNewLines();

        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            body.add(parser.parse(AddonMain.getIdentifier("statement")));
            parser.moveOverOptionalNewLines();
        }

        parser.moveOverOptionalNewLines();
        return body;
    }
}
