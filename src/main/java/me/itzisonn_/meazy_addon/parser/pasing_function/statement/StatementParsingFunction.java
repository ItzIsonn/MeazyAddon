package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.Set;

public class StatementParsingFunction extends AbstractParsingFunction<Statement> {
    public StatementParsingFunction() {
        super("statement");
    }

    @Override
    public Statement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.parseModifiers(context);

        if (parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
            VariableDeclarationStatement variableDeclarationStatement =
                    parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
            parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected.end_statement", "new_line", "variable_declaration"));
            parser.moveOverOptionalNewLines();
            return variableDeclarationStatement;
        }
        if (!modifiers.isEmpty()) throw new InvalidSyntaxException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.modifier.unexpected"));

        if (parser.getCurrent().getType().equals(AddonTokenTypes.IF())) return parser.parse(AddonMain.getIdentifier("if_statement"));
        if (parser.getCurrent().getType().equals(AddonTokenTypes.FOR())) return parser.parse(AddonMain.getIdentifier("for_statement"));
        if (parser.getCurrent().getType().equals(AddonTokenTypes.WHILE())) return parser.parse(AddonMain.getIdentifier("while_statement"));
        if (parser.getCurrent().getType().equals(AddonTokenTypes.RETURN())) return parser.parse(AddonMain.getIdentifier("return_statement"));
        if (parser.getCurrent().getType().equals(AddonTokenTypes.CONTINUE())) return parser.parse(AddonMain.getIdentifier("continue_statement"));
        if (parser.getCurrent().getType().equals(AddonTokenTypes.BREAK())) return parser.parse(AddonMain.getIdentifier("break_statement"));

        Expression expression = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
        if (expression instanceof FunctionCallExpression || expression instanceof ClassCallExpression || expression instanceof AssignmentExpression || expression instanceof MemberExpression) {
            parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected", "new_line"));
            return expression;
        }

        throw new InvalidStatementException(parser.getCurrent().getLine(), Text.translatable("meazy_addon:parser.exception.statement"));
    }
}
