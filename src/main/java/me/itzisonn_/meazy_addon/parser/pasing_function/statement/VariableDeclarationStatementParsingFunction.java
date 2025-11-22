package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.Set;

public class VariableDeclarationStatementParsingFunction extends AbstractParsingFunction<VariableDeclarationStatement> {
    public VariableDeclarationStatementParsingFunction() {
        super("variable_declaration_statement");
    }

    @Override
    public VariableDeclarationStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        if (extra.length == 1) throw new IllegalArgumentException("Expected boolean as extra argument");
        if (!(extra[1] instanceof Boolean canWithoutValue)) throw new IllegalArgumentException("Expected boolean as extra argument");

        boolean isConstant = parser.getCurrentAndNext(AddonTokenTypes.VARIABLE(), Text.translatable("meazy_addon:parser.expected.keyword", "variable")).getValue().equals("val");

        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected", "id")).getValue();

        DataType dataType = ParsingHelper.parseDataType(context);
        if (dataType == null) dataType = Registries.DATA_TYPE_FACTORY.getEntry().getValue().create();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement(modifiers, isConstant, id, dataType, null);
            }
            if (isConstant) throw new InvalidSyntaxException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.exception.constant_without_value"));
            return new VariableDeclarationStatement(modifiers, false, id, dataType, new NullLiteral());
        }

        parser.next(AddonTokenTypes.ASSIGN(), Text.translatable("meazy_addon:parser.expected.after", "assign", "id"));

        return new VariableDeclarationStatement(modifiers, isConstant, id, dataType, parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
    }
}
