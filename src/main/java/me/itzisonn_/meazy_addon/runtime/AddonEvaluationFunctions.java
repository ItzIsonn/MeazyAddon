package me.itzisonn_.meazy_addon.runtime;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.Expression;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.InvalidFileException;
import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.value.classes.NativeClassValue;
import me.itzisonn_.meazy.runtime.value.constructor.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.constructor.NativeConstructorValue;
import me.itzisonn_.meazy.runtime.value.constructor.RuntimeConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;
import me.itzisonn_.meazy_addon.AddonMain;
import me.itzisonn_.meazy_addon.parser.ast.expression.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.CallExpression;
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
import me.itzisonn_.meazy_addon.runtime.environment.GlobalEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.VariableValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.NativeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.classes.RuntimeClassValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.constructor.RuntimeConstructorValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.NativeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.impl.function.RuntimeFunctionValueImpl;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.InnerCollectionValue;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.ListClassNative;
import me.itzisonn_.meazy_addon.runtime.value.native_class.collection.MapClassNative;
import me.itzisonn_.meazy.runtime.value.*;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.classes.RuntimeClassValue;
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
import org.apache.logging.log4j.Level;

import java.io.File;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        if (isInit) throw new IllegalStateException("EvaluationFunctions have already been initialized");
        isInit = true;

        register("program", Program.class, (program, environment, extra) -> {
            for (Statement statement : program.getBody()) {
                Interpreter.evaluate(statement, environment);
            }

            return null;
        });

        register("import_statement", ImportStatement.class, (importStatement, environment, extra) -> {
            if (!(environment instanceof GlobalEnvironment globalEnvironment)) {
                throw new InvalidSyntaxException("Can't use imports in non-global environment");
            }

            File file = new File(importStatement.getFile());
            if (file.isDirectory() || !file.exists()) {
                throw new InvalidFileException("File '" + file.getAbsolutePath() + "' doesn't exist");
            }

            String extension = FileUtils.getExtension(file);
            Program program;
            switch (extension) {
                case "mea" -> {
                    Parser.reset();
                    List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(FileUtils.getLines(file));
                    program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(file, tokens);
                }
                case "meac" -> {
                    program = Registries.getGson().fromJson(FileUtils.getLines(file), Program.class);
                    if (program == null) throw new InvalidFileException("Failed to read file '" + file.getAbsolutePath() + "'");
                    if (MeazyMain.VERSION.isBefore(program.getVersion())) throw new InvalidFileException("Can't run file that has been compiled by a more recent version of the Meazy (" + program.getVersion() + "), in a more older version (" + MeazyMain.VERSION + ")");
                    if (MeazyMain.VERSION.isAfter(program.getVersion())) {
                        MeazyMain.LOGGER.log(Level.WARN, "It's unsafe to run file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), MeazyMain.VERSION);
                    }
                    program.setFile(file);
                }
                default -> throw new InvalidFileException("Can't run file with extension " + extension);
            }

            globalEnvironment.addRelatedGlobalEnvironment(Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().apply(program));
            return null;
        });

        register("using_statement", UsingStatement.class, (usingStatement, environment, objects) -> {
            if (!(environment instanceof GlobalEnvironment globalEnvironment)) {
                throw new InvalidSyntaxException("Can't use using statement in non-global environment");
            }

            Class<?> nativeClass;
            try {
                nativeClass = Class.forName(usingStatement.getClassName());
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Can't find native class", e);
            }
            if (!nativeClass.isAnnotationPresent(MeazyNativeClass.class)) {
                throw new InvalidSyntaxException("Can't use non-native class " + nativeClass.getName());
            }

            MeazyNativeClass nativeAnnotation = nativeClass.getAnnotation(MeazyNativeClass.class);
            if (nativeAnnotation == null) throw new InvalidSyntaxException("Can't use non-native class " + nativeClass.getName());

            ArrayList<File> accessibleFiles = new ArrayList<>();
            for (String file : nativeAnnotation.value()) {
                accessibleFiles.add(new File(file));
            }
            if (!accessibleFiles.isEmpty() && globalEnvironment.getParentFile() != null && !accessibleFiles.contains(globalEnvironment.getParentFile())) {
                throw new RuntimeException("Can't access native class " + nativeClass.getName());
            }

            globalEnvironment.addNativeClass(nativeClass);

            return null;
        });

        register("class_declaration_statement", ClassDeclarationStatement.class, (classDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare class in this environment");
            }

            for (Modifier modifier : classDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(classDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                    environment.getGlobalEnvironment(),
                    true,
                    classDeclarationStatement.getId(),
                    classDeclarationStatement.getModifiers());

            for (Statement statement : classDeclarationStatement.getBody()) {
                Interpreter.evaluate(statement, classEnvironment);
            }

            for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
                classEnvironment.declareVariable(new VariableValueImpl(
                        enumId,
                        new DataType(classDeclarationStatement.getId(), false),
                        null,
                        true,
                        Set.of(AddonModifiers.SHARED()),
                        false,
                        classEnvironment
                ));
            }

            RuntimeClassValue runtimeClassValue = null;
            if (classDeclarationStatement.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : classEnvironment.getGlobalEnvironment().getNativeClasses()) {
                    Method method;
                    try {
                        method = nativeClass.getDeclaredMethod("newInstance", Set.class, ClassEnvironment.class, List.class);
                    }
                    catch (NoSuchMethodException e) {
                        continue;
                    }

                    if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                        throw new InvalidSyntaxException("Can't call non-static native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!method.canAccess(null)) {
                        throw new InvalidSyntaxException("Can't call non-accessible native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!RuntimeClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classDeclarationStatement.getBaseClasses(), classEnvironment, classDeclarationStatement.getBody());
                        runtimeClassValue = (RuntimeClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }

                if (runtimeClassValue == null) {
                    throw new InvalidSyntaxException("Can't find native method to create new instance of class with id " + classEnvironment.getId());
                }
            }
            else runtimeClassValue = new RuntimeClassValueImpl(
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
                enumEnvironment.declareFunction(new NativeFunctionValueImpl("getOrdinal", List.of(), new DataType("Int", false), enumEnvironment, Set.of()) {
                    @Override
                    public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment) {
                        return new IntValue(finalEnumOrdinal);
                    }
                });
                enumOrdinal++;

                ClassValue enumValue = new NativeClassValueImpl(enumEnvironment);
                classEnvironment.assignVariable(enumId, enumValue);
                enumValues.add(enumValue);
            }

            if (classDeclarationStatement.getModifiers().contains(AddonModifiers.ENUM())) {
                classEnvironment.declareFunction(new NativeFunctionValueImpl("getValues", List.of(), new DataType("List", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
                    @Override
                    public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment) {
                        return ListClassNative.newList(functionEnvironment, new ArrayList<>(enumValues));
                    }
                });
            }

            return null;
        });

        register("function_declaration_statement", FunctionDeclarationStatement.class, (functionDeclarationStatement, environment, extra) -> {
            if (functionDeclarationStatement.getClassId() != null) {
                ClassValue classValue = environment.getGlobalEnvironment().getClass(functionDeclarationStatement.getClassId());
                if (classValue == null) throw new InvalidIdentifierException("Can't find class with id " + functionDeclarationStatement.getClassId());
                if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidIdentifierException("Can't extend final class with id " + functionDeclarationStatement.getClassId());
                if (!extensionFunctions.contains(functionDeclarationStatement)) extensionFunctions.add(functionDeclarationStatement);
                return null;
            }
            if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare function in this environment");
            }

            for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(functionDeclarationStatement, environment)) throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            RuntimeFunctionValue runtimeFunctionValue = new RuntimeFunctionValueImpl(
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
                if (!modifier.canUse(variableDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            Set<Modifier> modifiers = new HashSet<>(variableDeclarationStatement.getModifiers());
            if (!(environment instanceof ClassEnvironment) && environment.isShared() &&
                    !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED())) modifiers.add(AddonModifiers.SHARED());

            variableDeclarationStatement.getDeclarationInfos().forEach(variableDeclarationInfo -> {
                RuntimeValue<?> value = null;
                if (variableDeclarationInfo.getValue() != null && !(environment instanceof ClassEnvironment && environment.isShared() &&
                        !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED()))) {
                    if ((variableDeclarationInfo.getValue() instanceof CallExpression || variableDeclarationInfo.getValue() instanceof MemberExpression) &&
                            (environment instanceof GlobalEnvironment || environment instanceof ClassEnvironment)) {
                        if (environment.getGlobalEnvironment() instanceof GlobalEnvironmentImpl globalEnvironment) {
                            globalEnvironment.getVariableQueue().put(variableDeclarationInfo, environment);
                        }
                        else throw new RuntimeException("Can't place variable in queue");
                    }
                    else value = Interpreter.evaluate(variableDeclarationInfo.getValue(), environment);
                }

                VariableValue variableValue = new VariableValueImpl(
                        variableDeclarationInfo.getId(),
                        variableDeclarationInfo.getDataType(),
                        value,
                        variableDeclarationStatement.isConstant(),
                        modifiers,
                        false,
                        environment
                );
                environment.declareVariable(variableValue);
            });

            return null;
        });

        register("constructor_declaration_statement", ConstructorDeclarationStatement.class, (constructorDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare constructor in this environment");
            }

            for (Modifier modifier : constructorDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(constructorDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            RuntimeConstructorValue runtimeConstructorValue = new RuntimeConstructorValueImpl(
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
                throw new InvalidSyntaxException("Can't use BaseCallStatement in this environment");
            }

            ClassValue baseClassValue = environment.getGlobalEnvironment().getClass(baseCallStatement.getId());
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

                Environment ifEnvironment = Registries.ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);

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
            LoopEnvironment forEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);

            forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                    forEnvironment.declareVariable(new VariableValueImpl(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            variableDeclarationInfo.getValue() == null ?
                                    null :
                                    Interpreter.evaluate(variableDeclarationInfo.getValue(), environment),
                            forStatement.getVariableDeclarationStatement().isConstant(),
                            Set.of(),
                            false,
                            forEnvironment
                    ))
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
                    forEnvironment.declareVariable(new VariableValueImpl(
                            variableValue.getId(),
                            variableValue.getDataType(),
                            variableValue.getValue(),
                            variableValue.isConstant(),
                            new HashSet<>(),
                            false,
                            forEnvironment
                    ));
                }
                evaluateAssignmentExpression(forStatement.getAssignmentExpression(), forEnvironment);
            }

            return null;
        });

        register("foreach_statement", ForeachStatement.class, (foreachStatement, environment, extra) -> {
            LoopEnvironment foreachEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);

            RuntimeValue<?> rawCollectionValue = Interpreter.evaluate(foreachStatement.getCollection(), foreachEnvironment).getFinalRuntimeValue();
            if (!(rawCollectionValue instanceof ClassValue classValue && classValue.getBaseClasses().contains("Collection")))
                throw new InvalidSyntaxException("Can't get members of non-collection value");

            VariableValue variable = classValue.getEnvironment().getVariable("collection");
            if (variable == null) throw new InvalidSyntaxException("Can't get members of non-collection value");
            if (!(variable.getValue() instanceof InnerCollectionValue<?> collectionValue)) throw new InvalidSyntaxException("Can't get members of non-collection value");

            main:
            for (RuntimeValue<?> runtimeValue : collectionValue.getValue()) {
                foreachEnvironment.clearVariables();

                foreachEnvironment.declareVariable(new VariableValueImpl(
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getId(),
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getDataType(),
                        runtimeValue,
                        foreachStatement.getVariableDeclarationStatement().isConstant(),
                        new HashSet<>(),
                        false,
                        foreachEnvironment
                ));

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
            LoopEnvironment whileEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);

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
            return ListClassNative.newList(environment, list);
        });

        register("map_creation_expression", MapCreationExpression.class, (mapCreationExpression, environment, extra) -> {
            Map<RuntimeValue<?>, RuntimeValue<?>> map = new HashMap<>();

            for (Expression key : mapCreationExpression.getMap().keySet()) {
                Expression value = mapCreationExpression.getMap().get(key);
                map.put(Interpreter.evaluate(key, environment), Interpreter.evaluate(value, environment));
            }

            return MapClassNative.newMap(environment, map);
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

            ClassValue classValue = environment.getGlobalEnvironment().getClass(isExpression.getDataType());
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

            return callClassValue(classValue, extraEnvironment, args);
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
            RuntimeValue<?> function = Interpreter.evaluate(functionCallExpression.getCaller(), environment, extraEnvironment, args);
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

                                    ClassValue parentClassValue = parentEnv.getGlobalEnvironment().getClass(classEnvironment.getId());
                                    if (parentClassValue == null) {
                                        throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " doesn't exist");
                                    }
                                    return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                                }
                                return false;
                    })) {
                        throw new InvalidAccessException("Can't access protected variable with id " + identifier.getId());
                    }

                    if (!variableValue.getModifiers().contains(AddonModifiers.OPEN()) && variableDeclarationEnvironment.getParentFile() != null &&
                            !variableDeclarationEnvironment.getParentFile().equals(requestEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open variable with id " + identifier.getId() + " from different file (" + requestEnvironment.getParentFile().getName() + ")");
                    }

                    if (!variableValue.getModifiers().contains(AddonModifiers.SHARED()) && environment.isShared() && !variableValue.isArgument() &&
                            !(variableDeclarationEnvironment instanceof GlobalEnvironment))
                        throw new InvalidAccessException("Can't access non-shared variable with id " + identifier.getId() + " from shared environment");

                    return variableValue;
                }

                if (identifier instanceof FunctionIdentifier) {
                    if (extra.length == 1 || !(extra[1] instanceof List<?> rawArgs)) throw new RuntimeException("Invalid function args");

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

                            ClassValue parentClassValue = parentEnv.getGlobalEnvironment().getClass(classEnvironment.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnvironment.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidAccessException("Can't access protected function with id " + identifier.getId());
                    }

                    if (!functionValue.getModifiers().contains(AddonModifiers.OPEN()) && functionDeclarationEnvironment.getParentFile() != null &&
                            !functionDeclarationEnvironment.getParentFile().equals(requestEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open function with id " + identifier.getId() + " from different file (" + requestEnvironment.getParentFile().getName() + ")");
                    }

                    if (!functionValue.getModifiers().contains(AddonModifiers.SHARED()) && environment.isShared() &&
                            !(functionDeclarationEnvironment instanceof GlobalEnvironment))
                        throw new InvalidAccessException("Can't access non-shared function with id " + identifier.getId() + " from shared environment");

                    return functionValue;
                }

                if (identifier instanceof ClassIdentifier) {
                    ClassValue classValue = environment.getGlobalEnvironment().getClass(identifier.getId());
                    if (classValue == null) return evaluate(new VariableIdentifier(identifier.getId()), environment, extra);

                    if (!classValue.getModifiers().contains(AddonModifiers.OPEN()) &&  classValue.getEnvironment().getParentFile() != null &&
                            !classValue.getEnvironment().getParentFile().equals(requestEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open class with id " + identifier.getId() + " from different file (" + requestEnvironment.getParentFile().getName() + ")");
                    }

                    return classValue;
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
            return new NativeClassValueImpl(
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

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, boolean isDefault, GlobalEnvironment globalEnvironment) {
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

        if (!returnDataType.isMatches(returnValue, globalEnvironment)) {
            throw new InvalidSyntaxException("Returned value's data type is different from specified (" + returnDataType.getId() + ")" + defaultString);
        }

        return returnValue;
    }

    public static boolean hasRepeatedBaseClasses(Set<String> baseClassesList, List<String> baseClasses, GlobalEnvironment globalEnvironment) {
        for (String baseClass : baseClassesList) {
            if (baseClasses.contains(baseClass)) {
                return true;
            }
            baseClasses.add(baseClass);

            ClassValue classValue = globalEnvironment.getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException("Class with id " + baseClass + " doesn't exist");
            }
            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't inherit final class with id " + baseClass);

            boolean check = hasRepeatedBaseClasses(classValue.getBaseClasses(), baseClasses, globalEnvironment);
            if (check) return true;
        }

        return false;
    }

    public static boolean hasRepeatedVariables(Set<String> baseClassesList, List<String> variables, GlobalEnvironment globalEnvironment) {
        for (String baseClass : baseClassesList) {
            ClassValue classValue = globalEnvironment.getClass(baseClass);
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

            boolean check = hasRepeatedVariables(classValue.getBaseClasses(), variables, globalEnvironment);
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

    public static ClassValue callClassValue(ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = initClassEnvironment(classValue, callEnvironment, args);

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            if (runtimeClassValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : classEnvironment.getGlobalEnvironment().getNativeClasses()) {
                    Method method;
                    try {
                        method = nativeClass.getDeclaredMethod("newInstance", Set.class, ClassEnvironment.class, List.class);
                    }
                    catch (NoSuchMethodException e) {
                        continue;
                    }

                    if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                        throw new InvalidSyntaxException("Can't call non-static native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!method.canAccess(null)) {
                        throw new InvalidSyntaxException("Can't call non-accessible native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!RuntimeClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
                        return (RuntimeClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }

                throw new InvalidSyntaxException("Can't find native method to create new instance of class with id " + classEnvironment.getId());
            }
            else return new RuntimeClassValueImpl(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
        }
        if (classValue instanceof NativeClassValue nativeClassValue) return nativeClassValue.newInstance(nativeClassValue.getBaseClasses(), classEnvironment);

        throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
    }

    public static ClassEnvironment initClassEnvironment(ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getGlobalEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);

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
                ConstructorValue rawConstructor = classEnvironment.getConstructor(args);
                if (rawConstructor == null) throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");

                if (rawConstructor instanceof RuntimeConstructorValue runtimeConstructorValue) {
                    if (runtimeConstructorValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                        ArrayList<Class<?>> params = new ArrayList<>(Collections.nCopies(runtimeConstructorValue.getArgs().size(), RuntimeValue.class));
                        params.add(ConstructorEnvironment.class);
                        Class<?>[] array = params.toArray(Class[]::new);

                        boolean hasFound = false;
                        for (Class<?> nativeClass : constructorEnvironment.getGlobalEnvironment().getNativeClasses()) {
                            Method method;
                            try {
                                method = nativeClass.getDeclaredMethod("constructor", array);
                            }
                            catch (NoSuchMethodException e) {
                                continue;
                            }

                            if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                                throw new InvalidSyntaxException("Can't call non-static native constructor");
                            }
                            if (!method.canAccess(null)) {
                                throw new InvalidSyntaxException("Can't call non-accessible native constructor");
                            }
                            if (!method.getReturnType().equals(Void.TYPE)) {
                                throw new RuntimeException("Return value of native constructor with id " + method.getName() + " is invalid");
                            }

                            try {
                                ArrayList<Object> constructorArgs = new ArrayList<>(args);
                                constructorArgs.add(constructorEnvironment);
                                method.invoke(null, constructorArgs.toArray());
                                hasFound = true;
                            }
                            catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("Failed to call native constructor", e);
                            }
                        }

                        if (!hasFound) throw new InvalidSyntaxException("Can't find native constructor");
                    }

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

                            ClassValue parentClassValue = env.getGlobalEnvironment().getClass(classEnv.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnv.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(classValue.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has protected access");
                    }

                    if (!runtimeConstructorValue.getModifiers().contains(AddonModifiers.OPEN()) && classEnvironment.getParentFile() != null &&
                            !classEnvironment.getParentFile().equals(callEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open constructor from different file (" + callEnvironment.getParentFile().getName() + ")");
                    }

                    for (int i = 0; i < runtimeConstructorValue.getArgs().size(); i++) {
                        CallArgExpression callArgExpression = runtimeConstructorValue.getArgs().get(i);

                        constructorEnvironment.declareVariable(new VariableValueImpl(
                                callArgExpression.getId(),
                                callArgExpression.getDataType(),
                                args.get(i),
                                callArgExpression.isConstant(),
                                new HashSet<>(),
                                true,
                                constructorEnvironment
                        ));
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
            nativeClassValue.setupEnvironment(classEnvironment);

            if (classEnvironment.hasConstructor()) {
                RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                if (rawConstructor == null) throw new InvalidCallException("Class with id " + nativeClassValue.getId() + " doesn't have requested constructor");

                if (rawConstructor instanceof NativeConstructorValue nativeConstructorValue) {
                    if (nativeConstructorValue.getModifiers().contains(AddonModifiers.PRIVATE()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            return classEnv.getId().equals(nativeClassValue.getId());
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has private access");
                    }

                    if (nativeConstructorValue.getModifiers().contains(AddonModifiers.PROTECTED()) && !callEnvironment.hasParent(env -> {
                        if (env instanceof ClassEnvironment classEnv) {
                            if (classEnv.getId().equals(classValue.getId())) return true;

                            ClassValue parentClassValue = env.getGlobalEnvironment().getClass(classEnv.getId());
                            if (parentClassValue == null) {
                                throw new InvalidIdentifierException("Class with id " + classEnv.getId() + " doesn't exist");
                            }
                            return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(classValue.getId()));
                        }
                        return false;
                    })) {
                        throw new InvalidCallException("Requested constructor has protected access");
                    }

                    if (!nativeConstructorValue.getModifiers().contains(AddonModifiers.OPEN()) &&
                            !classEnvironment.getParentFile().equals(callEnvironment.getParentFile())) {
                        throw new InvalidAccessException("Can't access non-open constructor from different file (" + callEnvironment.getParentFile().getName() + ")");
                    }

                    nativeConstructorValue.run(args, constructorEnvironment);
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
            ClassValue baseClassValue = classValue.getEnvironment().getGlobalEnvironment().getClass(baseClass);
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

    public static ClassValue callEmptyClassValue(ClassValue classValue) {
        ClassEnvironment classEnvironment = initEmptyClassEnvironment(classValue);

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            if (runtimeClassValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : classEnvironment.getGlobalEnvironment().getNativeClasses()) {
                    Method method;
                    try {
                        method = nativeClass.getDeclaredMethod("newInstance", Set.class, ClassEnvironment.class, List.class);
                    }
                    catch (NoSuchMethodException e) {
                        continue;
                    }

                    if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                        throw new InvalidSyntaxException("Can't call non-static native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!method.canAccess(null)) {
                        throw new InvalidSyntaxException("Can't call non-accessible native method to create new instance of class with id " + classEnvironment.getId());
                    }
                    if (!RuntimeClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
                        return (RuntimeClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }

                throw new InvalidSyntaxException("Can't find native method to create new instance of class with id " + classEnvironment.getId());
            }
            else return new RuntimeClassValueImpl(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
        }
        if (classValue instanceof NativeClassValue nativeClassValue) return nativeClassValue.newInstance(nativeClassValue.getBaseClasses(), classEnvironment);

        throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
    }

    public static ClassEnvironment initEmptyClassEnvironment(ClassValue classValue) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getGlobalEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);

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
        }
        else if (classValue instanceof NativeClassValue nativeClassValue) {
            nativeClassValue.setupEnvironment(classEnvironment);
        }
        else throw new RuntimeException("Can't init ClassEnvironment of class value " + classValue.getClass().getName());

        for (String baseClass : classValue.getBaseClasses()) {
            ClassValue baseClassValue = classValue.getEnvironment().getGlobalEnvironment().getClass(baseClass);
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
        if (functionValue.getArgs().size() != args.size()) {
            throw new InvalidCallException("Expected " + functionValue.getArgs().size() + " args but found " + args.size());
        }

        FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                functionValue.getParentEnvironment(),
                functionValue.getModifiers().contains(AddonModifiers.SHARED()));

        if (functionValue instanceof NativeFunctionValue nativeFunctionValue) {
            RuntimeValue<?> returnValue = nativeFunctionValue.run(args, functionEnvironment);
            if (returnValue != null) returnValue = returnValue.getFinalRuntimeValue();
            return checkReturnValue(
                    returnValue,
                    nativeFunctionValue.getReturnDataType(),
                    nativeFunctionValue.getId(),
                    true,
                    functionEnvironment.getGlobalEnvironment());
        }
        if (functionValue instanceof RuntimeFunctionValue runtimeFunctionValue) {
            if (runtimeFunctionValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                ArrayList<Class<?>> params = new ArrayList<>(Collections.nCopies(functionValue.getArgs().size(), RuntimeValue.class));
                params.add(FunctionEnvironment.class);
                Class<?>[] array = params.toArray(Class[]::new);

                for (Class<?> nativeClass : functionEnvironment.getGlobalEnvironment().getNativeClasses()) {
                    Method method;
                    try {
                        method = nativeClass.getDeclaredMethod(functionValue.getId(), array);
                    }
                    catch (NoSuchMethodException e) {
                        continue;
                    }

                    if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                        throw new InvalidSyntaxException("Can't call non-static native method with id " + method.getName());
                    }
                    if (!method.canAccess(null)) {
                        throw new InvalidSyntaxException("Can't call non-accessible native method with id " + method.getName());
                    }
                    if (!method.getReturnType().equals(Void.TYPE) && !RuntimeValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        ArrayList<Object> methodArgs = new ArrayList<>(args);
                        methodArgs.add(functionEnvironment);
                        Object object = method.invoke(null, methodArgs.toArray());

                        if (method.getReturnType().equals(Void.TYPE)) {
                            if (functionValue.getReturnDataType() != null) throw new RuntimeException("Can't get return value for native method with id " + method.getName());
                            return null;
                        }
                        else {
                            return checkReturnValue(
                                    ((RuntimeValue<?>) object).getFinalRuntimeValue(),
                                    functionValue.getReturnDataType(),
                                    functionValue.getId(),
                                    true,
                                    functionEnvironment.getGlobalEnvironment());
                        }
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method with id " + method.getName(), e);
                    }
                }

                throw new InvalidSyntaxException("Can't find native method with id " + functionValue.getId());
            }

            for (int i = 0; i < runtimeFunctionValue.getArgs().size(); i++) {
                CallArgExpression callArgExpression = runtimeFunctionValue.getArgs().get(i);

                functionEnvironment.declareVariable(new VariableValueImpl(
                        callArgExpression.getId(),
                        callArgExpression.getDataType(),
                        args.get(i),
                        callArgExpression.isConstant(),
                        new HashSet<>(),
                        true,
                        functionEnvironment
                ));
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
                                false,
                                functionEnvironment.getGlobalEnvironment());
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
                                false,
                                functionEnvironment.getGlobalEnvironment());
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