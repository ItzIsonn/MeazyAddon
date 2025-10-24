package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy_addon.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy_addon.parser.pasing_function.ParsingHelper;

import java.util.*;

public class ClassDeclarationStatementParsingFunction extends AbstractParsingFunction<ClassDeclarationStatement> {
    public ClassDeclarationStatementParsingFunction() {
        super("class_declaration_statement");
    }

    @Override
    public ClassDeclarationStatement parse(ParsingContext context, Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        parser.getCurrentAndNext(AddonTokenTypes.CLASS(), "Expected class keyword");
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected id after class keyword").getValue();

        List<Statement> generatedBody = new ArrayList<>();
        if (modifiers.contains(AddonModifiers.DATA())) {
            generatedBody.addAll(generateDataBody(id, ParsingHelper.parseCallArgs(context)));
            modifiers.remove(AddonModifiers.DATA());
        }

        Set<String> baseClasses = new HashSet<>();
        if (parser.getCurrent().getType().equals(AddonTokenTypes.COLON())) {
            parser.getCurrentAndNext();
            baseClasses.add(parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected id as base class").getValue());

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.getCurrentAndNext();
                baseClasses.add(parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected id as base class after comma").getValue());
            }
        }

        boolean hasNewLine = parser.getCurrent().getType().equals(TokenTypes.NEW_LINE());
        parser.moveOverOptionalNewLines();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE()) && hasNewLine) {
            return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open class body");

        if (parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            parser.getCurrentAndNext();
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
            parser.moveOverOptionalNewLines();
            return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
        parser.moveOverOptionalNewLines();

        LinkedHashMap<String, List<Expression>> enumIds = new LinkedHashMap<>();
        if (modifiers.contains(AddonModifiers.ENUM())) {
            if (!baseClasses.isEmpty()) throw new InvalidSyntaxException("Enum class can't have base classes");

            String enumId = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected enum member id").getValue();
            List<Expression> args;
            if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) args = ParsingHelper.parseArgs(context);
            else args = new ArrayList<>();
            enumIds.put(enumId, args);

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.getCurrentAndNext();
                parser.moveOverOptionalNewLines();

                enumId = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected enum member id").getValue();
                if (enumIds.containsKey(enumId)) throw new InvalidSyntaxException("Enum class can't have duplicated entries");
                if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) args = ParsingHelper.parseArgs(context);
                else args = new ArrayList<>();
                enumIds.put(enumId, args);
            }

