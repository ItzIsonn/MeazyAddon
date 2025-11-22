package me.itzisonn_.meazy_addon.parser.pasing_function.statement;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
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

        parser.getCurrentAndNext(AddonTokenTypes.CLASS(), Text.translatable("meazy_addon:parser.expected.keyword", "class"));
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected.after_keyword", "id", "class")).getValue();

        List<Statement> generatedBody = new ArrayList<>();
        if (modifiers.contains(AddonModifiers.DATA())) {
            generatedBody.addAll(generateDataBody(id, ParsingHelper.parseParameters(context)));
            modifiers.remove(AddonModifiers.DATA());
        }

        Set<String> baseClasses = new HashSet<>();
        int baseClassesLineNumber = -1;

        if (parser.getCurrent().getType().equals(AddonTokenTypes.COLON())) {
            baseClassesLineNumber = parser.getCurrent().getLine();
            do {
                parser.next();
                baseClasses.add(parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected", "id")).getValue());
            }
            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA()));
        }

        boolean hasNewLine = parser.getCurrent().getType().equals(TokenTypes.NEW_LINE());
        parser.moveOverOptionalNewLines();

        if (!parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE()) && hasNewLine) {
            return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.next(AddonTokenTypes.LEFT_BRACE(), Text.translatable("meazy_addon:parser.expected.start", "left_brace", "class_body"));

        if (parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            parser.next();
            return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), Text.translatable("meazy_addon:parser.expected", "new_line"));
        parser.moveOverOptionalNewLines();

        LinkedHashMap<String, List<Expression>> enumIds = new LinkedHashMap<>();
        if (modifiers.contains(AddonModifiers.ENUM())) {
            if (!baseClasses.isEmpty()) throw new InvalidSyntaxException(baseClassesLineNumber, Text.translatable("meazy_addon:parser.exception.enums.base_classes"));

            String enumId = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected", "id")).getValue();
            List<Expression> args;
            if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PARENTHESIS())) args = ParsingHelper.parseArgs(context);
            else args = new ArrayList<>();
            enumIds.put(enumId, args);

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.next();
                parser.moveOverOptionalNewLines();

                int lineNumber = parser.getCurrent().getLine();
                enumId = parser.getCurrentAndNext(AddonTokenTypes.ID(), Text.translatable("meazy_addon:parser.expected", "id")).getValue();
                if (enumIds.containsKey(enumId)) throw new InvalidSyntaxException(lineNumber, Text.translatable("meazy_addon:parser.exception.enums.duplicated_entries"));

                if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PARENTHESIS())) args = ParsingHelper.parseArgs(context);
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

        parser.next(AddonTokenTypes.RIGHT_BRACE(), Text.translatable("meazy_addon:parser.expected.end", "right_brace", "class_body"));
        return new ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds);
    }



    private static List<Statement> generateDataBody(String id, List<ParameterExpression> dataVariables) {
        List<Statement> body = new ArrayList<>();

        for (ParameterExpression dataVariable : dataVariables) {
            body.add(new VariableDeclarationStatement(
                    Set.of(AddonModifiers.PRIVATE()),
                    dataVariable.isConstant(),
                    List.of(new VariableDeclarationStatement.VariableDeclarationInfo(dataVariable.getId(), dataVariable.getDataType(), null))));
        }

        List<Statement> constructorBody = new ArrayList<>();
        for (ParameterExpression callArgExpression : dataVariables) {
            constructorBody.add(new AssignmentExpression(new MemberExpression(new ThisLiteral(), new VariableIdentifier(callArgExpression.getId()), false), new VariableIdentifier(callArgExpression.getId())));
        }
        body.add(new ConstructorDeclarationStatement(Set.of(), dataVariables, constructorBody));

        for (ParameterExpression dataVariable : dataVariables) {
            body.add(getGetFunction(dataVariable.getId(), dataVariable.getDataType()));
        }

        for (ParameterExpression dataVariable : dataVariables) {
            if (dataVariable.isConstant()) continue;
            body.add(getSetFunction(dataVariable.getId(), dataVariable.getDataType()));
        }

        Expression toStringExpression = new StringLiteral(id + "(" + (dataVariables.isEmpty() ? ")" : ""));
        for (int i = 0; i < dataVariables.size(); i++) {
            ParameterExpression dataVariable = dataVariables.get(i);

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
                Registries.DATA_TYPE_FACTORY.getEntry().getValue().create("String", false)));

        List<Expression> copyArgs = new ArrayList<>();
        for (ParameterExpression dataVariable : dataVariables) {
            copyArgs.add(new VariableIdentifier(dataVariable.getId()));
        }
        body.add(new FunctionDeclarationStatement(
                Set.of(),
                "copy",
                List.of(),
                List.of(new ReturnStatement(new ClassCallExpression(new ClassIdentifier(id), copyArgs))),
                Registries.DATA_TYPE_FACTORY.getEntry().getValue().create(id, false)));

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
                ParameterExpression dataVariable = dataVariables.get(i);
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
                List.of(new ParameterExpression("value", Registries.DATA_TYPE_FACTORY.getEntry().getValue().create(), true)),
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
                Registries.DATA_TYPE_FACTORY.getEntry().getValue().create("Boolean", false)
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
                List.of(new ParameterExpression(id, dataType, true)),
                List.of(new AssignmentExpression(new MemberExpression(new ThisLiteral(), new VariableIdentifier(id), false), new VariableIdentifier(id))),
                null);
    }
}
