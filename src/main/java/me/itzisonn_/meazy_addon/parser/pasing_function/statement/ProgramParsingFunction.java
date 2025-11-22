package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonInfo;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.statement.RequireStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramParsingFunction extends AbstractParsingFunction<Program> {
    public ProgramParsingFunction() {
        super("program");
    }

    @Override
    public Program parse(ParsingContext context, Object... extra) {
        File file;
        if (extra.length == 0) throw new IllegalArgumentException("Expected file as extra argument");
        if (extra[0] == null) file = null;
        else if (extra[0] instanceof File fileArg) file = fileArg;
        else throw new IllegalArgumentException("Expected file as extra argument");

        Parser parser = context.getParser();
        parser.moveOverOptionalNewLines();

        Map<String, Version> requiredAddons = new HashMap<>();
        List<Statement> body = new ArrayList<>();

        Statement headerStatement = parser.parse(AddonMain.getIdentifier("header_statement"));

        while (headerStatement != null) {
            parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy_addon:parser.expected", "new_line"));
            parser.moveOverOptionalNewLines();

            if (headerStatement instanceof RequireStatement requireStatement) {
                requiredAddons.put(requireStatement.getId(), requireStatement.getVersion());
            }
            else {
                body.add(headerStatement);
            }

            headerStatement = parser.parse(AddonMain.getIdentifier("header_statement"));
        }

        parser.moveOverOptionalNewLines();

        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
            body.add(parser.parse(AddonMain.getIdentifier("global_statement")));

            if (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected", "new_line"));
                parser.moveOverOptionalNewLines();
            }
        }

        if (requiredAddons.isEmpty()) {
            for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                AddonInfo addonInfo = addon.getAddonInfo();
                requiredAddons.put(addonInfo.getId(), addonInfo.getVersion());
            }
        }

        return new Program(file, MeazyMain.VERSION, requiredAddons, body);
    }
}
