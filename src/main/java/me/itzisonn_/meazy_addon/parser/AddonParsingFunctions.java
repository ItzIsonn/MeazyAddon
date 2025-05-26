package me.itzisonn_.meazy_addon.parser;

import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.*;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.Expression;
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
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;

import java.util.*;

import static me.itzisonn_.meazy.parser.Parser.*;

/**
 * All basic ParsingFunctions
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class AddonParsingFunctions {
    private static boolean isInit = false;

    private AddonParsingFunctions() {}



    /**
     * Initializes {@link Registries#PARSING_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#PARSING_FUNCTIONS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("ParsingFunctions have already been initialized");
        isInit = true;

        register("global_statement", extra -> {
            if (getCurrent().getType().equals(AddonTokenTypes.IMPORT())) {
                return parse(AddonMain.getIdentifier("import_statement"), ImportStatement.class);
            }
            if (getCurrent().getType().equals(AddonTokenTypes.USING())) {
                return parse(AddonMain.getIdentifier("using_statement"), UsingStatement.class);
            }

            Set<Modifier> modifiers = parseModifiers();
            if (getCurrent().getType().equals(AddonTokenTypes.CLASS())) {
                return parse(AddonMain.getIdentifier("class_declaration_statement"), ClassDeclarationStatement.class, modifiers);
            }
            if (getCurrent().getType().equals(AddonTokenTypes.FUNCTION())) {
                return parse(AddonMain.getIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
            }
            if (getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }

            throw new InvalidStatementException("At global environment you only can declare variable, function or class", getCurrent().getLine());
        });

        register("import_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.IMPORT(), "Expected import keyword");
            String file = getCurrentAndNext(AddonTokenTypes.STRING(), "Expected file path after import keyword").getValue();
            return new ImportStatement(file.substring(1, file.length() - 1));
        });

        register("using_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.USING(), "Expected using keyword");
            String nativeClass = getCurrentAndNext(AddonTokenTypes.STRING(), "Expected native class name after using keyword").getValue();
            return new UsingStatement(nativeClass.substring(1, nativeClass.length() - 1));
        });

        register("class_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            getCurrentAndNext(AddonTokenTypes.CLASS(), "Expected class keyword");
            String id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected id after class keyword").getValue();

            List<Statement> generatedBody = new ArrayList<>();
            if (modifiers.contains(AddonModifiers.DATA())) {
                generatedBody.addAll(generateDataBody(id, parseCallArgs()));
                modifiers.remove(AddonModifiers.DATA());
            }

            Set<String> baseClasses = new HashSet<>();
            if (getCurrent().getType().equals(AddonTokenTypes.COLON())) {
                getCurrentAndNext();
                baseClasses.add(getCurrentAndNext(AddonTokenTypes.ID(), "Expected id as base class").getValue());

                while (getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                    getCurrentAndNext();
                    baseClasses.add(getCurrentAndNext(AddonTokenTypes.ID(), "Expected id as base class after comma").getValue());
                }
            }

            boolean hasNewLine = getCurrent().getType().equals(TokenTypes.NEW_LINE());
            moveOverOptionalNewLines();

            if (!getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE()) && hasNewLine) {
                return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
            }

            getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open class body");

            if (getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                getCurrentAndNext();
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
                moveOverOptionalNewLines();
                return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
            }

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
            moveOverOptionalNewLines();

            LinkedHashMap<String, List<Expression>> enumIds = new LinkedHashMap<>();
            if (modifiers.contains(AddonModifiers.ENUM())) {
                if (!baseClasses.isEmpty()) throw new InvalidSyntaxException("Enum class can't have base classes");

                String enumId = getCurrentAndNext(AddonTokenTypes.ID(), "Expected enum member id").getValue();
                List<Expression> args;
                if (getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) args = parseArgs();
                else args = new ArrayList<>();
                enumIds.put(enumId, args);

                while (getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                    getCurrentAndNext();
                    moveOverOptionalNewLines();

                    enumId = getCurrentAndNext(AddonTokenTypes.ID(), "Expected enum member id").getValue();
                    if (enumIds.containsKey(enumId)) throw new InvalidSyntaxException("Enum class can't have duplicated entries");
                    if (getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) args = parseArgs();
                    else args = new ArrayList<>();
                    enumIds.put(enumId, args);
                }

                moveOverOptionalNewLines();
            }

            List<Statement> body = new ArrayList<>(generatedBody);
            while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                Statement statement = parse(AddonMain.getIdentifier("class_body_statement"));
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

                moveOverOptionalNewLines();
            }

            getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close class body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the class declaration");

            return new ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds);
        });

        register("class_body_statement", extra -> {
            Set<Modifier> modifiers = parseModifiers();

            if (getCurrent().getType().equals(AddonTokenTypes.FUNCTION())) {
                return parse(AddonMain.getIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
            }
            if (getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, true);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (getCurrent().getType().equals(AddonTokenTypes.CONSTRUCTOR())) {
                return parse(AddonMain.getIdentifier("constructor_declaration_statement"), ConstructorDeclarationStatement.class, modifiers);
            }

            throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
        });

        register("function_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            getCurrentAndNext(AddonTokenTypes.FUNCTION(), "Expected function keyword");

            String classId = null;
            String id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after function keyword").getValue();
            if (getCurrent().getType().equals(AddonTokenTypes.DOT())) {
                getCurrentAndNext();
                classId = id;
                id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after function keyword").getValue();
            }

            List<CallArgExpression> args = parseCallArgs();
            DataType dataType = parseDataType();

            if (modifiers.contains(AddonModifiers.ABSTRACT()) || modifiers.contains(AddonModifiers.NATIVE())) {
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
                return new FunctionDeclarationStatement(modifiers, id, args, new ArrayList<>(), dataType);
            }

            List<Statement> body;
            if (getCurrent().getType().equals(AddonTokenTypes.ARROW())) {
                getCurrentAndNext();
                body = new ArrayList<>(List.of(parse(AddonMain.getIdentifier("statement"))));
            }
            else {
                moveOverOptionalNewLines();
                getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open function body");
                body = parseBody();
                getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close function body");
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
            }

            return new FunctionDeclarationStatement(modifiers, id, classId, args, body, dataType);
        });

        register("function_arg", extra -> {
            if (!getCurrent().getType().equals(AddonTokenTypes.VARIABLE()))
                throw new UnexpectedTokenException("Expected variable keyword at the beginning of function arg", getCurrent().getLine());
            boolean isConstant = getCurrentAndNext().getValue().equals("val");
            String id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after variable keyword in function arg").getValue();

            DataType dataType = parseDataType();
            return new CallArgExpression(id, dataType == null ? new DataTypeImpl("Any", true) : dataType, isConstant);
        });

        register("variable_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            if (extra.length == 1) throw new IllegalArgumentException("Expected boolean as extra argument");
            if (!(extra[1] instanceof Boolean canWithoutValue)) throw new IllegalArgumentException("Expected boolean as extra argument");

            boolean isConstant = getCurrentAndNext(AddonTokenTypes.VARIABLE(), "Expected variable keyword").getValue().equals("val");

            List<VariableDeclarationStatement.VariableDeclarationInfo> declarations = new ArrayList<>();
            declarations.add(parseVariableDeclarationInfo(isConstant, canWithoutValue));

            while (getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                getCurrentAndNext();
                declarations.add(parseVariableDeclarationInfo(isConstant, canWithoutValue));
            }

            return new VariableDeclarationStatement(modifiers, isConstant, declarations);
        });

        register("constructor_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);
            getCurrentAndNext(AddonTokenTypes.CONSTRUCTOR(), "Expected constructor keyword");

            List<CallArgExpression> args = parseCallArgs();

            if (modifiers.contains(AddonModifiers.NATIVE())) {
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");
                return new ConstructorDeclarationStatement(modifiers, args, new ArrayList<>());
            }

            boolean hasNewLine = getCurrent().getType().equals(TokenTypes.NEW_LINE());
            moveOverOptionalNewLines();

            if (!getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE()) && hasNewLine) {
                return new ConstructorDeclarationStatement(modifiers, args, new ArrayList<>());
            }

            getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open constructor body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
            moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                if (getCurrent().getType().equals(AddonTokenTypes.BASE())) body.add(parse(AddonMain.getIdentifier("base_call_statement")));
                else body.add(parse(AddonMain.getIdentifier("statement")));
                moveOverOptionalNewLines();
            }

            getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close constructor body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");

            return new ConstructorDeclarationStatement(modifiers, args, body);
        });

        register("base_call_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.BASE(), "Expected BASE to start base call statement");

            String id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier after base keyword").getValue();

            getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
            List<Expression> args = new ArrayList<>();
            if (getCurrent().getType() != AddonTokenTypes.RIGHT_PAREN()) {
                args.add(parse(AddonMain.getIdentifier("expression"), Expression.class));

                while (getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                    getCurrentAndNext();
                    args.add(parse(AddonMain.getIdentifier("expression"), Expression.class));
                }
            }
            getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
            return new BaseCallStatement(id, args);
        });

        register("statement", extra -> {
            Set<Modifier> modifiers = parseModifiers();

            if (getCurrent().getType().equals(AddonTokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (!modifiers.isEmpty()) throw new InvalidStatementException("Unexpected Modifier found", getCurrent().getLine());

            if (getCurrent().getType().equals(AddonTokenTypes.IF())) return parse(AddonMain.getIdentifier("if_statement"));
            if (getCurrent().getType().equals(AddonTokenTypes.FOR())) return parse(AddonMain.getIdentifier("for_statement"));
            if (getCurrent().getType().equals(AddonTokenTypes.WHILE())) return parse(AddonMain.getIdentifier("while_statement"));
            if (getCurrent().getType().equals(AddonTokenTypes.RETURN())) return parse(AddonMain.getIdentifier("return_statement"));
            if (getCurrent().getType().equals(AddonTokenTypes.CONTINUE())) return parse(AddonMain.getIdentifier("continue_statement"));
            if (getCurrent().getType().equals(AddonTokenTypes.BREAK())) return parse(AddonMain.getIdentifier("break_statement"));

            Expression expression = parse(AddonMain.getIdentifier("expression"), Expression.class);
            if (expression instanceof FunctionCallExpression || expression instanceof ClassCallExpression ||
                    expression instanceof AssignmentExpression || expression instanceof MemberExpression) {
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of expression");
                return expression;
            }

            throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
        });

        register("if_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.IF(), "Expected if keyword");

            getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open if condition");
            Expression condition = parse(AddonMain.getIdentifier("expression"), Expression.class);
            getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close if condition");
            moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            if (getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                getCurrentAndNext();
                body = parseBody();
                getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
            }
            else body.add(parse(AddonMain.getIdentifier("statement")));

            IfStatement elseStatement = null;
            if (getCurrent().getType().equals(AddonTokenTypes.ELSE())) {
                getCurrentAndNext();
                if (getCurrent().getType().equals(AddonTokenTypes.IF())) {
                    elseStatement = parse(AddonMain.getIdentifier("if_statement"), IfStatement.class);
                }
                else {
                    List<Statement> elseBody = new ArrayList<>();
                    moveOverOptionalNewLines();
                    if (getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                        getCurrentAndNext();
                        elseBody = parseBody();
                        getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                        getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
                    }
                    else {
                        elseBody.add(parse(AddonMain.getIdentifier("statement")));
                    }

                    elseStatement = new IfStatement(null, elseBody, null);
                }
            }

            return new IfStatement(condition, body, elseStatement);
        });

        register("for_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.FOR(), "Expected for keyword");

            getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open for condition");

            if (currentLineHasToken(AddonTokenTypes.IN())) {
                VariableDeclarationStatement variableDeclarationStatement = parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), true);
                if (variableDeclarationStatement.getDeclarationInfos().size() > 1) {
                    throw new InvalidSyntaxException("Foreach statement can declare only one variable");
                }
                getCurrentAndNext(AddonTokenTypes.IN(), "Expected IN after variable declaration");
                Expression collection = parse(AddonMain.getIdentifier("expression"), Expression.class);

                getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

                moveOverOptionalNewLines();
                getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
                List<Statement> body = parseBody();
                getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

                return new ForeachStatement(variableDeclarationStatement, collection, body);
            }

            VariableDeclarationStatement variableDeclarationStatement = null;
            if (!getCurrent().getType().equals(AddonTokenTypes.SEMICOLON())) {
                variableDeclarationStatement = parse(AddonMain.getIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), false);
            }
            getCurrentAndNext(AddonTokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            Expression condition = null;
            if (!getCurrent().getType().equals(AddonTokenTypes.SEMICOLON())) {
                condition = parse(AddonMain.getIdentifier("expression"), Expression.class);
            }
            getCurrentAndNext(AddonTokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            AssignmentExpression assignmentExpression = null;
            if (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
                if (parse(AddonMain.getIdentifier("assignment_expression"), Expression.class) instanceof AssignmentExpression expression) {
                    assignmentExpression = expression;
                }
                else throw new InvalidSyntaxException("Expected assignment expression as for statement's arg");
            }
            getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

            moveOverOptionalNewLines();
            getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
            List<Statement> body = parseBody();
            getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

            return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
        });

        register("while_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.WHILE(), "Expected while keyword");

            getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open while condition");
            Expression condition = parse(AddonMain.getIdentifier("expression"), Expression.class);
            getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close while condition");

            moveOverOptionalNewLines();
            getCurrentAndNext(AddonTokenTypes.LEFT_BRACE(), "Expected left brace to open while body");
            List<Statement> body = parseBody();
            getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close while body");

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the while statement");

            return new WhileStatement(condition, body);
        });

        register("return_statement", extra -> {
            getCurrentAndNext(AddonTokenTypes.RETURN(), "Expected return keyword");

            Expression expression = null;
            if (!getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
                expression = parse(AddonMain.getIdentifier("expression"), Expression.class);
            }
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the return statement");

            return new ReturnStatement(expression);
        });

        register("continue_statement", extra ->  {
            getCurrentAndNext(AddonTokenTypes.CONTINUE(), "Expected continue keyword");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the continue statement");
            return new ContinueStatement();
        });

        register("break_statement", extra ->  {
            getCurrentAndNext(AddonTokenTypes.BREAK(), "Expected break keyword");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the break statement");
            return new BreakStatement();
        });

        register("expression", extra -> parseAfter(AddonMain.getIdentifier("expression"), Expression.class));

        register("assignment_expression", extra -> {
            Expression left = parseAfter(AddonMain.getIdentifier("assignment_expression"), Expression.class);

            if (getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
                getCurrentAndNext();
                Expression value = parse(AddonMain.getIdentifier("assignment_expression"), Expression.class);
                return new AssignmentExpression(left, value);
            }
            else if (AddonTokenTypeSets.OPERATOR_ASSIGN().contains(getCurrent().getType())) {
                Token token = getCurrentAndNext();
                Expression value = new OperatorExpression(
                        left,
                        parse(AddonMain.getIdentifier("assignment_expression"), Expression.class),
                        token.getValue().replaceAll("=$", ""), OperatorType.INFIX);
                return new AssignmentExpression(left, value);
            }

            return left;
        });

        register("list_creation_expression", extra -> {
            if (getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACKET())) {
                getCurrentAndNext();

                List<Expression> list = new ArrayList<>();
                while (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACKET())) {
                    list.add(parse(AddonMain.getIdentifier("expression"), Expression.class));
                    if (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACKET())) getCurrentAndNext(AddonTokenTypes.COMMA(), "Expected comma as a separator between list elements");
                }
                getCurrentAndNext(AddonTokenTypes.RIGHT_BRACKET(), "Expected right bracket to close list creation");

                return new ListCreationExpression(list);
            }

            return parseAfter(AddonMain.getIdentifier("list_creation_expression"), Expression.class);
        });

        register("map_creation_expression", extra -> {
            if (getCurrent().getType().equals(AddonTokenTypes.LEFT_BRACE())) {
                getCurrentAndNext();

                Map<Expression, Expression> map = new HashMap<>();
                while (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
                    Expression key = parse(AddonMain.getIdentifier("list_creation_expression"), Expression.class);
                    Expression value;
                    if (getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
                        getCurrentAndNext();
                        value = parse(AddonMain.getIdentifier("expression"), Expression.class);
                    }
                    else value = new NullLiteral();
                    map.put(key, value);

                    if (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) getCurrentAndNext(AddonTokenTypes.COMMA(), "Expected comma as a separator between map pairs");
                }
                getCurrentAndNext(AddonTokenTypes.RIGHT_BRACE(), "Expected right brace to close map creation");

                return new MapCreationExpression(map);
            }

            return parseAfter(AddonMain.getIdentifier("map_creation_expression"), Expression.class);
        });

        register("null_check_expression", extra -> {
            Expression checkExpression = parseAfter(AddonMain.getIdentifier("null_check_expression"), Expression.class);

            if (getCurrent().getType().equals(AddonTokenTypes.QUESTION_COLON())) {
                getCurrentAndNext();
                Expression nullExpression = parse(AddonMain.getIdentifier("expression"), Expression.class);
                return new NullCheckExpression(checkExpression, nullExpression);
            }

            return checkExpression;
        });

        register("logical_expression", extra -> {
            Expression left = parseAfter(AddonMain.getIdentifier("logical_expression"), Expression.class);

            TokenType current = getCurrent().getType();
            while (current.equals(AddonTokenTypes.AND()) || current.equals(AddonTokenTypes.OR())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(AddonMain.getIdentifier("logical_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

                current = getCurrent().getType();
            }

            return left;
        });

        register("comparison_expression", extra -> {
            Expression left = parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);

            TokenType current = getCurrent().getType();
            while (current.equals(AddonTokenTypes.EQUALS()) || current.equals(AddonTokenTypes.NOT_EQUALS()) || current.equals(AddonTokenTypes.GREATER()) ||
                    current.equals(AddonTokenTypes.GREATER_OR_EQUALS()) || current.equals(AddonTokenTypes.LESS()) || current.equals(AddonTokenTypes.LESS_OR_EQUALS())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(AddonMain.getIdentifier("comparison_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

                current = getCurrent().getType();
            }

            return left;
        });

        register("is_expression", extra -> {
            Expression value = parseAfter(AddonMain.getIdentifier("is_expression"), Expression.class);

            if (getCurrent().getType().equals(AddonTokenTypes.IS()) || getCurrent().getType().equals(AddonTokenTypes.IS_LIKE())) {
                boolean isLike = getCurrentAndNext().getType().equals(AddonTokenTypes.IS_LIKE());
                return new IsExpression(value, getCurrentAndNext(AddonTokenTypes.ID(), "Must specify data type after is keyword").getValue(), isLike);
            }

            return value;
        });

        register("addition_expression", extra -> {
            Expression left = parseAfter(AddonMain.getIdentifier("addition_expression"), Expression.class);

            while (getCurrent().getType().equals(AddonTokenTypes.PLUS()) || getCurrent().getType().equals(AddonTokenTypes.MINUS())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(AddonMain.getIdentifier("addition_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("multiplication_expression", extra -> {
            Expression left = parseAfter(AddonMain.getIdentifier("multiplication_expression"), Expression.class);

            while (getCurrent().getType().equals(AddonTokenTypes.MULTIPLY()) || getCurrent().getType().equals(AddonTokenTypes.DIVIDE()) ||
                    getCurrent().getType().equals(AddonTokenTypes.PERCENT())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(AddonMain.getIdentifier("multiplication_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("power_expression", extra -> {
            Expression left = parseAfter(AddonMain.getIdentifier("power_expression"), Expression.class);

            while (getCurrent().getType().equals(AddonTokenTypes.POWER())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(AddonMain.getIdentifier("power_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("inversion_expression", extra -> {
            if (getCurrent().getType().equals(AddonTokenTypes.INVERSION())) {
                getCurrentAndNext();
                Expression expression = parseAfter(AddonMain.getIdentifier("inversion_expression"), Expression.class);
                return new OperatorExpression(expression, null, AddonOperators.INVERSION());
            }

            return parseAfter(AddonMain.getIdentifier("inversion_expression"), Expression.class);
        });

        register("negation_expression", extra -> {
            if (getCurrent().getType().equals(AddonTokenTypes.MINUS())) {
                getCurrentAndNext();
                return new OperatorExpression(parseAfter(AddonMain.getIdentifier("negation_expression"), Expression.class), null, AddonOperators.NEGATION());
            }

            return parseAfter(AddonMain.getIdentifier("negation_expression"), Expression.class);
        });

        register("postfix_expression", extra -> {
            Expression id = parseAfter(AddonMain.getIdentifier("postfix_expression"), Expression.class);

            if (AddonTokenTypeSets.OPERATOR_POSTFIX().contains(getCurrent().getType())) {
                Token token = getCurrentAndNext();
                Expression value = new OperatorExpression(id, new NumberLiteral("1"), token.getValue().substring(1), OperatorType.INFIX);
                return new AssignmentExpression(id, value);
            }

            return id;
        });

        register("member_expression", extra -> {
            Expression object = parseAfter(AddonMain.getIdentifier("member_expression"), Expression.class);

            while (AddonTokenTypeSets.MEMBER_ACCESS().contains(getCurrent().getType())) {
                boolean isNullSafe = getCurrentAndNext().getType().equals(AddonTokenTypes.QUESTION_DOT());
                Expression member = parseAfter(AddonMain.getIdentifier("member_expression"), Expression.class);
                if (!(member instanceof Identifier) && !(member instanceof CallExpression)) {
                    throw new UnexpectedTokenException("Right side must be either Identifier or Call", getCurrent().getLine());
                }
                object = new MemberExpression(object, member, isNullSafe);
            }

            return object;
        });

        register("class_call_expression", extra -> {
            if (getCurrent().getType().equals(AddonTokenTypes.NEW())) {
                getCurrentAndNext();
                Expression expression = parseAfter(AddonMain.getIdentifier("class_call_expression"), Expression.class);
                if (expression instanceof CallExpression callExpression) {
                    return new ClassCallExpression(new ClassIdentifier(callExpression.getCaller().getId()), callExpression.getArgs());
                }
                throw new InvalidSyntaxException("Class creation must be call expression");
            }

            return parseAfter(AddonMain.getIdentifier("class_call_expression"), Expression.class);
        });

        register("function_call_expression", extra -> {
            Expression expression = parseAfter(AddonMain.getIdentifier("function_call_expression"), Expression.class);

            if (getCurrent().getType().equals(AddonTokenTypes.LEFT_PAREN())) {
                if (!(expression instanceof Identifier identifier)) throw new InvalidSyntaxException("Can't call non-identifier");
                return new FunctionCallExpression(new FunctionIdentifier(identifier.getId()), parseArgs());
            }

            return expression;
        });

        register("primary_expression", extra -> {
            TokenType tokenType = getCurrent().getType();

            if (tokenType.equals(AddonTokenTypes.ID())) {
                if ((getPos() != 0 && getTokens().get(getPos() - 1).getType().equals(AddonTokenTypes.NEW())) ||
                        (getTokens().size() > getPos() + 1 && getTokens().get(getPos() + 1).getType().equals(AddonTokenTypes.DOT()) && getPos() != 0 && !getTokens().get(getPos() - 1).getType().equals(AddonTokenTypes.DOT())))
                    return new ClassIdentifier(getCurrentAndNext().getValue());
                else if (getTokens().size() > getPos() + 1 && getTokens().get(getPos() + 1).getType().equals(AddonTokenTypes.LEFT_PAREN())) {
                    return new FunctionIdentifier(getCurrentAndNext().getValue());
                }
                else return new VariableIdentifier(getCurrentAndNext().getValue());
            }
            if (tokenType.equals(AddonTokenTypes.NULL())) {
                getCurrentAndNext();
                return new NullLiteral();
            }
            if (tokenType.equals(AddonTokenTypes.NUMBER())) return new NumberLiteral(getCurrentAndNext().getValue());
            if (tokenType.equals(AddonTokenTypes.STRING())) {
                String value = getCurrentAndNext().getValue();
                return new StringLiteral(value.substring(1, value.length() - 1));
            }
            if (tokenType.equals(AddonTokenTypes.BOOLEAN())) return new BooleanLiteral(Boolean.parseBoolean(getCurrentAndNext().getValue()));
            if (tokenType.equals(AddonTokenTypes.THIS())) {
                getCurrentAndNext();
                return new ThisLiteral();
            }
            if (tokenType.equals(AddonTokenTypes.LEFT_PAREN())) {
                getCurrentAndNext();
                Expression value = parse(AddonMain.getIdentifier("expression"), Expression.class);
                getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis");
                return value;
            }

            throw new InvalidStatementException("Can't parse token with type " + tokenType.getId());
        });
    }

    public static Map<String, Version> parseRequiredAddons() {
        Map<String, Version> requiredAddons = new HashMap<>();

        moveOverOptionalNewLines();

        while (getCurrent().getType().equals(AddonTokenTypes.REQUIRE())) {
            getCurrentAndNext();

            String id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected id after require keyword").getValue();
            Version version;
            if (getCurrent().getType().equals(AddonTokenTypes.STRING())) {
                String value = getCurrentAndNext().getValue();
                version = Version.of(value.substring(1, value.length() - 1));
            }
            else version = null;
            requiredAddons.put(id, version);

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line in the end of the require statement");
            moveOverOptionalNewLines();
        }

        return requiredAddons;
    }


    private static Set<Modifier> parseModifiers() {
        Set<Modifier> modifiers = new HashSet<>();
        while (getCurrent().getType().equals(AddonTokenTypes.ID())) {
            String id = getCurrent().getValue();
            Modifier modifier = AddonModifiers.parse(id);
            if (modifier == null) {
                if (modifiers.isEmpty()) return modifiers;
                throw new InvalidStatementException("Modifier with id " + id + " doesn't exist");
            }
            getCurrentAndNext();
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

    private static List<CallArgExpression> parseCallArgs() {
        getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<CallArgExpression> args = new ArrayList<>();
        if (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
            args.add(parse(AddonMain.getIdentifier("function_arg"), CallArgExpression.class));

            while (getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                getCurrentAndNext();
                args.add(parse(AddonMain.getIdentifier("function_arg"), CallArgExpression.class));
            }
        }
        getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    private static List<Expression> parseArgs() {
        getCurrentAndNext(AddonTokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<Expression> args = new ArrayList<>();
        if (!getCurrent().getType().equals(AddonTokenTypes.RIGHT_PAREN())) {
            args.add(parse(AddonMain.getIdentifier("expression"), Expression.class));

            while (getCurrent().getType().equals(AddonTokenTypes.COMMA())) {
                getCurrentAndNext();
                args.add(parse(AddonMain.getIdentifier("expression"), Expression.class));
            }
        }
        getCurrentAndNext(AddonTokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    private static DataType parseDataType() {
        if (getCurrent().getType().equals(AddonTokenTypes.COLON())) {
            getCurrentAndNext();
            String dataTypeId = getCurrentAndNext(AddonTokenTypes.ID(), "Must specify variable's data type after colon").getValue();

            if (getCurrent().getType().equals(AddonTokenTypes.QUESTION())) {
                getCurrentAndNext();
                return new DataTypeImpl(dataTypeId, true);
            }
            return new DataTypeImpl(dataTypeId, false);
        }
        return null;
    }

    private static List<Statement> parseBody() {
        List<Statement> body = new ArrayList<>();
        getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
        moveOverOptionalNewLines();

        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(AddonTokenTypes.RIGHT_BRACE())) {
            body.add(parse(AddonMain.getIdentifier("statement")));
            moveOverOptionalNewLines();
        }

        moveOverOptionalNewLines();
        return body;
    }

    private static VariableDeclarationStatement.VariableDeclarationInfo parseVariableDeclarationInfo(boolean isConstant, boolean canWithoutValue) {
        String id = getCurrentAndNext(AddonTokenTypes.ID(), "Expected identifier in variable declaration statement").getValue();

        DataType dataType = parseDataType();
        if (dataType == null) dataType = new DataTypeImpl("Any", true);

        if (!getCurrent().getType().equals(AddonTokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, null);
            }
            if (isConstant) throw new InvalidStatementException("Can't declare a constant variable without a value", getCurrent().getLine());
            return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, new NullLiteral());
        }

        getCurrentAndNext(AddonTokenTypes.ASSIGN(), "Expected ASSIGN token after the id in variable declaration");

        return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, parse(AddonMain.getIdentifier("expression"), Expression.class));
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