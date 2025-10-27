package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.ArrayList;
import java.util.List;
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

        boolean isConstant = parser.getCurrentAndNext(AddonTokenTypes.VARIABLE(), "Expected variable keyword").getValue().equals("val");

        List<VariableDeclarationStatement.VariableDeclarationInfo> declarations = new ArrayList<>();
        declarations.add(parseVariableDeclarationInfo(context, isConstant, canWithoutValue));

        while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
            parser.getCurrentAndNext();
            declarations.add(parseVariableDeclarationInfo(context, isConstant, canWithoutValue));
        }

        return new VariableDeclarationStatement(modifiers, isConstant, declarations);
    }



    private static VariableDeclarationStatement.VariableDeclarationInfo parseVariableDeclarationInfo(ParsingContext context, boolean isConstant, boolean canWithoutValue) {
        Parser parser = context.getParser();
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier in variable declaration statement").getValue();

        DataType dataType = ParsingHelper.parseDataType(context);
        if (dataType == null) dataType = Registries.DATA_TYPE_FACTORY.getEntry().getValue().create();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, null);
            }
            if (isConstant) throw new InvalidStatementException("Can't declare a constant variable without a value", parser.getCurrent().getLine());
            return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, new NullLiteral());
        }

        parser.getCurrentAndNext(AddonTokenTypes.ASSIGN(), "Expected ASSIGN token after the id in variable declaration");

        return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
    }
}