            parser.moveOverOptionalNewLines();
        }

        List<Statement> body = new ArrayList<>(generatedBody);
        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            Statement statement = parser.parse(AddonMain.getIdentifier("class_body_statement"));
            body.add(statement);

            if (statement instanceof VariableDeclarationStatement variableDeclarationStatement) {
                if (variableDeclarationStatement.getModifiers().contains(AddonModifiers.GET())) {
                    for (VariableDeclarationStatement.VariableDeclarationInfo variableDeclarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
                        body.add(getGetFunction(variableDeclarationInfo.getId(), variableDeclarationInfo.getDataType()));
                    }
                    variableDeclarationStatement.getModifiers().remove(AddonModifiers.GET());
                }
                if (variableDeclarationStatement.getModifiers().contains(AddonModifiers.SET()) && !variableDeclarationStatement.isConstant()) {
                    for (VariableDeclarationStatement.VariableDeclarationInfo variableDeclarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
                        body.add(getSetFunction(variableDeclarationInfo.getId(), variableDeclarationInfo.getDataType()));
                    }
                    variableDeclarationStatement.getModifiers().remove(AddonModifiers.SET());
                }
            }

            parser.moveOverOptionalNewLines();
        }

        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close class body");
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the class declaration");

        return new ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds);
    }



    private static List<Statement> generateDataBody(String id, List<CallArgExpression> dataVariables) {
        List<Statement> body = new ArrayList<>();

        for (CallArgExpression dataVariable : dataVariables) {
            body.add(new VariableDeclarationStatement(
                    Set.of(AddonModifiers.PRIVATE()),
                    dataVariable.isConstant(),
                    List.of(new VariableDeclarationStatement.VariableDeclarationInfo(dataVariable.getId(), dataVariable.getDataType(), null))));
        }

        List<Statement> constructorBody = new ArrayList<>();
        for (CallArgExpression callArgExpression : dataVariables) {
            constructorBody.add(new AssignmentExpression(new MemberExpression(new ThisLiteral(), new VariableIdentifier(callArgExpression.getId()), false), new VariableIdentifier(callArgExpression.getId())));
        }
        body.add(new ConstructorDeclarationStatement(Set.of(), dataVariables, constructorBody));

        for (CallArgExpression dataVariable : dataVariables) {
            body.add(getGetFunction(dataVariable.getId(), dataVariable.getDataType()));
        }

        for (CallArgExpression dataVariable : dataVariables) {
            if (dataVariable.isConstant()) continue;
            body.add(getSetFunction(dataVariable.getId(), dataVariable.getDataType()));
        }

        Expression toStringExpression = new StringLiteral(id + "(" + (dataVariables.isEmpty() ? ")" : ""));
        for (int i = 0; i < dataVariables.size(); i++) {
            CallArgExpression dataVariable = dataVariables.get(i);

            Expression endingExpression;
            if (i == dataVariables.size() - 1) endingExpression = new OperatorExpression(
                    new VariableIdentifier(dataVariable.getId()),
                    new StringLiteral(")"),
                    "+", OperatorType.INFIX
            );
            else endingExpression = new VariableIdentifier(dataVariable.getId());

            toStringExpression = new OperatorExpression(
                    toStringExpression,
                    new OperatorExpression(
                            new StringLiteral((i == 0 ? "" : ",") + dataVariable.getId() + "="),
                            endingExpression,
                            "+", OperatorType.INFIX),
                    "+", OperatorType.INFIX);
        }
        body.add(new FunctionDeclarationStatement(
                Set.of(),
                "toString",
                List.of(),
                List.of(new ReturnStatement(toStringExpression)),
                new DataTypeImpl("String", false)));

        List<Expression> copyArgs = new ArrayList<>();
        for (CallArgExpression dataVariable : dataVariables) {
            copyArgs.add(new VariableIdentifier(dataVariable.getId()));
        }
        body.add(new FunctionDeclarationStatement(
                Set.of(),
                "copy",
                List.of(),
                List.of(new ReturnStatement(new ClassCallExpression(new ClassIdentifier(id), copyArgs))),
                new DataTypeImpl(id, false)));

        Expression equalsExpression;
        if (!dataVariables.isEmpty()) {
            equalsExpression = new OperatorExpression(
                    new VariableIdentifier(dataVariables.getFirst().getId()),
                    new MemberExpression(
                            new VariableIdentifier("value"),
                            new FunctionCallExpression(
                                    new FunctionIdentifier(AddonUtils.generatePrefixedName("get", dataVariables.getFirst().getId())),
                                    List.of()),
                            false),
                    "==", OperatorType.INFIX);
            for (int i = 1; i < dataVariables.size(); i++) {
                CallArgExpression dataVariable = dataVariables.get(i);
                equalsExpression = new OperatorExpression(
                        equalsExpression,
                        new OperatorExpression(
                                new VariableIdentifier(dataVariable.getId()),
                                new MemberExpression(
                                        new VariableIdentifier("value"),
                                        new FunctionCallExpression(
                                                new FunctionIdentifier(AddonUtils.generatePrefixedName("get", dataVariable.getId())),
                                                List.of()),
                                        false),
                                "==", OperatorType.INFIX),
                        "&&", OperatorType.INFIX
                );
            }
        }
        else equalsExpression = new BooleanLiteral(true);
        body.add(new FunctionDeclarationStatement(
                Set.of(AddonModifiers.OPERATOR()),
                "equals",
                List.of(new CallArgExpression("value", new DataTypeImpl("Any", true), true)),
                List.of(
                        new IfStatement(
                                new OperatorExpression(new VariableIdentifier("value"), new NullLiteral(), "==", OperatorType.INFIX),
                                List.of(new ReturnStatement(new BooleanLiteral(false))),
                                null),
                        new IfStatement(
                                new OperatorExpression(new IsExpression(new VariableIdentifier("value"), id, true), null, "!", OperatorType.PREFIX),
                                List.of(new ReturnStatement(new BooleanLiteral(false))),
                                null),
                        new ReturnStatement(equalsExpression)),
                new DataTypeImpl("Boolean", false)
        ));

        return body;
    }

    private static FunctionDeclarationStatement getGetFunction(String id, DataType dataType) {
        return new FunctionDeclarationStatement(
                Set.of(),
                AddonUtils.generatePrefixedName("get", id),
                List.of(),
                List.of(new ReturnStatement(new VariableIdentifier(id))),
                dataType);
    }

    private static FunctionDeclarationStatement getSetFunction(String id, DataType dataType) {
        return new FunctionDeclarationStatement(
                Set.of(),
                AddonUtils.generatePrefixedName("set", id),
                List.of(new CallArgExpression(id, dataType, true)),
                List.of(new AssignmentExpression(new MemberExpression(new ThisLiteral(), new VariableIdentifier(id), false), new VariableIdentifier(id))),
                null);
    }
}
