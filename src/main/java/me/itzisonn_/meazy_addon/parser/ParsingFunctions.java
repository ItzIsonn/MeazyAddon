package me.itzisonn_.meazy_addon.parser;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.parser.*;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy_addon.lexer.TokenTypeSets;
import me.itzisonn_.meazy_addon.lexer.TokenTypes;
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
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;

import java.util.*;

import static me.itzisonn_.meazy.parser.Parser.*;

/**
 * All basic ParsingFunctions
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class ParsingFunctions {
    private static boolean isInit = false;

    private ParsingFunctions() {}



    /**
     * Initializes {@link Registries#PARSING_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#PARSING_FUNCTIONS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("ParsingFunctions have already been initialized!");
        isInit = true;

        register("global_statement", extra -> {
            Set<Modifier> modifiers = parseModifiers();

            if (getCurrent().getType().equals(TokenTypes.FUNCTION())) {
                return parse(RegistryIdentifier.ofDefault("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
            }
            if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(RegistryIdentifier.ofDefault("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (getCurrent().getType().equals(TokenTypes.CLASS())) {
                return parse(RegistryIdentifier.ofDefault("class_declaration_statement"), ClassDeclarationStatement.class, modifiers);
            }

            throw new InvalidStatementException("At global environment you only can declare variable, function or class", getCurrent().getLine());
        });

        register("class_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            getCurrentAndNext(TokenTypes.CLASS(), "Expected class keyword");
            String id = getCurrentAndNext(TokenTypes.ID(), "Expected id after class keyword").getValue();

            List<Statement> generatedBody = new ArrayList<>();
            if (modifiers.contains(Modifiers.DATA())) {
                generatedBody.addAll(generateDataBody(id, parseCallArgs()));
                modifiers.remove(Modifiers.DATA());
            }

            Set<String> baseClasses = new HashSet<>();
            if (getCurrent().getType().equals(TokenTypes.COLON())) {
                getCurrentAndNext();
                baseClasses.add(getCurrentAndNext(TokenTypes.ID(), "Expected id as base class").getValue());

                while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                    getCurrentAndNext();
                    baseClasses.add(getCurrentAndNext(TokenTypes.ID(), "Expected id as base class after comma").getValue());
                }
            }

            boolean hasNewLine = getCurrent().getType().equals(TokenTypes.NEW_LINE());
            moveOverOptionalNewLines();

            if (!getCurrent().getType().equals(TokenTypes.LEFT_BRACE()) && hasNewLine) {
                return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
            }

            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open class body");

            if (getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                getCurrentAndNext();
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
                moveOverOptionalNewLines();
                return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
            }

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
            moveOverOptionalNewLines();

            LinkedHashMap<String, List<Expression>> enumIds = new LinkedHashMap<>();
            if (modifiers.contains(Modifiers.ENUM())) {
                if (!baseClasses.isEmpty()) throw new InvalidSyntaxException("Enum class can't have base classes");

                String enumId = getCurrentAndNext(TokenTypes.ID(), "Expected enum member id").getValue();
                List<Expression> args;
                if (getCurrent().getType().equals(TokenTypes.LEFT_PAREN())) args = parseArgs();
                else args = new ArrayList<>();
                enumIds.put(enumId, args);

                while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                    getCurrentAndNext();
                    moveOverOptionalNewLines();

                    enumId = getCurrentAndNext(TokenTypes.ID(), "Expected enum member id").getValue();
                    if (enumIds.containsKey(enumId)) throw new InvalidSyntaxException("Enum class can't have duplicated entries");
                    if (getCurrent().getType().equals(TokenTypes.LEFT_PAREN())) args = parseArgs();
                    else args = new ArrayList<>();
                    enumIds.put(enumId, args);
                }

                moveOverOptionalNewLines();
            }

            List<Statement> body = new ArrayList<>(generatedBody);
            while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                Statement statement = parse(RegistryIdentifier.ofDefault("class_body_statement"));
                body.add(statement);

                if (statement instanceof VariableDeclarationStatement variableDeclarationStatement) {
                    if (variableDeclarationStatement.getModifiers().contains(Modifiers.GET())) {
                        for (VariableDeclarationStatement.VariableDeclarationInfo variableDeclarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
                            body.add(getGetFunction(variableDeclarationInfo.getId(), variableDeclarationInfo.getDataType()));
                        }
                        variableDeclarationStatement.getModifiers().remove(Modifiers.GET());
                    }
                    if (variableDeclarationStatement.getModifiers().contains(Modifiers.SET()) && !variableDeclarationStatement.isConstant()) {
                        for (VariableDeclarationStatement.VariableDeclarationInfo variableDeclarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
                            body.add(getSetFunction(variableDeclarationInfo.getId(), variableDeclarationInfo.getDataType()));
                        }
                        variableDeclarationStatement.getModifiers().remove(Modifiers.SET());
                    }
                }

                moveOverOptionalNewLines();
            }

            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close class body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the class declaration");

            return new ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds);
        });

        register("class_body_statement", extra -> {
            Set<Modifier> modifiers = parseModifiers();

            if (getCurrent().getType().equals(TokenTypes.FUNCTION())) {
                return parse(RegistryIdentifier.ofDefault("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers);
            }
            if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(RegistryIdentifier.ofDefault("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, true);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (getCurrent().getType().equals(TokenTypes.CONSTRUCTOR())) {
                return parse(RegistryIdentifier.ofDefault("constructor_declaration_statement"), ConstructorDeclarationStatement.class, modifiers);
            }

            throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
        });

        register("function_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            getCurrentAndNext(TokenTypes.FUNCTION(), "Expected function keyword");

            String classId = null;
            String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after function keyword").getValue();
            if (getCurrent().getType().equals(TokenTypes.DOT())) {
                getCurrentAndNext();
                classId = id;
                id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after function keyword").getValue();
            }

            List<CallArgExpression> args = parseCallArgs();
            DataType dataType = parseDataType();

            if (modifiers.contains(Modifiers.ABSTRACT())) {
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
                return new FunctionDeclarationStatement(modifiers, id, args, new ArrayList<>(), dataType);
            }

            List<Statement> body;
            if (getCurrent().getType().equals(TokenTypes.ARROW())) {
                getCurrentAndNext();
                body = new ArrayList<>(List.of(parse(RegistryIdentifier.ofDefault("statement"))));
            }
            else {
                moveOverOptionalNewLines();
                getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open function body");
                body = parseBody();
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close function body");
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");
            }

            return new FunctionDeclarationStatement(modifiers, id, classId, args, body, dataType);
        });

        register("function_arg", extra -> {
            if (!getCurrent().getType().equals(TokenTypes.VARIABLE()))
                throw new UnexpectedTokenException("Expected variable keyword at the beginning of function arg", getCurrent().getLine());
            boolean isConstant = getCurrentAndNext().getValue().equals("val");
            String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after variable keyword in function arg").getValue();

            DataType dataType = parseDataType();
            return new CallArgExpression(id, dataType == null ? new DataType("Any", true) : dataType, isConstant);
        });

        register("variable_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);

            if (extra.length == 1) throw new IllegalArgumentException("Expected boolean as extra argument");
            if (!(extra[1] instanceof Boolean canWithoutValue)) throw new IllegalArgumentException("Expected boolean as extra argument");

            boolean isConstant = getCurrentAndNext(TokenTypes.VARIABLE(), "Expected variable keyword").getValue().equals("val");

            List<VariableDeclarationStatement.VariableDeclarationInfo> declarations = new ArrayList<>();
            declarations.add(parseVariableDeclarationInfo(isConstant, canWithoutValue));

            while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                getCurrentAndNext();
                declarations.add(parseVariableDeclarationInfo(isConstant, canWithoutValue));
            }

            return new VariableDeclarationStatement(modifiers, isConstant, declarations);
        });

        register("constructor_declaration_statement", extra -> {
            Set<Modifier> modifiers = getModifiersFromExtra(extra);
            getCurrentAndNext(TokenTypes.CONSTRUCTOR(), "Expected constructor keyword");

            List<CallArgExpression> args = parseCallArgs();

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open constructor body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
            moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                if (getCurrent().getType().equals(TokenTypes.BASE())) body.add(parse(RegistryIdentifier.ofDefault("base_call_statement")));
                else body.add(parse(RegistryIdentifier.ofDefault("statement")));
                moveOverOptionalNewLines();
            }

            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close constructor body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");

            return new ConstructorDeclarationStatement(modifiers, args, body);
        });

        register("base_call_statement", extra -> {
            getCurrentAndNext(TokenTypes.BASE(), "Expected BASE to start base call statement");

            String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after base keyword").getValue();

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
            List<Expression> args = new ArrayList<>();
            if (getCurrent().getType() != TokenTypes.RIGHT_PAREN()) {
                args.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));

                while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                    getCurrentAndNext();
                    args.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));
                }
            }
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
            return new BaseCallStatement(id, args);
        });

        register("statement", extra -> {
            Set<Modifier> modifiers = parseModifiers();

            if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(RegistryIdentifier.ofDefault("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                moveOverOptionalNewLines();
                return variableDeclarationStatement;
            }
            if (!modifiers.isEmpty()) throw new InvalidStatementException("Unexpected Modifier found", getCurrent().getLine());

            if (getCurrent().getType().equals(TokenTypes.IF())) return parse(RegistryIdentifier.ofDefault("if_statement"));
            if (getCurrent().getType().equals(TokenTypes.FOR())) return parse(RegistryIdentifier.ofDefault("for_statement"));
            if (getCurrent().getType().equals(TokenTypes.WHILE())) return parse(RegistryIdentifier.ofDefault("while_statement"));
            if (getCurrent().getType().equals(TokenTypes.RETURN())) return parse(RegistryIdentifier.ofDefault("return_statement"));
            if (getCurrent().getType().equals(TokenTypes.CONTINUE())) return parse(RegistryIdentifier.ofDefault("continue_statement"));
            if (getCurrent().getType().equals(TokenTypes.BREAK())) return parse(RegistryIdentifier.ofDefault("break_statement"));

            Expression expression = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            if (expression instanceof FunctionCallExpression || expression instanceof ClassCallExpression ||
                    expression instanceof AssignmentExpression || expression instanceof MemberExpression) {
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of expression");
                return expression;
            }

            throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
        });

        register("if_statement", extra -> {
            getCurrentAndNext(TokenTypes.IF(), "Expected if keyword");

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open if condition");
            Expression condition = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close if condition");
            moveOverOptionalNewLines();

            List<Statement> body = new ArrayList<>();
            if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                getCurrentAndNext();
                body = parseBody();
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
            }
            else body.add(parse(RegistryIdentifier.ofDefault("statement")));

            IfStatement elseStatement = null;
            if (getCurrent().getType().equals(TokenTypes.ELSE())) {
                getCurrentAndNext();
                if (getCurrent().getType().equals(TokenTypes.IF())) {
                    elseStatement = parse(RegistryIdentifier.ofDefault("if_statement"), IfStatement.class);
                }
                else {
                    List<Statement> elseBody = new ArrayList<>();
                    moveOverOptionalNewLines();
                    if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                        getCurrentAndNext();
                        elseBody = parseBody();
                        getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                        getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
                    }
                    else {
                        elseBody.add(parse(RegistryIdentifier.ofDefault("statement")));
                    }

                    elseStatement = new IfStatement(null, elseBody, null);
                }
            }

            return new IfStatement(condition, body, elseStatement);
        });

        register("for_statement", extra -> {
            getCurrentAndNext(TokenTypes.FOR(), "Expected for keyword");

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open for condition");

            if (currentLineHasToken(TokenTypes.IN())) {
                VariableDeclarationStatement variableDeclarationStatement = parse(RegistryIdentifier.ofDefault("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), true);
                if (variableDeclarationStatement.getDeclarationInfos().size() > 1) {
                    throw new InvalidSyntaxException("Foreach statement can declare only one variable");
                }
                getCurrentAndNext(TokenTypes.IN(), "Expected IN after variable declaration");
                Expression collection = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);

                getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

                moveOverOptionalNewLines();
                getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
                List<Statement> body = parseBody();
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

                return new ForeachStatement(variableDeclarationStatement, collection, body);
            }

            VariableDeclarationStatement variableDeclarationStatement = null;
            if (!getCurrent().getType().equals(TokenTypes.SEMICOLON())) {
                variableDeclarationStatement = parse(RegistryIdentifier.ofDefault("variable_declaration_statement"), VariableDeclarationStatement.class, new HashSet<>(), false);
            }
            getCurrentAndNext(TokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            Expression condition = null;
            if (!getCurrent().getType().equals(TokenTypes.SEMICOLON())) {
                condition = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            }
            getCurrentAndNext(TokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            AssignmentExpression assignmentExpression = null;
            if (!getCurrent().getType().equals(TokenTypes.RIGHT_PAREN())) {
                if (parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class) instanceof AssignmentExpression expression) {
                    assignmentExpression = expression;
                }
                else throw new InvalidSyntaxException("Expected assignment expression as for statement's arg");
            }
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
            List<Statement> body = parseBody();
            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

            return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
        });

        register("while_statement", extra -> {
            getCurrentAndNext(TokenTypes.WHILE(), "Expected while keyword");

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open while condition");
            Expression condition = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close while condition");

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open while body");
            List<Statement> body = parseBody();
            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close while body");

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the while statement");

            return new WhileStatement(condition, body);
        });

        register("return_statement", extra -> {
            getCurrentAndNext(TokenTypes.RETURN(), "Expected return keyword");

            Expression expression = null;
            if (!getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
                expression = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            }
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the return statement");

            return new ReturnStatement(expression);
        });

        register("continue_statement", extra ->  {
            getCurrentAndNext(TokenTypes.CONTINUE(), "Expected continue keyword");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the continue statement");
            return new ContinueStatement();
        });

        register("break_statement", extra ->  {
            getCurrentAndNext(TokenTypes.BREAK(), "Expected break keyword");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the break statement");
            return new BreakStatement();
        });

        register("expression", extra -> parseAfter(RegistryIdentifier.ofDefault("expression"), Expression.class));

        register("assignment_expression", extra -> {
            Expression left = parseAfter(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class);

            if (getCurrent().getType().equals(TokenTypes.ASSIGN())) {
                getCurrentAndNext();
                Expression value = parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class);
                return new AssignmentExpression(left, value);
            }
            else if (TokenTypeSets.OPERATOR_ASSIGN().contains(getCurrent().getType())) {
                Token token = getCurrentAndNext();
                Expression value = new OperatorExpression(
                        left,
                        parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class),
                        token.getValue().replaceAll("=$", ""), OperatorType.INFIX);
                return new AssignmentExpression(left, value);
            }

            return left;
        });

        register("list_creation_expression", extra -> {
            if (getCurrent().getType().equals(TokenTypes.LEFT_BRACKET())) {
                getCurrentAndNext();

                List<Expression> list = new ArrayList<>();
                while (!getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) {
                    list.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));
                    if (!getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) getCurrentAndNext(TokenTypes.COMMA(), "Expected comma as a separator between list elements");
                }
                getCurrentAndNext(TokenTypes.RIGHT_BRACKET(), "Expected right bracket to close list creation");

                return new ListCreationExpression(list);
            }

            return parseAfter(RegistryIdentifier.ofDefault("list_creation_expression"), Expression.class);
        });

        register("map_creation_expression", extra -> {
            if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                getCurrentAndNext();

                Map<Expression, Expression> map = new HashMap<>();
                while (!getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                    Expression key = parse(RegistryIdentifier.ofDefault("list_creation_expression"), Expression.class);
                    Expression value;
                    if (getCurrent().getType().equals(TokenTypes.ASSIGN())) {
                        getCurrentAndNext();
                        value = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
                    }
                    else value = new NullLiteral();
                    map.put(key, value);

                    if (!getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) getCurrentAndNext(TokenTypes.COMMA(), "Expected comma as a separator between map pairs");
                }
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close map creation");

                return new MapCreationExpression(map);
            }

            return parseAfter(RegistryIdentifier.ofDefault("map_creation_expression"), Expression.class);
        });

        register("null_check_expression", extra -> {
            Expression checkExpression = parseAfter(RegistryIdentifier.ofDefault("null_check_expression"), Expression.class);

            if (getCurrent().getType().equals(TokenTypes.QUESTION_COLON())) {
                getCurrentAndNext();
                Expression nullExpression = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
                return new NullCheckExpression(checkExpression, nullExpression);
            }

            return checkExpression;
        });

        register("logical_expression", extra -> {
            Expression left = parseAfter(RegistryIdentifier.ofDefault("logical_expression"), Expression.class);

            TokenType current = getCurrent().getType();
            while (current.equals(TokenTypes.AND()) || current.equals(TokenTypes.OR())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(RegistryIdentifier.ofDefault("logical_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

                current = getCurrent().getType();
            }

            return left;
        });

        register("comparison_expression", extra -> {
            Expression left = parseAfter(RegistryIdentifier.ofDefault("comparison_expression"), Expression.class);

            TokenType current = getCurrent().getType();
            while (current.equals(TokenTypes.EQUALS()) || current.equals(TokenTypes.NOT_EQUALS()) || current.equals(TokenTypes.GREATER()) ||
                    current.equals(TokenTypes.GREATER_OR_EQUALS()) || current.equals(TokenTypes.LESS()) || current.equals(TokenTypes.LESS_OR_EQUALS())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(RegistryIdentifier.ofDefault("comparison_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);

                current = getCurrent().getType();
            }

            return left;
        });

        register("is_expression", extra -> {
            Expression value = parseAfter(RegistryIdentifier.ofDefault("is_expression"), Expression.class);

            if (getCurrent().getType().equals(TokenTypes.IS()) || getCurrent().getType().equals(TokenTypes.IS_LIKE())) {
                boolean isLike = getCurrentAndNext().getType().equals(TokenTypes.IS_LIKE());
                return new IsExpression(value, getCurrentAndNext(TokenTypes.ID(), "Must specify data type after is keyword").getValue(), isLike);
            }

            return value;
        });

        register("addition_expression", extra -> {
            Expression left = parseAfter(RegistryIdentifier.ofDefault("addition_expression"), Expression.class);

            while (getCurrent().getType().equals(TokenTypes.PLUS()) || getCurrent().getType().equals(TokenTypes.MINUS())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(RegistryIdentifier.ofDefault("addition_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("multiplication_expression", extra -> {
            Expression left = parseAfter(RegistryIdentifier.ofDefault("multiplication_expression"), Expression.class);

            while (getCurrent().getType().equals(TokenTypes.MULTIPLY()) || getCurrent().getType().equals(TokenTypes.DIVIDE()) ||
                    getCurrent().getType().equals(TokenTypes.PERCENT())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(RegistryIdentifier.ofDefault("multiplication_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("power_expression", extra -> {
            Expression left = parseAfter(RegistryIdentifier.ofDefault("power_expression"), Expression.class);

            while (getCurrent().getType().equals(TokenTypes.POWER())) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parseAfter(RegistryIdentifier.ofDefault("power_expression"), Expression.class);
                left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
            }

            return left;
        });

        register("inversion_expression", extra -> {
            if (getCurrent().getType().equals(TokenTypes.INVERSION())) {
                getCurrentAndNext();
                Expression expression = parseAfter(RegistryIdentifier.ofDefault("inversion_expression"), Expression.class);
                return new OperatorExpression(expression, null, Operators.INVERSION());
            }

            return parseAfter(RegistryIdentifier.ofDefault("inversion_expression"), Expression.class);
        });

        register("negation_expression", extra -> {
            if (getCurrent().getType().equals(TokenTypes.MINUS())) {
                getCurrentAndNext();
                return new OperatorExpression(parseAfter(RegistryIdentifier.ofDefault("negation_expression"), Expression.class), null, Operators.NEGATION());
            }

            return parseAfter(RegistryIdentifier.ofDefault("negation_expression"), Expression.class);
        });

        register("postfix_expression", extra -> {
            Expression id = parseAfter(RegistryIdentifier.ofDefault("postfix_expression"), Expression.class);

            if (TokenTypeSets.OPERATOR_POSTFIX().contains(getCurrent().getType())) {
                Token token = getCurrentAndNext();
                Expression value = new OperatorExpression(id, new NumberLiteral("1"), token.getValue().substring(1), OperatorType.INFIX);
                return new AssignmentExpression(id, value);
            }

            return id;
        });

        register("member_expression", extra -> {
            Expression object = parseAfter(RegistryIdentifier.ofDefault("member_expression"), Expression.class);

            while (TokenTypeSets.MEMBER_ACCESS().contains(getCurrent().getType())) {
                boolean isNullSafe = getCurrentAndNext().getType().equals(TokenTypes.QUESTION_DOT());
                Expression member = parseAfter(RegistryIdentifier.ofDefault("member_expression"), Expression.class);
                if (!(member instanceof Identifier) && !(member instanceof CallExpression)) {
                    throw new UnexpectedTokenException("Right side must be either Identifier or Call", getCurrent().getLine());
                }
                object = new MemberExpression(object, member, isNullSafe);
            }

            return object;
        });

        register("class_call_expression", extra -> {
            if (getCurrent().getType().equals(TokenTypes.NEW())) {
                getCurrentAndNext();
                Expression expression = parseAfter(RegistryIdentifier.ofDefault("class_call_expression"), Expression.class);
                if (expression instanceof CallExpression callExpression) {
                    return new ClassCallExpression(new ClassIdentifier(callExpression.getCaller().getId()), callExpression.getArgs());
                }
                throw new InvalidSyntaxException("Class creation must be call expression");
            }

            return parseAfter(RegistryIdentifier.ofDefault("class_call_expression"), Expression.class);
        });

        register("function_call_expression", extra -> {
            Expression expression = parseAfter(RegistryIdentifier.ofDefault("function_call_expression"), Expression.class);

            if (getCurrent().getType().equals(TokenTypes.LEFT_PAREN())) {
                if (!(expression instanceof Identifier identifier)) throw new InvalidSyntaxException("Can't call non-identifier");
                return new FunctionCallExpression(new FunctionIdentifier(identifier.getId()), parseArgs());
            }

            return expression;
        });

        register("primary_expression", extra -> {
            TokenType tokenType = getCurrent().getType();

            if (tokenType.equals(TokenTypes.ID())) {
                if ((getPos() != 0 && getTokens().get(getPos() - 1).getType().equals(TokenTypes.NEW())) ||
                        (getTokens().size() > getPos() + 1 && getTokens().get(getPos() + 1).getType().equals(TokenTypes.DOT()) && getPos() != 0 && !getTokens().get(getPos() - 1).getType().equals(TokenTypes.DOT())))
                    return new ClassIdentifier(getCurrentAndNext().getValue());
                else if (getTokens().size() > getPos() + 1 && getTokens().get(getPos() + 1).getType().equals(TokenTypes.LEFT_PAREN())) {
                    return new FunctionIdentifier(getCurrentAndNext().getValue());
                }
                else return new VariableIdentifier(getCurrentAndNext().getValue());
            }
            if (tokenType.equals(TokenTypes.NULL())) {
                getCurrentAndNext();
                return new NullLiteral();
            }
            if (tokenType.equals(TokenTypes.NUMBER())) return new NumberLiteral(getCurrentAndNext().getValue());
            if (tokenType.equals(TokenTypes.STRING())) {
                String value = getCurrentAndNext().getValue();
                return new StringLiteral(value.substring(1, value.length() - 1));
            }
            if (tokenType.equals(TokenTypes.BOOLEAN())) return new BooleanLiteral(Boolean.parseBoolean(getCurrentAndNext().getValue()));
            if (tokenType.equals(TokenTypes.THIS())) {
                getCurrentAndNext();
                return new ThisLiteral();
            }
            if (tokenType.equals(TokenTypes.LEFT_PAREN())) {
                getCurrentAndNext();
                Expression value = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
                getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis");
                return value;
            }

            throw new InvalidStatementException("Can't parse token with type " + tokenType.getId());
        });
    }



    private static Set<Modifier> parseModifiers() {
        Set<Modifier> modifiers = new HashSet<>();
        while (getCurrent().getType().equals(TokenTypes.ID())) {
            String id = getCurrent().getValue();
            Modifier modifier = Modifiers.parse(id);
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
        getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<CallArgExpression> args = new ArrayList<>();
        if (!getCurrent().getType().equals(TokenTypes.RIGHT_PAREN())) {
            args.add(parse(RegistryIdentifier.ofDefault("function_arg"), CallArgExpression.class));

            while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                getCurrentAndNext();
                args.add(parse(RegistryIdentifier.ofDefault("function_arg"), CallArgExpression.class));
            }
        }
        getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    private static List<Expression> parseArgs() {
        getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        List<Expression> args = new ArrayList<>();
        if (!getCurrent().getType().equals(TokenTypes.RIGHT_PAREN())) {
            args.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));

            while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                getCurrentAndNext();
                args.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));
            }
        }
        getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    private static DataType parseDataType() {
        if (getCurrent().getType().equals(TokenTypes.COLON())) {
            getCurrentAndNext();
            String dataTypeId = getCurrentAndNext(TokenTypes.ID(), "Must specify variable's data type after colon").getValue();

            if (getCurrent().getType().equals(TokenTypes.QUESTION())) {
                getCurrentAndNext();
                return new DataType(dataTypeId, true);
            }
            return new DataType(dataTypeId, false);
        }
        return null;
    }

    private static List<Statement> parseBody() {
        List<Statement> body = new ArrayList<>();
        getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected new line");
        moveOverOptionalNewLines();

        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parse(RegistryIdentifier.ofDefault("statement")));
            moveOverOptionalNewLines();
        }

        moveOverOptionalNewLines();
        return body;
    }

    private static VariableDeclarationStatement.VariableDeclarationInfo parseVariableDeclarationInfo(boolean isConstant, boolean canWithoutValue) {
        String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier in variable declaration statement").getValue();

        DataType dataType = parseDataType();
        if (dataType == null) dataType = new DataType("Any", true);

        if (!getCurrent().getType().equals(TokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, null);
            }
            if (isConstant) throw new InvalidStatementException("Can't declare a constant variable without a value", getCurrent().getLine());
            return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, new NullLiteral());
        }

        getCurrentAndNext(TokenTypes.ASSIGN(), "Expected ASSIGN token after the id in variable declaration");

        return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, parse(RegistryIdentifier.ofDefault("expression"), Expression.class));
    }

    private static List<Statement> generateDataBody(String id, List<CallArgExpression> dataVariables) {
        List<Statement> body = new ArrayList<>();

        for (CallArgExpression dataVariable : dataVariables) {
            body.add(new VariableDeclarationStatement(
                    Set.of(Modifiers.PRIVATE()),
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
                new DataType("String", false)));

        List<Expression> copyArgs = new ArrayList<>();
        for (CallArgExpression dataVariable : dataVariables) {
            copyArgs.add(new VariableIdentifier(dataVariable.getId()));
        }
        body.add(new FunctionDeclarationStatement(
                Set.of(),
                "copy",
                List.of(),
                List.of(new ReturnStatement(new ClassCallExpression(new ClassIdentifier(id), copyArgs))),
                new DataType(id, false)));

        Expression equalsExpression;
        if (!dataVariables.isEmpty()) {
            equalsExpression = new OperatorExpression(
                    new VariableIdentifier(dataVariables.getFirst().getId()),
                    new MemberExpression(
                            new VariableIdentifier("value"),
                            new FunctionCallExpression(
                                    new FunctionIdentifier(Utils.generatePrefixedName("get", dataVariables.getFirst().getId())),
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
                                                new FunctionIdentifier(Utils.generatePrefixedName("get", dataVariable.getId())),
                                                List.of()),
                                        false),
                                "==", OperatorType.INFIX),
                        "&&", OperatorType.INFIX
                );
            }
        }
        else equalsExpression = new BooleanLiteral(true);
        body.add(new FunctionDeclarationStatement(
                Set.of(Modifiers.OPERATOR()),
                "equals",
                List.of(new CallArgExpression("value", new DataType("Any", true), true)),
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
                new DataType("Boolean", false)
        ));

        return body;
    }

    private static FunctionDeclarationStatement getGetFunction(String id, DataType dataType) {
        return new FunctionDeclarationStatement(
                Set.of(),
                Utils.generatePrefixedName("get", id),
                List.of(),
                List.of(new ReturnStatement(new VariableIdentifier(id))),
                dataType);
    }

    private static FunctionDeclarationStatement getSetFunction(String id, DataType dataType) {
        return new FunctionDeclarationStatement(
                Set.of(),
                Utils.generatePrefixedName("set", id),
                List.of(new CallArgExpression(id, dataType, true)),
                List.of(new AssignmentExpression(new MemberExpression(new ThisLiteral(), new VariableIdentifier(id), false), new VariableIdentifier(id))),
                null);
    }



    private static void register(String id, ParsingFunction<? extends Statement> parsingFunction) {
        Registries.PARSING_FUNCTIONS.register(RegistryIdentifier.ofDefault(id), parsingFunction);
    }
}