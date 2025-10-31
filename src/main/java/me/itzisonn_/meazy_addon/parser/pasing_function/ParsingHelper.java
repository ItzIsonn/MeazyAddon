package me.itzisonn_.meazy_addon.parser.pasing_function;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
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
                throw new InvalidStatementException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.modifier.doesnt_exist", id));
            }

            parser.next();
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

            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected.after_keyword", "id", "require")).getValue();
            Version version;

            if (parser.getCurrent().getType().equals(AddonTokenTypes.STRING())) {
                String value = parser.getCurrentAndNext().getValue();
                version = Version.of(value.substring(1, value.length() - 1));
            }
            else version = null;
            requiredAddons.put(id, version);

            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "require"));
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

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.start_expression", "left_parenthesis", "parameters"));
        List<ParameterExpression> parameters = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PARENTHESIS())) {
            parameters.add(parser.parse(AddonMain.getIdentifier("parameter_expression"), ParameterExpression.class));

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.next();
                parameters.add(parser.parse(AddonMain.getIdentifier("parameter_expression"), ParameterExpression.class));
            }
        }

        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.end_expression", "right_parenthesis", "parameters"));
        return parameters;
    }

    public static List<Expression> parseArgs(ParsingContext context) {
        Parser parser = context.getParser();
        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.start_expression", "left_parenthesis", "args"));
        List<Expression> args = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PARENTHESIS())) {
            args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.next();
                args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
            }
        }

        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PARENTHESIS(), Text.translatable("meazy_addon:parser.expected.end_expression", "right_parenthesis", "args"));
        return args;
    }

    public static DataType parseDataType(ParsingContext context) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.COLON())) {
            parser.getCurrentAndNext();
            String dataTypeId = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected.after", "id", "colon")).getValue();

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
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected", "new_line"));
        parser.moveOverOptionalNewLines();

        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            body.add(parser.parse(AddonMain.getIdentifier("statement")));
            parser.moveOverOptionalNewLines();
        }

        parser.moveOverOptionalNewLines();
        return body;
    }
}
