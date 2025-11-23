package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConstructorDeclarationStatementParsingFunction extends AbstractParsingFunction<ConstructorDeclarationStatement> {
    public ConstructorDeclarationStatementParsingFunction() {
        super("constructor_declaration_statement");
    }

    @Override
    public ConstructorDeclarationStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);
        parser.next(AddonTokenTypes.CONSTRUCTOR(), Text.translatable("meazy_addon:parser.expected.keyword", "constructor"));

        List<ParameterExpression> parameters = ParsingHelper.parseParameters(context);

        if (modifiers.contains(AddonModifiers.NATIVE())) {
            return new ConstructorDeclarationStatement(modifiers, parameters, new ArrayList<>());
        }

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
            return new ConstructorDeclarationStatement(modifiers, parameters, new ArrayList<>());
        }

        parser.next(AddonTokenTypes.LEFT_BRACE(), Text.translatable("meazy_addon:parser.expected.start", "left_brace", "constructor_body"));
        parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy_addon:parser.expected", "new_line"));
        parser.moveOverOptionalNewLines();

        List<Statement> body = new ArrayList<>();
        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            if (parser.getCurrent().getType().equals(AddonTokenTypes.BASE())) body.add(parser.parse(AddonMain.getIdentifier("base_call_statement")));
            else body.add(parser.parse(AddonMain.getIdentifier("statement")));
            parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy_addon:parser.expected", "new_line"));
            parser.moveOverOptionalNewLines();
        }

        parser.next(AddonTokenTypes.RIGHT_BRACE(), Text.translatable("meazy_addon:parser.expected.end", "right_brace", "constructor_body"));
        return new ConstructorDeclarationStatement(modifiers, parameters, body);
    }
}
