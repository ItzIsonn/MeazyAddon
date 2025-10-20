package me.itzisonn_.meazy_addon.parser;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.*;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.AddonUtils;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.AddonTokenTypes;
import me.itzisonn_.meazy_addon.parser.ast.expression.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.*;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.CallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.*;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;

import java.util.*;

/**
 * Addon parsing functions registrar
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class AddonParsingFunctions {
    private static boolean hasRegistered = false;

    private AddonParsingFunctions() {}



    /**
     * Initializes {@link Registries#PARSING_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#PARSING_FUNCTIONS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("ParsingFunctions have already been initialized");
        hasRegistered = true;

        register("global_statement", (context, extra) -> {
            Parser parser = context.getParser();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.IMPORT())) {
                return parser.parse(AddonMain.getIdentifier("import_statement"), ImportStatement.class);
            }
            if (parser.getCurrent().getType().equals(AddonTokenTypes.USING())) {
                return parser.parse(AddonMain.getIdentifier("using_statement"), UsingStatement.class);
            }

            Set<Modifier> modifiers = parseModifiers(context);
            if (parser.getCurrent().getType().equals(AddonTokenTypes.CLASS())) {
                return parser.parse(AddonMain.getIdentifier("class_declaration_statement"), ClassDeclarationStatement.class, modifiers);
            }
            if (parser.getCurrent().getType().equals(AddonTokenTypes.FUNCTION())) {
                return parser.parse(AddonMain.getIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
            }
            if (parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                parser.moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }

            throw new InvalidStatementException("At global environment you only can declare variable, function or class", parser.getCurrent().getLine());
        });

        register("import_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.IMPORT(), "Expected import keyword");
            String file = parser.getCurrentAndNext(AddonTokenTypes.STRING(), "Expected file path after import keyword").getValue();
            return new ImportStatement(file.substring(1, file.length() - 1));
        });

        register("using_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.USING(), "Expected using keyword");
            String nativeClass = parser.getCurrentAndNext(AddonTokenTypes.STRING(), "Expected native class name after using keyword").getValue();
            return new UsingStatement(nativeClass.substring(1, nativeClass.length() - 1));
        });

        register("class_declaration_statement", (context, extra) -> {
            Parser parser = context.getParser();

            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            parser.getCurrentAndNext(AddonTokenTypes.CLASS(), "Expected class keyword");
            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected id after class keyword").getValue();

            List<Statement> generatedBody = new ArrayList<>();
            if (modifiers.contains(AddonModifiers.DATA())) {
                generatedBody.addAll(generateDataBody(id, parseCallArgs(context)));
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
                if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) args = parseArgs(context);
                else args = new ArrayList<>();
                enumIds.put(enumId, args);

                while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                    parser.getCurrentAndNext();
                    parser.moveOverOptionalNewLines();

                    enumId = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected enum member id").getValue();
                    if (enumIds.containsKey(enumId)) throw new InvalidSyntaxException("Enum class can't have duplicated entries");
                    if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) args = parseArgs(context);
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
        });

        register("class_body_statement", (context, extra) -> {
            Parser parser = context.getParser();

            Set<Modifier> modifiers = parseModifiers(context);

            if (parser.getCurrent().getType().equals(AddonTokenTypes.FUNCTION())) {
                return parser.parse(AddonMain.getIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
            }
            if (parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, true);
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                parser.moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (parser.getCurrent().getType().equals(AddonTokenTypes.CONSTRUCTOR())) {
                return parser.parse(AddonMain.getIdentifier("constructor_declaration_statement"), ConstructorDeclarationStatement.class, modifiers);
            }

            throw new InvalidStatementException("Invalid statement found", parser.getCurrent().getLine());
        });

        register("function_declaration_statement", (context, extra) -> {
            Parser parser = context.getParser();

            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            parser.getCurrentAndNext(AddonTokenTypes.FUNCTION(), "Expected function keyword");

            String classId = null;
            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after function keyword").getValue();
            if (parser.getCurrent().getType().equals(AddonTokenTypes.DOT())) {
                parser.getCurrentAndNext();
                classId = id;
                id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after function keyword").getValue();
            }

            List<CallArgExpression> args = parseCallArgs(context);
            DataType dataType = parseDataType(context);

            if (modifiers.contains(AddonModifiers.ABSTRACT()) || modifiers.contains(AddonModifiers.NATIVE())) {
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
                return new FunctionDeclarationStatement(modifiers, id, args, new ArrayList<>(), dataType);
            }

            List<Statement> body;
            if (parser.getCurrent().getType().equals(AddonTokenTypes.ARROW())) {
                parser.getCurrentAndNext();
                body = new ArrayList<>(List.of(parser.parse(AddonMain.getIdentifier("statement"))));
            }
            else {
                parser.moveOverOptionalNewLines();
                parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open function body");
                body = parseBody(context);
                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close function body");
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
            }

            return new FunctionDeclarationStatement(modifiers, id, classId, args, body, dataType);
        });

        register("function_arg", (context, extra) -> {
            Parser parser = context.getParser();

            if (!parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE()))
                throw new UnexpectedTokenException("Expected variable keyword at the beginning of function arg", parser.getCurrent().getLine());
            boolean isConstant = parser.getCurrentAndNext().getValue().equals("val");
            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after variable keyword in function arg").getValue();

            DataType dataType = parseDataType(context);
            return new CallArgExpression(id, dataType == null ? new DataTypeImpl("Any", true) : dataType, isConstant);
        });

        register("variable_declaration_statement", (context, extra) -> {
            Parser parser = context.getParser();

            Set<Modifier> modifiers = getModifiersFromExtra(extra);

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
        });

        register("constructor_declaration_statement", (context, extra) -> {
            Parser parser = context.getParser();

            Set<Modifier> modifiers = getModifiersFromExtra(extra);
            parser.getCurrentAndNext(AddonTokenTypes.CONSTRUCTOR(), "Expected constructor keyword");

            List<CallArgExpression> args = parseCallArgs(context);

            if (modifiers.contains(AddonModifiers.NATIVE())) {
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");
                return new ConstructorDeclarationStatement(modifiers, args, new ArrayList<>());
            }

            boolean hasNewLine = parser.getCurrent().getType().equals(TokenTypes.NEW_LINE());
            parser.moveOverOptionalNewLines();

            if (!parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE()) && hasNewLine) {
                return new ConstructorDeclarationStatement(modifiers, args, new ArrayList<>());
            }

            parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open constructor body");
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
            parser.moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                if (parser.getCurrent().getType().equals(AddonTokenTypes.BASE())) body.add(parser.parse(AddonMain.getIdentifier("base_call_statement")));
                else body.add(parser.parse(AddonMain.getIdentifier("statement")));
                parser.moveOverOptionalNewLines();
            }

            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close constructor body");
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");

            return new ConstructorDeclarationStatement(modifiers, args, body);
        });

        register("base_call_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.BASE(), "Expected BASE to start base call statement");

            String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after base keyword").getValue();

            parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
            List<Expression> args = new ArrayList<>();
            if (parser.getCurrent().getType() != AddonTokenTypes.RIGHT_PAREN()) {
                args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));

                while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                    parser.getCurrentAndNext();
                    args.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
                }
            }
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
            return new BaseCallStatement(id, args);
        });

        register("statement", (context, extra) -> {
            Parser parser = context.getParser();

            Set<Modifier> modifiers = parseModifiers(context);

            if (parser.getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                parser.moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (!modifiers.isEmpty()) throw new InvalidStatementException("Unexpected Modifier found", parser.getCurrent().getLine());

            if (parser.getCurrent().getType().equals(AddonTokenTypes.IF())) return parser.parse(AddonMain.getIdentifier("if_statement"));
            if (parser.getCurrent().getType().equals(AddonTokenTypes.FOR())) return parser.parse(AddonMain.getIdentifier("for_statement"));
            if (parser.getCurrent().getType().equals(AddonTokenTypes.WHILE())) return parser.parse(AddonMain.getIdentifier("while_statement"));
            if (parser.getCurrent().getType().equals(AddonTokenTypes.RETURN())) return parser.parse(AddonMain.getIdentifier("return_statement"));
            if (parser.getCurrent().getType().equals(AddonTokenTypes.CONTINUE())) return parser.parse(AddonMain.getIdentifier("continue_statement"));
            if (parser.getCurrent().getType().equals(AddonTokenTypes.BREAK())) return parser.parse(AddonMain.getIdentifier("break_statement"));

            Expression expression = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            if (expression instanceof FunctionCallExpression || expression instanceof ClassCallExpression ||
                    expression instanceof AssignmentExpression || expression instanceof MemberExpression) {
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of expression");
                return expression;
            }

            throw new InvalidStatementException("Invalid statement found", parser.getCurrent().getLine());
        });

        register("if_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.IF(), "Expected if keyword");

            parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open if condition");
            Expression condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close if condition");
            parser.moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                parser.getCurrentAndNext();
                body = parseBody(context);
                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
            }
            else body.add(parser.parse(AddonMain.getIdentifier("statement")));

            IfStatement elseStatement = null;
            if (parser.getCurrent().getType().equals(AddonTokenTypes.ELSE())) {
                parser.getCurrentAndNext();
                if (parser.getCurrent().getType().equals(AddonTokenTypes.IF())) {
                    elseStatement = parser.parse(AddonMain.getIdentifier("if_statement"), IfStatement.class);
                }
                else {
                    List<Statement> elseBody = new ArrayList<>();
                    parser.moveOverOptionalNewLines();
                    if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                        parser.getCurrentAndNext();
                        elseBody = parseBody(context);
                        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
                    }
                    else {
                        elseBody.add(parser.parse(AddonMain.getIdentifier("statement")));
                    }

                    elseStatement = new IfStatement(null, elseBody, null);
                }
            }

            return new IfStatement(condition, body, elseStatement);
        });

        register("for_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.FOR(), "Expected for keyword");

            parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open for condition");

            if (parser.currentLineHasToken(AddonTokenTypes.IN())) {
                VariableDeclarationStatement variableDeclarationStatement = parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), true);
                if (variableDeclarationStatement.getDeclarationInfos().size() > 1) {
                    throw new InvalidSyntaxException("Foreach statement can declare only one variable");
                }
                parser.getCurrentAndNext(AddonTokenTypes.IN(), "Expected IN after variable declaration");
                Expression collection = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);

                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

                parser.moveOverOptionalNewLines();
                parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
                List<Statement> body = parseBody(context);
                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

                parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

                return new ForeachStatement(variableDeclarationStatement, collection, body);
            }

            VariableDeclarationStatement variableDeclarationStatement = null;
            if (!parser.getCurrent().getType().equals(AddonTokenTypes.SEMICOLON())) {
                variableDeclarationStatement = parser.parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), false);
            }
            parser.getCurrentAndNext(AddonTokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            Expression condition = null;
            if (!parser.getCurrent().getType().equals(AddonTokenTypes.SEMICOLON())) {
                condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            }
            parser.getCurrentAndNext(AddonTokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            AssignmentExpression assignmentExpression = null;
            if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
                if (parser.parse(AddonMain.getIdentifier("assignment_expression"), Expression.class) instanceof AssignmentExpression expression) {
                    assignmentExpression = expression;
                }
                else throw new InvalidSyntaxException("Expected assignment expression as for statement's arg");
            }
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

            parser.moveOverOptionalNewLines();
            parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
            List<Statement> body = parseBody(context);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

            return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
        });

        register("while_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.WHILE(), "Expected while keyword");

            parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open while condition");
            Expression condition = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close while condition");

            parser.moveOverOptionalNewLines();
            parser.getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open while body");
            List<Statement> body = parseBody(context);
            parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close while body");

            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the while statement");

            return new WhileStatement(condition, body);
        });

        register("return_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.RETURN(), "Expected return keyword");

            Expression expression = null;
            if (!parser.getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
                expression = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
            }
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the return statement");

            return new ReturnStatement(expression);
        });

        register("continue_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.CONTINUE(), "Expected continue keyword");
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the continue statement");
            return new ContinueStatement();
        });

        register("break_statement", (context, extra) -> {
            Parser parser = context.getParser();

            parser.getCurrentAndNext(AddonTokenTypes.BREAK(), "Expected break keyword");
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the break statement");
            return new BreakStatement();
        });

        register("expression", (context, extra) -> {
            Parser parser = context.getParser();
            return parser.parseAfter(AddonMain.getIdentifier("expression"), Expression.class);
        });

        register("assignment_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression left = parser.parseAfter(AddonMain.getIdentifier("assignment_expression"), Expression.class);

            if (parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
                parser.getCurrentAndNext();
                Expression value = parser.parse(AddonMain.getIdentifier("assignment_expression"), Expression.class);
                return new AssignmentExpression(left, value);
            }
            else if (AddonTokenTypeSets.OPERATOR_ASSIGN().contains(parser.getCurrent().getType())) {
                Token token = parser.getCurrentAndNext();
                Expression value = new OperatorExpression(
                        left,
                        parser.parse(AddonMain.getIdentifier("assignment_expression"), Expression.class),
                        token.getValue().replaceAll("=$", ""), OperatorType.INFIX);
                return new AssignmentExpression(left, value);
            }

            return left;
        });

        register("list_creation_expression", (context, extra) -> {
            Parser parser = context.getParser();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACKET())) {
                parser.getCurrentAndNext();

                List<Expression> list = new ArrayList<>();
                while (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACKET())) {
                    list.add(parser.parse(AddonMain.getIdentifier("expression"), Expression.class));
                    if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACKET())) parser.getCurrentAndNext(AddonTokenTypes.COMMA(), "Expected comma as a separator between list elements");
                }
                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACKET(), "Expected right bracket to close list creation");

                return new ListCreationExpression(list);
            }

            return parser.parseAfter(AddonMain.getIdentifier("list_creation_expression"), Expression.class);
        });

        register("map_creation_expression", (context, extra) -> {
            Parser parser = context.getParser();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                parser.getCurrentAndNext();

                Map<Expression, Expression> map = new HashMap<>();
                while (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                    Expression key = parser.parse(AddonMain.getIdentifier("list_creation_expression"), Expression.class);
                    Expression value;
                    if (parser.getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
                        parser.getCurrentAndNext();
                        value = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
                    }
                    else value = new NullLiteral();
                    map.put(key, value);

                    if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) parser.getCurrentAndNext(AddonTokenTypes.COMMA(), "Expected comma as a separator between map pairs");
                }
                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close map creation");

                return new MapCreationExpression(map);
            }

            return parser.parseAfter(AddonMain.getIdentifier("map_creation_expression"), Expression.class);
        });

        register("null_check_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression checkExpression = parser.parseAfter(AddonMain.getIdentifier("null_check_expression"), Expression.class);

            if (parser.getCurrent().getType().equals(AddonTokenTypes.QUESTION_COLON())) {
                parser.getCurrentAndNext();
                Expression nullExpression = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
                return new NullCheckExpression(checkExpression, nullExpression);
            }

            return checkExpression;
        });

        register("logical_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression left = parser.parseAfter(AddonMain.getIdentifier("logical_expression"), Expression.class);

            TokenType current = parser.getCurrent().getType();
            while (current.equals(AddonTokenTypes.AND()) || current.equals(AddonTokenTypes.OR())) {
                String operator = parser.getCurrentAndNext().getValue();
                Expression right = parser.parseAfter(AddonMain.getIdentifier("logical_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

                current = parser.getCurrent().getType();
            }

            return left;
        });

        register("comparison_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression left = parser.parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);

            TokenType current = parser.getCurrent().getType();
            while (current.equals(AddonTokenTypes.EQUALS()) || current.equals(AddonTokenTypes.NOT_EQUALS()) || current.equals(AddonTokenTypes.GREATER()) ||
                    current.equals(AddonTokenTypes.GREATER_OR_EQUALS()) || current.equals(AddonTokenTypes.LESS()) || current.equals(AddonTokenTypes.LESS_OR_EQUALS())) {
                String operator = parser.getCurrentAndNext().getValue();
                Expression right = parser.parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

                current = parser.getCurrent().getType();
            }

            return left;
        });

        register("is_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression value = parser.parseAfter(AddonMain.getIdentifier("is_expression"), Expression.class);

            if (parser.getCurrent().getType().equals(AddonTokenTypes.IS()) || parser.getCurrent().getType().equals(AddonTokenTypes.IS_LIKE())) {
                boolean isLike = parser.getCurrentAndNext().getType().equals(AddonTokenTypes.IS_LIKE());
                return new IsExpression(value, parser.getCurrentAndNext(AddonTokenTypes.ID(), "Must specify data type after is keyword").getValue(), isLike);
            }

            return value;
        });

        register("addition_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression left = parser.parseAfter(AddonMain.getIdentifier("addition_expression"), Expression.class);

            while (parser.getCurrent().getType().equals(AddonTokenTypes.PLUS()) || parser.getCurrent().getType().equals(AddonTokenTypes.MINUS())) {
                String operator = parser.getCurrentAndNext().getValue();
                Expression right = parser.parse(AddonMain.getIdentifier("addition_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("multiplication_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression left = parser.parseAfter(AddonMain.getIdentifier("multiplication_expression"), Expression.class);

            while (parser.getCurrent().getType().equals(AddonTokenTypes.MULTIPLY()) || parser.getCurrent().getType().equals(AddonTokenTypes.DIVIDE()) ||
                    parser.getCurrent().getType().equals(AddonTokenTypes.PERCENT())) {
                String operator = parser.getCurrentAndNext().getValue();
                Expression right = parser.parseAfter(AddonMain.getIdentifier("multiplication_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("power_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression left = parser.parseAfter(AddonMain.getIdentifier("power_expression"), Expression.class);

            while (parser.getCurrent().getType().equals(AddonTokenTypes.POWER())) {
                String operator = parser.getCurrentAndNext().getValue();
                Expression right = parser.parseAfter(AddonMain.getIdentifier("power_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("inversion_expression", (context, extra) -> {
            Parser parser = context.getParser();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.INVERSION())) {
                parser.getCurrentAndNext();
                Expression expression = parser.parseAfter(AddonMain.getIdentifier("inversion_expression"), Expression.class);
                return new OperatorExpression(expression, null, AddonOperators.INVERSION());
            }

            return parser.parseAfter(AddonMain.getIdentifier("inversion_expression"), Expression.class);
        });

        register("negation_expression", (context, extra) -> {
            Parser parser = context.getParser();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.MINUS())) {
                parser.getCurrentAndNext();
                return new OperatorExpression(parser.parseAfter(AddonMain.getIdentifier("negation_expression"), Expression.class), null, AddonOperators.NEGATION());
            }

            return parser.parseAfter(AddonMain.getIdentifier("negation_expression"), Expression.class);
        });

        register("postfix_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression id = parser.parseAfter(AddonMain.getIdentifier("postfix_expression"), Expression.class);

            if (AddonTokenTypeSets.OPERATOR_POSTFIX().contains(parser.getCurrent().getType())) {
                Token token = parser.getCurrentAndNext();
                Expression value = new OperatorExpression(id, new NumberLiteral("1"), token.getValue().substring(1), OperatorType.INFIX);
                return new AssignmentExpression(id, value);
            }

            return id;
        });

        register("member_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression object = parser.parseAfter(AddonMain.getIdentifier("member_expression"), Expression.class);

            while (AddonTokenTypeSets.MEMBER_ACCESS().contains(parser.getCurrent().getType())) {
                boolean isNullSafe = parser.getCurrentAndNext().getType().equals(AddonTokenTypes.QUESTION_DOT());
                Expression member = parser.parseAfter(AddonMain.getIdentifier("member_expression"), Expression.class);
                if (!(member instanceof Identifier) && !(member instanceof CallExpression)) {
                    throw new UnexpectedTokenException("Right side must be either Identifier or Call", parser.getCurrent().getLine());
                }
                object = new MemberExpression(object, member, isNullSafe);
            }

            return object;
        });

        register("class_call_expression", (context, extra) -> {
            Parser parser = context.getParser();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.NEW())) {
                parser.getCurrentAndNext();
                Expression expression = parser.parseAfter(AddonMain.getIdentifier("class_call_expression"), Expression.class);
                if (expression instanceof CallExpression callExpression) {
                    return new ClassCallExpression(new ClassIdentifier(callExpression.getCaller().getId()), callExpression.getArgs());
                }
                throw new InvalidSyntaxException("Class creation must be call expression");
            }

            return parser.parseAfter(AddonMain.getIdentifier("class_call_expression"), Expression.class);
        });

        register("function_call_expression", (context, extra) -> {
            Parser parser = context.getParser();

            Expression expression = parser.parseAfter(AddonMain.getIdentifier("function_call_expression"), Expression.class);

            if (parser.getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) {
                if (!(expression instanceof Identifier identifier)) throw new InvalidSyntaxException("Can't call non-identifier");
                return new FunctionCallExpression(new FunctionIdentifier(identifier.getId()), parseArgs(context));
            }

            return expression;
        });

        register("primary_expression", (context, extra) -> {
            Parser parser = context.getParser();
            TokenType tokenType = parser.getCurrent().getType();

            if (tokenType.equals(AddonTokenTypes.ID())) {
                if ((parser.getPos() != 0 && parser.getTokens().get(parser.getPos() - 1).getType().equals(AddonTokenTypes.NEW())) ||
                        (parser.getTokens().size() > parser.getPos() + 1 && parser.getTokens().get(parser.getPos() + 1).getType().equals(AddonTokenTypes.DOT()) && parser.getPos() != 0 && !parser.getTokens().get(parser.getPos() - 1).getType().equals(AddonTokenTypes.DOT())))
                    return new ClassIdentifier(parser.getCurrentAndNext().getValue());
                else if (parser.getTokens().size() > parser.getPos() + 1 && parser.getTokens().get(parser.getPos() + 1).getType().equals(AddonTokenTypes.LEFT_PAREN())) {
                    return new FunctionIdentifier(parser.getCurrentAndNext().getValue());
                }
                else return new VariableIdentifier(parser.getCurrentAndNext().getValue());
            }
            if (tokenType.equals(AddonTokenTypes.NULL())) {
                parser.getCurrentAndNext();
                return new NullLiteral();
            }
            if (tokenType.equals(AddonTokenTypes.NUMBER())) return new NumberLiteral(parser.getCurrentAndNext().getValue());
            if (tokenType.equals(AddonTokenTypes.STRING())) {
                String value = parser.getCurrentAndNext().getValue();
                return new StringLiteral(value.substring(1, value.length() - 1));
            }
            if (tokenType.equals(AddonTokenTypes.BOOLEAN())) return new BooleanLiteral(Boolean.parseBoolean(parser.getCurrentAndNext().getValue()));
            if (tokenType.equals(AddonTokenTypes.THIS())) {
                parser.getCurrentAndNext();
                return new ThisLiteral();
            }
            if (tokenType.equals(AddonTokenTypes.LEFT_PAREN())) {
                parser.getCurrentAndNext();
                Expression value = parser.parse(AddonMain.getIdentifier("expression"), Expression.class);
                parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis");
                return value;
            }

            throw new InvalidStatementException("Can't parse token with type " + tokenType.getId());
        });
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


    private static Set<Modifier> parseModifiers(ParsingContext context) {
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

    @SuppressWarnings("unchecked")
    private static Set<Modifier> getModifiersFromExtra(Object[] extra) {
        if (extra.length == 0) throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        if (!(extra[0] instanceof Set<?> set)) throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        try {
            return (Set<Modifier>) set;
        }
        catch (ClassCastException ignore) {
            throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        }
    }

    private static List<CallArgExpression> parseCallArgs(ParsingContext context) {
        Parser parser = context.getParser();
        
        parser.getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<CallArgExpression> args = new ArrayList<>();
        
        if (!parser.getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
            args.add(parser.parse(AddonMain.getIdentifier("function_arg"), CallArgExpression.class));

            while (parser.getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                parser.getCurrentAndNext();
                args.add(parser.parse(AddonMain.getIdentifier("function_arg"), CallArgExpression.class));
            }
        }
        
        parser.getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    private static List<Expression> parseArgs(ParsingContext context) {
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

    private static DataType parseDataType(ParsingContext context) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(AddonTokenTypes.COLON())) {
            parser.getCurrentAndNext();
            String dataTypeId = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Must specify variable's data type after colon").getValue();

            if (parser.getCurrent().getType().equals(AddonTokenTypes.QUESTION())) {
                parser.getCurrentAndNext();
                return new DataTypeImpl(dataTypeId, true);
            }
            return new DataTypeImpl(dataTypeId, false);
        }
        return null;
    }

    private static List<Statement> parseBody(ParsingContext context) {
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

    private static VariableDeclarationStatement.VariableDeclarationInfo parseVariableDeclarationInfo(ParsingContext context, boolean isConstant, boolean canWithoutValue) {
        Parser parser = context.getParser();
        String id = parser.getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier in variable declaration statement").getValue();

        DataType dataType = parseDataType(context);
        if (dataType == null) dataType = new DataTypeImpl("Any", true);

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



    private static void register(String id, ParsingFunction<? extends Statement> parsingFunction) {
        Registries.PARSING_FUNCTIONS.register(AddonMain.getIdentifier(id), parsingFunction);
    }
}