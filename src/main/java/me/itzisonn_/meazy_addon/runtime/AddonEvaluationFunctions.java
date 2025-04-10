package me.itzisonn_.meazy_addon.runtime;

import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.*;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.parser.AddonModifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.CollectionClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.ListClassValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collections.MapClassValue;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.classes.RuntimeClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.RuntimeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;
import me.itzisonn_.meazy.runtime.value.function.RuntimeFunctionValue;
import me.itzisonn_.meazy_addon.runtime.value.BaseClassIdValue;
import me.itzisonn_.meazy_addon.runtime.value.BooleanValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassValue;
import me.itzisonn_.meazy_addon.runtime.value.number.*;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.BreakInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy_addon.runtime.value.statement_info.ReturnInfoValue;
import me.itzisonn_.registry.RegistryEntry;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * All basic EvaluationFunctions
 *
 * @see Registries#EVALUATION_FUNCTIONS
 */
public final class AddonEvaluationFunctions {
    private static boolean isInit = false;
    private static final List<FunctionDeclarationStatement> extensionFunctions = new ArrayList<>();

    private AddonEvaluationFunctions() {}



    /**
     * Initializes {@link Registries#EVALUATION_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#EVALUATION_FUNCTIONS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("EvaluationFunctions have already been initialized!");
        isInit = true;

        register("program", Program.class, (program, environment, extra) -> {
            for (Statement statement : program.getBody()) {
                Interpreter.evaluate(statement, environment);
            }

            return null;
        });

        register("class_declaration_statement", ClassDeclarationStatement.class, (classDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare class in this environment!");
            }

            for (Modifier modifier : classDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(classDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            ClassEnvironment classEnvironment;
            try {
                classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue()
                        .getConstructor(ClassDeclarationEnvironment.class, boolean.class, String.class, Set.class)
                        .newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), true, classDeclarationStatement.getId(), classDeclarationStatement.getModifiers());
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            for (Statement statement : classDeclarationStatement.getBody()) {
                Interpreter.evaluate(statement, classEnvironment);
            }

            for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
                classEnvironment.declareVariable(new VariableValue(
                        enumId,
                        new DataType(classDeclarationStatement.getId(), false),
                        null,
                        true,
                        Set.of(AddonModifiers.SHARED()),
                        false
                ));
            }

            if (hasRepeatedBaseClasses(classDeclarationStatement.getBaseClasses(), new ArrayList<>())) {
                throw new InvalidIdentifierException("Class with id " + classDeclarationStatement.getId() + " has repeated base classes");
            }
            if (hasRepeatedVariables(
                    classDeclarationStatement.getBaseClasses(),
                    new ArrayList<>(classEnvironment.getVariables().stream().map(VariableValue::getId).toList()))) {
                throw new InvalidIdentifierException("Class with id " + classDeclarationStatement.getId() + " has repeated variables");
            }

            RuntimeClassValue runtimeClassValue = new RuntimeClassValue(
                    classDeclarationStatement.getBaseClasses(),
                    classEnvironment,
                    classDeclarationStatement.getBody());
            classDeclarationEnvironment.declareClass(runtimeClassValue);

            int enumOrdinal = 1;
            List<ClassValue> enumValues = new ArrayList<>();
            for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
                List<RuntimeValue<?>> args = classDeclarationStatement.getEnumIds().get(enumId).stream().map(expression -> Interpreter.evaluate(expression, classEnvironment)).collect(Collectors.toList());
                ClassEnvironment enumEnvironment = initClassEnvironment(runtimeClassValue, classEnvironment, args);

                int finalEnumOrdinal = enumOrdinal;
                enumEnvironment.declareFunction(new NativeFunctionValue("getOrdinal", List.of(), new DataType("Int", false), enumEnvironment, Set.of()) {
                    @Override
                    public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                        return new IntValue(finalEnumOrdinal);
                    }
                });
                enumOrdinal++;

                ClassValue enumValue = new NativeClassValue(enumEnvironment);
                classEnvironment.assignVariable(enumId, enumValue);
                enumValues.add(enumValue);
            }

            if (classDeclarationStatement.getModifiers().contains(AddonModifiers.ENUM())) {
                classEnvironment.declareFunction(new NativeFunctionValue("getValues", List.of(), new DataType("List", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
                    @Override
                    public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                        return new ListClassValue(new ArrayList<>(enumValues));
                    }
                });
            }

            return null;
        });

        register("function_declaration_statement", FunctionDeclarationStatement.class, (functionDeclarationStatement, environment, extra) -> {
            FunctionDeclarationEnvironment functionDeclarationEnvironment;

            if (functionDeclarationStatement.getClassId() != null) {
                ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(functionDeclarationStatement.getClassId());
                if (classValue == null) throw new InvalidIdentifierException("Can't find class with id " + functionDeclarationStatement.getClassId());
                if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidIdentifierException("Can't extend final class with id " + functionDeclarationStatement.getClassId());
                if (!extensionFunctions.contains(functionDeclarationStatement)) extensionFunctions.add(functionDeclarationStatement);
                return null;
            }
            if (!(environment instanceof FunctionDeclarationEnvironment declarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare function in this environment!");
            }
            else functionDeclarationEnvironment = declarationEnvironment;

            for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(functionDeclarationStatement, environment)) throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            RuntimeFunctionValue runtimeFunctionValue = new RuntimeFunctionValue(
                    functionDeclarationStatement.getId(),
                    functionDeclarationStatement.getArgs(),
                    functionDeclarationStatement.getBody(),
                    functionDeclarationStatement.getReturnDataType(),
                    functionDeclarationEnvironment,
                    functionDeclarationStatement.getModifiers());

            if (functionDeclarationStatement.getModifiers().contains(AddonModifiers.OPERATOR())) {
                if (!(environment instanceof ClassEnvironment classEnvironment)) {
                    throw new InvalidSyntaxException("Can't declare operator function not inside a class");
                }

                Operator operator = AddonOperators.parseById(runtimeFunctionValue.getId());
                if (operator == null) {
                    throw new InvalidSyntaxException("Can't declare operator function because operator " + runtimeFunctionValue.getId() + " doesn't exist");
                }

                int args = operator.getOperatorType() == OperatorType.INFIX ? 1 : 0;
                if (runtimeFunctionValue.getArgs().size() != args) {
                    throw new InvalidSyntaxException("Function for operator " + runtimeFunctionValue.getId() + " must have " + args + " args");
                }

                if (runtimeFunctionValue.getReturnDataType() == null) {
                    throw new InvalidSyntaxException("Operator function must return value");
                }

                classEnvironment.declareOperatorFunction(runtimeFunctionValue);
            }
            else functionDeclarationEnvironment.declareFunction(runtimeFunctionValue);
            return null;
        });

        register("variable_declaration_statement", VariableDeclarationStatement.class, (variableDeclarationStatement, environment, extra) -> {
            for (Modifier modifier : variableDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(variableDeclarationStatement, environment)) throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            Set<Modifier> modifiers = new HashSet<>(variableDeclarationStatement.getModifiers());
            if (!(environment instanceof ClassEnvironment) && environment.isShared() && !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED())) modifiers.add(AddonModifiers.SHARED());

            variableDeclarationStatement.getDeclarationInfos().forEach(variableDeclarationInfo -> {
                RuntimeValue<?> value;
                if (variableDeclarationInfo.getValue() == null) value = null;
                else if (environment instanceof ClassEnvironment && environment.isShared() &&
                        !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED())) value = null;
                else value = Interpreter.evaluate(variableDeclarationInfo.getValue(), environment);

                environment.declareVariable(new VariableValue(
                        variableDeclarationInfo.getId(),
                        variableDeclarationInfo.getDataType(),
                        value,
                        variableDeclarationStatement.isConstant(),
                        modifiers,
                        false
                ));
            });

            return null;
        });

        register("constructor_declaration_statement", ConstructorDeclarationStatement.class, (constructorDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare constructor in this environment!");
            }

            for (Modifier modifier : constructorDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(constructorDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            RuntimeConstructorValue runtimeConstructorValue = new RuntimeConstructorValue(
                    constructorDeclarationStatement.getArgs(),
                    constructorDeclarationStatement.getBody(),
                    constructorDeclarationEnvironment,
                    constructorDeclarationStatement.getModifiers());

            constructorDeclarationEnvironment.declareConstructor(runtimeConstructorValue);
            return null;
        });

        register("base_call_statement", BaseCallStatement.class, (baseCallStatement, environment, extra) -> {
            ClassEnvironment classEnvironment;
            if (extra.length == 1 && extra[0] instanceof ClassEnvironment env) classEnvironment = env;
            else throw new InvalidSyntaxException("Unknown error occurred");

            if (!(environment instanceof ConstructorEnvironment constructorEnvironment)) {
                throw new InvalidSyntaxException("Can't use BaseCallStatement in this environment!");
            }

            ClassValue baseClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(baseCallStatement.getId());
            List<RuntimeValue<?>> args = baseCallStatement.getArgs().stream().map(expression -> Interpreter.evaluate(expression, environment)).collect(Collectors.toList());
            classEnvironment.addBaseClass(initClassEnvironment(baseClassValue, constructorEnvironment, args));
            return new BaseClassIdValue(baseCallStatement.getId());
        });


        register("if_statement", IfStatement.class, (ifStatement, environment, extra) -> {
            while (ifStatement != null) {
                if (ifStatement.getCondition() != null) {
                    if (!parseCondition(ifStatement.getCondition(), environment)) {
                        ifStatement = ifStatement.getElseStatement();
                        continue;
                    }
                }

                Environment ifEnvironment;
                try {
                    ifEnvironment = Registries.ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < ifStatement.getBody().size(); i++) {
                    Statement statement = ifStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, ifEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < ifStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < ifStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        return new ContinueInfoValue();
                    }
                    if (result instanceof ContinueInfoValue continueInfoValue) {
                        return continueInfoValue;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < ifStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        return new BreakInfoValue();
                    }
                    if (result instanceof BreakInfoValue breakInfoValue) {
                        return breakInfoValue;
                    }
                }
                break;
            }
            return null;
        });

        register("for_statement", ForStatement.class, (forStatement, environment, extra) -> {
            LoopEnvironment forEnvironment;
            try {
                forEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                    forEnvironment.declareVariable(new VariableValue(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            variableDeclarationInfo.getValue() == null ?
                                    null :
                                    Interpreter.evaluate(variableDeclarationInfo.getValue(), environment),
                            forStatement.getVariableDeclarationStatement().isConstant(),
                            Set.of(),
                            false))
            );

            main:
            while (parseCondition(forStatement.getCondition(), forEnvironment)) {
                for (int i = 0; i < forStatement.getBody().size(); i++) {
                    Statement statement = forStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, forEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < forStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < forStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        break;
                    }
                    if (result instanceof ContinueInfoValue) {
                        break;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < forStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        break main;
                    }
                    if (result instanceof BreakInfoValue) {
                        break main;
                    }
                }

                List<VariableValue> variableValues = new ArrayList<>();
                forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                        variableValues.add(forEnvironment.getVariable(variableDeclarationInfo.getId())));

                forEnvironment.clearVariables();
                for (VariableValue variableValue : variableValues) {
                    forEnvironment.declareVariable(new VariableValue(
                            variableValue.getId(),
                            variableValue.getDataType(),
                            variableValue.getValue(),
                            variableValue.isConstant(),
                            new HashSet<>(),
                            false));
                }
                evaluateAssignmentExpression(forStatement.getAssignmentExpression(), forEnvironment);
            }

            return null;
        });

        register("foreach_statement", ForeachStatement.class, (foreachStatement, environment, extra) -> {
            LoopEnvironment foreachEnvironment;
            try {
                foreachEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            RuntimeValue<?> rawCollectionValue = Interpreter.evaluate(foreachStatement.getCollection(), foreachEnvironment).getFinalRuntimeValue();
            if (!(rawCollectionValue instanceof ClassValue classValue && classValue.getBaseClasses().contains("Collection")))
                throw new InvalidSyntaxException("Can't get members of non-collection value");

            VariableValue variable = classValue.getEnvironment().getVariable("value");
            if (variable == null) throw new InvalidSyntaxException("Can't get members of non-collection value");
            if (!(variable.getValue() instanceof CollectionClassValue.InnerCollectionValue<?> collectionValue)) throw new InvalidSyntaxException("Can't get members of non-collection value");

            main:
            for (RuntimeValue<?> runtimeValue : collectionValue.getValue()) {
                foreachEnvironment.clearVariables();

                foreachEnvironment.declareVariable(new VariableValue(
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getId(),
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getDataType(),
                        runtimeValue,
                        foreachStatement.getVariableDeclarationStatement().isConstant(),
                        new HashSet<>(),
                        false));

                for (int i = 0; i < foreachStatement.getBody().size(); i++) {
                    Statement statement = foreachStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, foreachEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < foreachStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < foreachStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        break;
                    }
                    if (result instanceof ContinueInfoValue) {
                        break;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < foreachStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        break main;
                    }
                    if (result instanceof BreakInfoValue) {
                        break main;
                    }
                }
            }

            return null;
        });

        register("while_statement", WhileStatement.class, (whileStatement, environment, extra) -> {
            LoopEnvironment whileEnvironment;
            try {
                whileEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            main:
            while (parseCondition(whileStatement.getCondition(), environment)) {
                whileEnvironment.clearVariables();

                for (int i = 0; i < whileStatement.getBody().size(); i++) {
                    Statement statement = whileStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, whileEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        break;
                    }
                    if (result instanceof ContinueInfoValue) {
                        break;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        break main;
                    }
                    if (result instanceof BreakInfoValue) {
                        break main;
                    }
                }
            }

            return null;
        });

        register("return_statement", ReturnStatement.class, (returnStatement, environment, extra) -> {
            if (environment instanceof FunctionEnvironment || environment.hasParent(parent -> parent instanceof FunctionEnvironment)) {
                if (returnStatement.getValue() == null) return null;
                return Interpreter.evaluate(returnStatement.getValue(), environment);
            }

            if (returnStatement.getValue() == null) {
                return null;
            }

            throw new InvalidSyntaxException("Can't return value not inside a function");
        });

        register("continue_statement", ContinueStatement.class, (continueStatement, environment, extra) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can't use continue statement outside of for/while statements");
        });

        register("break_statement", BreakStatement.class, (breakStatement, environment, extra) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can't use break statement outside of for/while statements");
        });

        register("assignment_expression", AssignmentExpression.class, (assignmentExpression, environment, extra) -> evaluateAssignmentExpression(assignmentExpression, environment));

        register("list_creation_expression", ListCreationExpression.class, (listCreationExpression, environment, extra) -> {
            List<RuntimeValue<?>> list = listCreationExpression.getList().stream().map(expression -> Interpreter.evaluate(expression, environment)).collect(Collectors.toList());
            return new ListClassValue(list);
        });

        register("map_creation_expression", MapCreationExpression.class, (mapCreationExpression, environment, extra) -> {
            Map<RuntimeValue<?>, RuntimeValue<?>> map = new HashMap<>();

            for (Expression key : mapCreationExpression.getMap().keySet()) {
                Expression value = mapCreationExpression.getMap().get(key);
                map.put(Interpreter.evaluate(key, environment), Interpreter.evaluate(value, environment));
            }

            return new MapClassValue(map);
        });

        register("null_check_expression", NullCheckExpression.class, (nullCheckExpression, environment, extra) -> {
            RuntimeValue<?> checkValue = Interpreter.evaluate(nullCheckExpression.getCheckExpression(), environment).getFinalRuntimeValue();

            if (checkValue instanceof NullValue) {
                return Interpreter.evaluate(nullCheckExpression.getNullExpression(), environment).getFinalRuntimeValue();
            }
            return checkValue;
        });

        register("is_expression", IsExpression.class, (isExpression, environment, extra) -> {
            RuntimeValue<?> value = Interpreter.evaluate(isExpression.getValue(), environment).getFinalRuntimeValue();

            ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(isExpression.getDataType());
            if (classValue == null) throw new InvalidSyntaxException("Data type with id " + isExpression.getDataType() + " doesn't exist");

            if (isExpression.isLike()) return new BooleanValue(classValue.isLikeMatches(value.getFinalRuntimeValue()));
            return new BooleanValue(classValue.isMatches(value.getFinalRuntimeValue()));
        });

        register("operator_expression", OperatorExpression.class, (operatorExpression, environment, extra) -> {
            RuntimeValue<?> left = Interpreter.evaluate(operatorExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = operatorExpression.getRight() != null ? Interpreter.evaluate(operatorExpression.getRight(), environment).getFinalRuntimeValue() : null;

            if (operatorExpression.getType() == OperatorType.INFIX && right == null) throw new RuntimeException("Infix expression must contain both parts");
            if (operatorExpression.getType() != OperatorType.INFIX && right != null) throw new RuntimeException("Prefix and suffix expression must contain only one part");

            ClassValue classValue;
            List<RuntimeValue<?>> args;
            if (left instanceof ClassValue leftClassValue) {
                classValue = leftClassValue;
                args = right == null ? List.of() : List.of(right);
            }
            else if (right instanceof ClassValue rightClassValue) {
                classValue = rightClassValue;
                args = List.of(left);
            }
            else {
                classValue = null;
                args = null;
            }
            if (classValue != null) {
                RegistryEntry<Operator> entry = Registries.OPERATORS.getEntry(operatorExpression.getOperator());
                if (entry != null) {
                    FunctionValue operatorFunction = classValue.getEnvironment().getOperatorFunction(entry.getIdentifier().getId(), args);
                    if (operatorFunction != null) return callFunction(operatorFunction, args);
                }
            }

            RuntimeValue<?> result = operatorExpression.getOperator().calculate(left, right);
            if (result == null) throw new UnsupportedOperatorException(operatorExpression.getOperator().getSymbol());

            return result;
        });

        register("class_call_expression", ClassCallExpression.class, (classCallExpression, environment, extra) -> {
            Environment extraEnvironment;
            if (extra.length == 0) extraEnvironment = environment;
            else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
            else extraEnvironment = environment;

            List<RuntimeValue<?>> args = classCallExpression.getArgs().stream().map(expression -> Interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());
            RuntimeValue<?> rawClass = Interpreter.evaluate(classCallExpression.getCaller(), environment);

            if (!(rawClass instanceof ClassValue classValue)) throw new InvalidCallException("Can't call " + rawClass.getClass().getName() + " because it's not a class");
            if (classValue.getModifiers().contains(AddonModifiers.ABSTRACT())) throw new InvalidCallException("Can't create instance of an abstract class " + classValue.getId());
            if (classValue.getModifiers().contains(AddonModifiers.ENUM())) throw new InvalidCallException("Can't create instance of an enum class " + classValue.getId());

            ClassEnvironment classEnvironment = initClassEnvironment(classValue, extraEnvironment, args);
            if (classValue instanceof RuntimeClassValue runtimeClassValue) return new RuntimeClassValue(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
            if (classValue instanceof NativeClassValue) return new NativeClassValue(classValue.getBaseClasses(), classEnvironment);

            throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
        });

        register("member_expression", MemberExpression.class, (memberExpression, environment, extra) -> {
            RuntimeValue<?> value = Interpreter.evaluate(memberExpression.getObject(), environment).getFinalRuntimeValue();

            if (value instanceof NullValue) {
                if (memberExpression.isNullSafe()) return value;
                else throw new InvalidSyntaxException("Can't get member of null value");
            }

            if (value instanceof ClassValue classValue) {
                return Interpreter.evaluate(memberExpression.getMember(), classValue.getEnvironment(), environment);
            }

            throw new InvalidSyntaxException("Can't get member of " + value + " because it's not a class");
        });

        register("function_call_expression", FunctionCallExpression.class, (functionCallExpression, environment, extra) -> {
            Environment extraEnvironment;
            if (extra.length == 0) extraEnvironment = environment;
            else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
            else extraEnvironment = environment;

            List<RuntimeValue<?>> args = functionCallExpression.getArgs().stream().map(expression -> Interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());
            RuntimeValue<?> function = Interpreter.evaluate(functionCallExpression.getCaller(), environment, args);
            if (!(function instanceof FunctionValue functionValue)) {
                throw new InvalidCallException("Can't call " + function.getValue() + " because it's not a function");
            }

            return callFunction(functionValue, args);
        });

        register("identifier", Identifier.class, new EvaluationFunction<>() {
            @Override
            public RuntimeValue<?> evaluate(Identifier identifier, Environment environment, Object... extra) {
                Environment requestEnvironment;
                if (extra.length == 0) requestEnvironment = environment;
                else if (extra[0] instanceof Environment env) requestEnvironment = env;
                else requestEnvironment = environment;

                if (identifier instanceof VariableIdentifier) {
                    Environment variableDeclarationEnvironment = environment.getVariableDeclarationEnvironment(identifier.getId());
                    if (variableDeclarationEnvironment == null) throw new InvalidIdentifierException("Variable with id " + identifier.getId() + " doesn't exist");

                    VariableValue variableValue = variableDeclarationEnvironment.getVariable(identifier.getId());
                    if (variableValue == null) throw new InvalidIdentifierException("Variable with id " + identifier.getId() + " doesn't exist");

                    if (variableValue.getModifiers().contains(AddonModifiers.PRIVATE()) && requestEnvironment != variableDeclarationEnvironment &&
                            !requestEnvironment.hasParent(variableDeclarationEnvironment))
                        throw new InvalidAccessException("Can't access private variable with id " + identifier.getId());

                    if (variableValue.getModifiers().contains(AddonModifiers.PROTECTED()) && requestEnvironment != variableDeclarationEnvironment &&
                            !requestEnvironment.hasParent(variableDeclarationEnvironment) && !requestEnvironment.hasParent(parentEnv -> {
                                if (parentEnv instanceof ClassEnvironment classEnvironment) {
                                    ClassEnvironment declarationEnvironment = (ClassEnvironment) variableDeclarationEnvironment.getParent(env -> env instanceof ClassEnvironment);
                                    if (declarationEnvironment == null) return false;
                                    if (classEnvironment.getId().equals(declarationEnvironment.getId())) return true;

                                    ClassValue parentClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(classEnvironment.getId());
                                    if (parentClassValue == null) {
                                        throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " doesn't exist");
                                    }
                                    return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                                }
                                return false;
                    })) {
                        throw new InvalidAccessException("Can't access protected variable with id " + identifier.getId());
                    }

                    if (!variableValue.getModifiers().contains(AddonModifiers.SHARED()) && environment.isShared() && !variableValue.isArgument())
                        throw new InvalidAccessException("Can't access not-shared variable with id " + identifier.getId() + " from shared environment");

                    return variableValue;
                }

                if (identifier instanceof FunctionIdentifier) {
                    if (extra.length == 0 || !(extra[0] instanceof List<?> rawArgs)) throw new RuntimeException("Invalid function args");

                    List<RuntimeValue<?>> args = rawArgs.stream().map(object -> {
                        if (object instanceof RuntimeValue<?> arg) return arg;
                        throw new RuntimeException("Unknown error occurred. Probably used function has returned nothing");
                    }).collect(Collectors.toList());

                    FunctionDeclarationEnvironment functionDeclarationEnvironment = environment.getFunctionDeclarationEnvironment(identifier.getId(), args);
                    if (functionDeclarationEnvironment == null) throw new InvalidIdentifierException("Function with id " + identifier.getId() + " doesn't exist");

                    FunctionValue functionValue = functionDeclarationEnvironment.getFunction(identifier.getId(), args);
                    if (functionValue == null) throw new InvalidIdentifierException("Function with id " + identifier.getId() + " doesn't exist");

                    if (functionValue.getModifiers().contains(AddonModifiers.PRIVATE()) && requestEnvironment != functionDeclarationEnvironment &&
                            !requestEnvironment.hasParent(functionDeclarationEnvironment))
                        throw new InvalidAccessException("Can't access private function with id " + identifier.getId());

                    if (functionValue.getModifiers().contains(AddonModifiers.PROTECTED()) && requestEnvironment != functionDeclarationEnvironment &&
                            !requestEnvironment.hasParent(functionDeclarationEnvironment) && !requestEnvironment.hasParent(parentEnv -> {
                        if (parentEnv instanceof ClassEnvironment classEnvironment) {
                            ClassEnvironment declarationEnvironment;
                            if (functionDeclarationEnvironment instanceof ClassEnvironment env) declarationEnvironment = env;
                            else declarationEnvironment = (ClassEnvironment) functionDeclarationEnvironment.getParent(env -> env instanceof ClassEnvironment);

                            if (declarationEnvironment == null) return false;
                            if (classEnvironment.getId().equals(declarationEnvironment.getId())) return true;

                            ClassValue parentClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(classEnvironment.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidAccessException("Can't access protected function with id " + identifier.getId());
                    }

                    if (!functionValue.getModifiers().contains(AddonModifiers.SHARED()) && environment.isShared())
                        throw new InvalidAccessException("Can't access not-shared function with id " + identifier.getId() + " from shared environment");

                    return functionValue;
                }

                if (identifier instanceof ClassIdentifier) {
                    ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(identifier.getId());
                    if (classValue != null) return classValue;

                    return evaluate(new VariableIdentifier(identifier.getId()), environment, extra);
                }

                throw new InvalidIdentifierException("Invalid identifier " + identifier.getClass().getName());
            }
        });

        register("null_literal", NullLiteral.class, (nullLiteral, environment, extra) -> new NullValue());
        register("number_literal", NumberLiteral.class, (numberLiteral, environment, extra) -> {
            String value = numberLiteral.getValue();
            if (!value.contains(".")) {
                try {
                    return new IntValue(Integer.parseInt(value));
                }
                catch (NumberFormatException ignore) {
                    try {
                        return new LongValue(Long.parseLong(value));
                    }
                    catch (NumberFormatException ignore2) {
                        throw new InvalidSyntaxException("Number " + value + " is too big");
                    }
                }
            }

            try {
                return new FloatValue(Float.parseFloat(value));
            }
            catch (NumberFormatException ignore) {
                try {
                    return new DoubleValue(Double.parseDouble(value));
                }
                catch (NumberFormatException ignore2) {
                    throw new InvalidSyntaxException("Number " + value + " is too big");
                }
            }
        });
        register("string_literal", StringLiteral.class, (stringLiteral, environment, extra) -> new StringClassValue(stringLiteral.getValue()));
        register("boolean_literal", BooleanLiteral.class, (booleanLiteral, environment, extra) -> new BooleanValue(booleanLiteral.isValue()));

        register("this_literal", ThisLiteral.class, (thisLiteral, environment, extra) -> {
            Environment parent = environment.getParent(env -> env instanceof ClassEnvironment);
            if (!(parent instanceof ClassEnvironment classEnvironment)) throw new RuntimeException("Can't use 'this' keyword not inside a class");
            if (environment.isShared()) throw new RuntimeException("Can't use 'this' keyword inside a shared environment");
            return new NativeClassValue(
                    classEnvironment.getBaseClasses().stream().map(ClassEnvironment::getId).collect(Collectors.toSet()),
                    classEnvironment);
        });
    }



    private static RuntimeValue<?> evaluateAssignmentExpression(AssignmentExpression assignmentExpression, Environment environment) {
        if (assignmentExpression.getId() instanceof VariableIdentifier variableIdentifier) {
            RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment).getFinalRuntimeValue();
            environment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
            return value;
        }
        if (assignmentExpression.getId() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = Interpreter.evaluate(memberExpression, environment);
            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment).getFinalRuntimeValue();
                variableValue.setValue(value);
                return value;
            }
            throw new InvalidSyntaxException("Can't assign value to not variable " + memberExpressionValue);
        }
        throw new InvalidSyntaxException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    private static boolean parseCondition(Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = Interpreter.evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, boolean isDefault) {
        String defaultString = isDefault ? ". It's probably an Addon's error" : "";

        if (returnValue == null) {
            if (returnDataType != null) {
                throw new InvalidSyntaxException("Didn't find return value but function with id " + functionId + " must return value" + defaultString);
            }
            return null;
        }
        if (returnDataType == null) {
            throw new InvalidSyntaxException("Found return value but function with id " + functionId + " must return nothing" + defaultString);
        }

        if (!returnDataType.isMatches(returnValue)) {
            throw new InvalidSyntaxException("Returned value's data type is different from specified (" + returnDataType.getId() + ")" + defaultString);
        }

        return returnValue;
    }

    private static boolean hasRepeatedBaseClasses(Set<String> baseClassesList, List<String> baseClasses) {
        for (String baseClass : baseClassesList) {
            if (baseClasses.contains(baseClass)) {
                return true;
            }
            baseClasses.add(baseClass);

            ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException("Class with id " + baseClass + " doesn't exist");
            }
            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't inherit final class with id " + baseClass);

            boolean check = hasRepeatedBaseClasses(classValue.getBaseClasses(), baseClasses);
            if (check) return true;
        }

        return false;
    }

    private static boolean hasRepeatedVariables(Set<String> baseClassesList, List<String> variables) {
        for (String baseClass : baseClassesList) {
            ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException("Class with id " + baseClass + " doesn't exist");
            }

            for (VariableValue variableValue : classValue.getEnvironment().getVariables()) {
                if (variableValue.getModifiers().contains(AddonModifiers.PRIVATE())) continue;
                if (variables.contains(variableValue.getId())) {
                    return true;
                }
                variables.add(variableValue.getId());
            }

            boolean check = hasRepeatedVariables(classValue.getBaseClasses(), variables);
            if (check) return true;
        }

        return false;
    }

    private static boolean hasRepeatedFunctions(Set<ClassEnvironment> baseClassesList, List<FunctionValue> functions) {
        for (ClassEnvironment classEnvironment : baseClassesList) {
            for (FunctionValue functionValue : getFinalFunctions(classEnvironment)) {
                if (functionValue.getModifiers().contains(AddonModifiers.PRIVATE())) continue;
                if (functionValue.getModifiers().contains(AddonModifiers.ABSTRACT())) {
                    if (functions.stream().anyMatch(function -> function.isLike(functionValue) && !function.getModifiers().contains(AddonModifiers.ABSTRACT()))) {
                        return true;
                    }
                }
                else if (functions.stream().anyMatch(function -> function.isLike(functionValue))) {
                    return true;
                }
                functions.add(functionValue);
            }

            boolean check = hasRepeatedFunctions(classEnvironment.getBaseClasses(), functions);
            if (check) return true;
        }

        return false;
    }

    private static List<FunctionValue> getFinalFunctions(ClassEnvironment classEnvironment) {
        List<FunctionValue> functionValues = new ArrayList<>();
        for (FunctionValue functionValue : classEnvironment.getFunctions()) {
            if (!functionValue.isOverridden()) functionValues.add(functionValue);
        }

        return functionValues;
    }

    private static ClassEnvironment initClassEnvironment(ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment;
        try {
            classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue()
                    .getConstructor(ClassDeclarationEnvironment.class, String.class, Set.class)
                    .newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), classValue.getId(), classValue.getModifiers());
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        ConstructorEnvironment constructorEnvironment;
        try {
            constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT.getEntry().getValue().getConstructor(ConstructorDeclarationEnvironment.class).newInstance(classEnvironment);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Set<String> calledBaseClasses = new HashSet<>();
        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            for (Statement statement : runtimeClassValue.getBody()) {
                Interpreter.evaluate(statement, classEnvironment);
            }
            for (FunctionDeclarationStatement functionDeclarationStatement : extensionFunctions) {
                if (functionDeclarationStatement.getClassId().equals(runtimeClassValue.getId())) {
                    Interpreter.evaluate(new FunctionDeclarationStatement(
                            functionDeclarationStatement.getModifiers(),
                            functionDeclarationStatement.getId(),
                            null,
                            functionDeclarationStatement.getArgs(),
                            functionDeclarationStatement.getBody(),
                            functionDeclarationStatement.getReturnDataType()
                    ), classEnvironment);
                }
            }

            if (classEnvironment.hasConstructor()) {
                RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                if (rawConstructor == null) throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");

                if (rawConstructor instanceof RuntimeConstructorValue runtimeConstructorValue) {
                    if (runtimeConstructorValue.getModifiers().contains(AddonModifiers.PRIVATE()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            return classEnv.getId().equals(classValue.getId());
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has private access");
                    }

                    if (runtimeConstructorValue.getModifiers().contains(AddonModifiers.PROTECTED()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            if (classEnv.getId().equals(classValue.getId())) return true;

                            ClassValue parentClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(classEnv.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnv.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(classValue.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has protected access");
                    }

                    for (int i = 0; i < runtimeConstructorValue.getArgs().size(); i++) {
                        CallArgExpression callArgExpression = runtimeConstructorValue.getArgs().get(i);

                        constructorEnvironment.declareVariable(new VariableValue(
                                callArgExpression.getId(),
                                callArgExpression.getDataType(),
                                args.get(i),
                                callArgExpression.isConstant(),
                                new HashSet<>(),
                                true));
                    }

                    for (Statement statement : runtimeConstructorValue.getBody()) {
                        if (statement instanceof BaseCallStatement) {
                            RuntimeValue<?> value = Interpreter.evaluate(statement, constructorEnvironment, classEnvironment);
                            if (!(value instanceof BaseClassIdValue baseClassIdValue)) throw new RuntimeException("Unknown error occurred");
                            if (!classValue.getBaseClasses().contains(baseClassIdValue.getValue()))
                                throw new InvalidSyntaxException("Can't call base class " + baseClassIdValue.getValue() + " because it's not base class of class " + classValue.getId());
                            calledBaseClasses.add(baseClassIdValue.getValue());
                        }
                        else Interpreter.evaluate(statement, constructorEnvironment);
                    }
                }
            }
            else if (!args.isEmpty()) {
                throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");
            }
        }
        else if (classValue instanceof NativeClassValue nativeClassValue) {
            nativeClassValue.getEnvironment().getVariables().forEach(variable -> classEnvironment.declareVariable(new VariableValue(
                    variable.getId(),
                    variable.getDataType(),
                    variable.getValue(),
                    variable.isConstant(),
                    variable.getModifiers(),
                    variable.isArgument())));
            nativeClassValue.getEnvironment().getFunctions().forEach(function -> classEnvironment.declareFunction(function.copy(classEnvironment)));
            nativeClassValue.getEnvironment().getConstructors().forEach(constructor -> classEnvironment.declareConstructor(constructor.copy(classEnvironment)));

            if (classEnvironment.hasConstructor()) {
                RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                if (rawConstructor == null) throw new InvalidCallException("Class with id " + nativeClassValue.getId() + " doesn't have requested constructor");

                if (rawConstructor instanceof NativeConstructorValue defaultConstructorValue) {
                    if (defaultConstructorValue.getModifiers().contains(AddonModifiers.PRIVATE()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            return classEnv.getId().equals(nativeClassValue.getId());
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has private access");
                    }

                    if (defaultConstructorValue.getModifiers().contains(AddonModifiers.PROTECTED()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            if (classEnv.getId().equals(classValue.getId())) return true;

                            ClassValue parentClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(classEnv.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnv.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(classValue.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has protected access");
                    }

                    defaultConstructorValue.run(args, constructorEnvironment);
                }
            }
            else if (!args.isEmpty()) throw new InvalidCallException("Class with id " + nativeClassValue.getId() + " doesn't have requested constructor");
        }
        else throw new RuntimeException("Can't init ClassEnvironment of class value " + classValue.getClass().getName());

        for (VariableValue variableValue : classEnvironment.getVariables()) {
            if (variableValue.isConstant() && variableValue.getValue() == null) {
                throw new InvalidSyntaxException("Empty constant variable with id " + variableValue.getId() + " hasn't been initialized");
            }
        }

        for (String baseClass : classValue.getBaseClasses()) {
            if (calledBaseClasses.contains(baseClass)) continue;
            ClassValue baseClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(baseClass);
            classEnvironment.addBaseClass(initClassEnvironment(baseClassValue, constructorEnvironment, new ArrayList<>()));
        }

        for (FunctionValue value : classEnvironment.getFunctions()) {
            for (ClassEnvironment baseClass : classEnvironment.getDeepBaseClasses()) {
                for (FunctionValue baseClassFunction : baseClass.getFunctions()) {
                    if (baseClassFunction.isLike(value) && !baseClassFunction.getModifiers().contains(AddonModifiers.PRIVATE())) {
                        if (baseClassFunction.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't override final function with id " + baseClassFunction.getId());
                        baseClassFunction.setOverridden();
                    }
                }
            }
        }

        if (hasRepeatedFunctions(classEnvironment.getBaseClasses(), new ArrayList<>(classEnvironment.getFunctions()))) {
            throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " has repeated functions");
        }

        if (!classEnvironment.getModifiers().contains(AddonModifiers.ABSTRACT())) {
            for (ClassEnvironment baseClass : classEnvironment.getBaseClasses()) {
                for (FunctionValue functionValue : getFinalFunctions(baseClass)) {
                    if (functionValue.getModifiers().contains(AddonModifiers.ABSTRACT())) {
                        throw new InvalidSyntaxException("Abstract function with id " + functionValue.getId() + " in class with id " + classEnvironment.getId() + " hasn't been initialized");
                    }
                }
            }
        }

        return classEnvironment;
    }

    private static RuntimeValue<?> callFunction(FunctionValue functionValue, List<RuntimeValue<?>> args) {
        if (functionValue instanceof NativeFunctionValue defaultFunctionValue) {
            if (defaultFunctionValue.getArgs().size() != args.size()) {
                throw new InvalidCallException("Expected " + defaultFunctionValue.getArgs().size() + " args but found " + args.size());
            }

            FunctionEnvironment functionEnvironment;
            try {
                functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(FunctionDeclarationEnvironment.class, boolean.class)
                        .newInstance(defaultFunctionValue.getParentEnvironment(), defaultFunctionValue.getModifiers().contains(AddonModifiers.SHARED()));
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            RuntimeValue<?> returnValue = defaultFunctionValue.run(args, functionEnvironment);
            if (returnValue != null) returnValue = returnValue.getFinalRuntimeValue();
            return checkReturnValue(
                    returnValue,
                    defaultFunctionValue.getReturnDataType(),
                    defaultFunctionValue.getId(),
                    true);
        }
        if (functionValue instanceof RuntimeFunctionValue runtimeFunctionValue) {
            if (runtimeFunctionValue.getArgs().size() != args.size()) {
                throw new InvalidCallException("Expected " + runtimeFunctionValue.getArgs().size() + " args but found " + args.size());
            }

            FunctionEnvironment functionEnvironment;
            try {
                functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(FunctionDeclarationEnvironment.class, boolean.class)
                        .newInstance(runtimeFunctionValue.getParentEnvironment(), runtimeFunctionValue.getModifiers().contains(AddonModifiers.SHARED()));
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < runtimeFunctionValue.getArgs().size(); i++) {
                CallArgExpression callArgExpression = runtimeFunctionValue.getArgs().get(i);

                functionEnvironment.declareVariable(new VariableValue(
                        callArgExpression.getId(),
                        callArgExpression.getDataType(),
                        args.get(i),
                        callArgExpression.isConstant(),
                        new HashSet<>(),
                        true));
            }

            RuntimeValue<?> result = null;
            boolean hasReturnStatement = false;
            for (int i = 0; i < runtimeFunctionValue.getBody().size(); i++) {
                Statement statement = runtimeFunctionValue.getBody().get(i);
                if (statement instanceof ReturnStatement) {
                    hasReturnStatement = true;
                    result = Interpreter.evaluate(statement, functionEnvironment);
                    if (result != null) {
                        checkReturnValue(
                                result.getFinalRuntimeValue(),
                                runtimeFunctionValue.getReturnDataType(),
                                runtimeFunctionValue.getId(),
                                false);
                    }
                    if (i + 1 < runtimeFunctionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                    break;
                }
                RuntimeValue<?> value = Interpreter.evaluate(statement, functionEnvironment);
                if (value instanceof ReturnInfoValue returnInfoValue) {
                    hasReturnStatement = true;
                    result = returnInfoValue.getFinalRuntimeValue();
                    if (result.getFinalValue() != null) {
                        checkReturnValue(
                                result.getFinalRuntimeValue(),
                                runtimeFunctionValue.getReturnDataType(),
                                runtimeFunctionValue.getId(),
                                false);
                    }
                    break;
                }
            }
            if ((result == null || result instanceof NullValue) && runtimeFunctionValue.getReturnDataType() != null) {
                throw new InvalidSyntaxException(hasReturnStatement ?
                        "Function specified return value's data type but return statement is empty" : "Missing return statement");
            }
            return result;
        }

        throw new InvalidCallException("Can't call " + functionValue.getValue() + " because it's not a function");
    }



    private static <T extends Statement> void register(String id, Class<T> cls, EvaluationFunction<T> evaluationFunction) {
        Registries.EVALUATION_FUNCTIONS.register(AddonMain.getIdentifier(id), cls, evaluationFunction);
    }
}