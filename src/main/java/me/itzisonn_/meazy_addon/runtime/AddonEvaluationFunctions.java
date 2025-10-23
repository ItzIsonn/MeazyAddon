package me.itzisonn_.meazy_addon.runtime;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
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
import me.itzisonn_.meazy_addon.parser.ast.expression.literal.*;
import me.itzisonn_.meazy_addon.parser.ast.statement.*;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy_addon.parser.modifier.AddonModifiers;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy_addon.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy_addon.parser.AddonOperators;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy_addon.parser.data_type.DataTypeImpl;
import me.itzisonn_.meazy_addon.runtime.environment.FileEnvironmentImpl;
import me.itzisonn_.meazy_addon.runtime.value.NullValue;
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
import me.itzisonn_.meazy_addon.runtime.value.native_class.primitive.StringClassNative;
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
 * Addon evaluation functions registrar
 *
 * @see Registries#EVALUATION_FUNCTIONS
 */
public final class AddonEvaluationFunctions {
    private static boolean hasRegistered = false;
    private static final List<FunctionDeclarationStatement> extensionFunctions = new ArrayList<>();

    private AddonEvaluationFunctions() {}



    /**
     * Initializes {@link Registries#EVALUATION_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#EVALUATION_FUNCTIONS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("EvaluationFunctions have already been initialized");
        hasRegistered = true;

        register("program", Program.class, (program, context, environment, _) -> {
            Interpreter interpreter = context.getInterpreter();

            for (Statement statement : program.getBody()) {
                interpreter.evaluate(statement, environment);
            }

            return null;
        });

        register("import_statement", ImportStatement.class, (importStatement, context, environment, _) -> {
            if (!(environment instanceof FileEnvironment fileEnvironment)) {
                throw new InvalidSyntaxException("Can't use imports in non-global environment");
            }

            String folderPath = fileEnvironment.getParentFile().getParentFile().getAbsolutePath() + "\\";
            File file = new File(folderPath + importStatement.getFile());
            if (file.isDirectory() || !file.exists()) {
                throw new InvalidFileException("File '" + file.getAbsolutePath() + "' doesn't exist");
            }

            String extension = FileUtils.getExtension(file);
            Program program;
            switch (extension) {
                case "mea" -> {
                    List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(FileUtils.getLines(file));
                    program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens);
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

            GlobalEnvironment globalEnvironment = context.getGlobalEnvironment();
            globalEnvironment.addFileEnvironment(Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment));
            return null;
        });

        register("using_statement", UsingStatement.class, (usingStatement, _, environment, _) -> {
            if (!(environment instanceof FileEnvironment fileEnvironment)) {
                throw new InvalidSyntaxException("Can't use using statement in non-file environment");
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
            if (!accessibleFiles.isEmpty() && fileEnvironment.getParentFile() != null && !accessibleFiles.contains(fileEnvironment.getParentFile())) {
                throw new RuntimeException("Can't access native class " + nativeClass.getName());
            }

            fileEnvironment.addNativeClass(nativeClass);

            return null;
        });

        register("class_declaration_statement", ClassDeclarationStatement.class, (classDeclarationStatement, context, environment, _) -> {
            if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare class in this environment");
            }

            for (Modifier modifier : classDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(classDeclarationStatement, context, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                    classDeclarationEnvironment,
                    true,
                    classDeclarationStatement.getId(),
                    classDeclarationStatement.getModifiers());

            Interpreter interpreter = context.getInterpreter();
            for (Statement statement : classDeclarationStatement.getBody()) {
                interpreter.evaluate(statement, classEnvironment);
            }

            for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
                classEnvironment.declareVariable(new VariableValueImpl(
                        enumId,
                        new DataTypeImpl(classDeclarationStatement.getId(), false),
                        null,
                        true,
                        Set.of(AddonModifiers.SHARED()),
                        false,
                        classEnvironment
                ));
            }

            ClassValue classValue = null;
            if (classDeclarationStatement.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : environment.getFileEnvironment().getNativeClasses()) {
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
                    if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classDeclarationStatement.getBaseClasses(), classEnvironment, classDeclarationStatement.getBody());
                        classValue = (ClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }
            }

            if (classValue == null) {
                classValue = new RuntimeClassValueImpl(
                        classDeclarationStatement.getBaseClasses(),
                        classEnvironment,
                        classDeclarationStatement.getBody());
            }

            classDeclarationEnvironment.declareClass(classValue);

            int enumOrdinal = 1;
            List<ClassValue> enumValues = new ArrayList<>();
            for (String enumId : classDeclarationStatement.getEnumIds().keySet()) {
                List<RuntimeValue<?>> args = classDeclarationStatement.getEnumIds().get(enumId).stream().map(expression -> interpreter.evaluate(expression, classEnvironment)).collect(Collectors.toList());
                ClassEnvironment enumEnvironment = initClassEnvironment(context, classValue, classEnvironment, args);

                int finalEnumOrdinal = enumOrdinal;
                enumEnvironment.declareFunction(new NativeFunctionValueImpl("getOrdinal", List.of(), new DataTypeImpl("Int", false), enumEnvironment, Set.of()) {
                    @Override
                    public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                        return new IntValue(finalEnumOrdinal);
                    }
                });
                enumOrdinal++;

                ClassValue enumValue = new NativeClassValueImpl(enumEnvironment);
                classEnvironment.assignVariable(enumId, enumValue);
                enumValues.add(enumValue);
            }

            if (classDeclarationStatement.getModifiers().contains(AddonModifiers.ENUM())) {
                classEnvironment.declareFunction(new NativeFunctionValueImpl("getValues", List.of(), new DataTypeImpl("List", false), classEnvironment, Set.of(AddonModifiers.SHARED())) {
                    @Override
                    public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, RuntimeContext context, FunctionEnvironment functionEnvironment) {
                        return ListClassNative.newList(functionEnvironment, context, new ArrayList<>(enumValues));
                    }
                });
            }

            return null;
        });

        register("function_declaration_statement", FunctionDeclarationStatement.class, (functionDeclarationStatement, context, environment, _) -> {
            if (functionDeclarationStatement.getClassId() != null) {
                ClassValue classValue = environment.getFileEnvironment().getClass(functionDeclarationStatement.getClassId());
                if (classValue == null) throw new InvalidIdentifierException("Can't find class with id " + functionDeclarationStatement.getClassId());
                if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidIdentifierException("Can't extend final class with id " + functionDeclarationStatement.getClassId());
                if (!extensionFunctions.contains(functionDeclarationStatement)) extensionFunctions.add(functionDeclarationStatement);
                return null;
            }
            if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare function in this environment");
            }

            for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(functionDeclarationStatement, context, environment)) throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
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

        register("variable_declaration_statement", VariableDeclarationStatement.class, (variableDeclarationStatement, context, environment, _) -> {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare function in this environment");
            }

            for (Modifier modifier : variableDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(variableDeclarationStatement, context, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' Modifier");
            }

            Set<Modifier> modifiers = new HashSet<>(variableDeclarationStatement.getModifiers());
            if (!(environment instanceof ClassEnvironment) && environment.isShared() &&
                    !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED())) modifiers.add(AddonModifiers.SHARED());

            Interpreter interpreter = context.getInterpreter();
            for (VariableDeclarationStatement.VariableDeclarationInfo variableDeclarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
                RuntimeValue<?> value = null;

                if (variableDeclarationInfo.getValue() != null && !(environment instanceof ClassEnvironment && environment.isShared() &&
                        !variableDeclarationStatement.getModifiers().contains(AddonModifiers.SHARED()))) {
                    boolean placed = false;

                    if ((environment instanceof FileEnvironment || environment instanceof ClassEnvironment) && environment.isShared()) {
                        if (environment.getFileEnvironment() instanceof FileEnvironmentImpl fileEnvironment) {
                            fileEnvironment.getVariableQueue().put(variableDeclarationInfo, variableDeclarationEnvironment);
                            placed = true;
                        }
                    }

                    if (!placed) value = interpreter.evaluate(variableDeclarationInfo.getValue(), environment);
                }

                VariableValue variableValue = new VariableValueImpl(
                        variableDeclarationInfo.getId(),
                        variableDeclarationInfo.getDataType(),
                        value,
                        variableDeclarationStatement.isConstant(),
                        modifiers,
                        false,
                        variableDeclarationEnvironment
                );
                variableDeclarationEnvironment.declareVariable(variableValue);
            }

            return null;
        });

        register("constructor_declaration_statement", ConstructorDeclarationStatement.class, (constructorDeclarationStatement, context, environment, _) -> {
            if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare constructor in this environment");
            }

            for (Modifier modifier : constructorDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(constructorDeclarationStatement, context, environment))
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

        register("base_call_statement", BaseCallStatement.class, (baseCallStatement, context, environment, extra) -> {
            ClassEnvironment classEnvironment;
            if (extra.length == 1 && extra[0] instanceof ClassEnvironment env) classEnvironment = env;
            else throw new InvalidSyntaxException("Unknown error occurred");

            if (!(environment instanceof ConstructorEnvironment constructorEnvironment)) {
                throw new InvalidSyntaxException("Can't use BaseCallStatement in this environment");
            }

            Interpreter interpreter = context.getInterpreter();
            ClassValue baseClassValue = environment.getFileEnvironment().getClass(baseCallStatement.getId());
            List<RuntimeValue<?>> args = baseCallStatement.getArgs().stream().map(expression -> interpreter.evaluate(expression, environment)).collect(Collectors.toList());

            classEnvironment.addBaseClass(initClassEnvironment(context, baseClassValue, constructorEnvironment, args));
            return new BaseClassIdValue(baseCallStatement.getId());
        });


        register("if_statement", IfStatement.class, (ifStatement, context, environment, _) -> {
            Interpreter interpreter = context.getInterpreter();

            while (ifStatement != null) {
                if (ifStatement.getCondition() != null) {
                    if (!parseCondition(context, ifStatement.getCondition(), environment)) {
                        ifStatement = ifStatement.getElseStatement();
                        continue;
                    }
                }

                Environment ifEnvironment = Registries.ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);

                for (int i = 0; i < ifStatement.getBody().size(); i++) {
                    Statement statement = ifStatement.getBody().get(i);
                    RuntimeValue<?> result = interpreter.evaluate(statement, ifEnvironment);

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

        register("for_statement", ForStatement.class, (forStatement, context, environment, _) -> {
            LoopEnvironment forEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);
            Interpreter interpreter = context.getInterpreter();

            forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                    forEnvironment.declareVariable(new VariableValueImpl(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            variableDeclarationInfo.getValue() == null ?
                                    null :
                                    interpreter.evaluate(variableDeclarationInfo.getValue(), environment),
                            forStatement.getVariableDeclarationStatement().isConstant(),
                            Set.of(),
                            false,
                            forEnvironment
                    ))
            );

            main:
            while (parseCondition(context, forStatement.getCondition(), forEnvironment)) {
                for (int i = 0; i < forStatement.getBody().size(); i++) {
                    Statement statement = forStatement.getBody().get(i);
                    RuntimeValue<?> result = interpreter.evaluate(statement, forEnvironment);

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
                evaluateAssignmentExpression(context, forStatement.getAssignmentExpression(), forEnvironment);
            }

            return null;
        });

        register("foreach_statement", ForeachStatement.class, (foreachStatement, context, environment, _) -> {
            LoopEnvironment foreachEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);
            Interpreter interpreter = context.getInterpreter();

            RuntimeValue<?> rawCollectionValue = interpreter.evaluate(foreachStatement.getCollection(), foreachEnvironment).getFinalRuntimeValue();
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
                    RuntimeValue<?> result = interpreter.evaluate(statement, foreachEnvironment);

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

        register("while_statement", WhileStatement.class, (whileStatement, context, environment, _) -> {
            LoopEnvironment whileEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment);
            Interpreter interpreter = context.getInterpreter();

            main:
            while (parseCondition(context, whileStatement.getCondition(), environment)) {
                whileEnvironment.clearVariables();

                for (int i = 0; i < whileStatement.getBody().size(); i++) {
                    Statement statement = whileStatement.getBody().get(i);
                    RuntimeValue<?> result = interpreter.evaluate(statement, whileEnvironment);

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

        register("return_statement", ReturnStatement.class, (returnStatement, context, environment, _) -> {
            if (environment instanceof FunctionEnvironment || environment.hasParent(parent -> parent instanceof FunctionEnvironment)) {
                if (returnStatement.getValue() == null) return null;
                return context.getInterpreter().evaluate(returnStatement.getValue(), environment);
            }

            if (returnStatement.getValue() == null) {
                return null;
            }

            throw new InvalidSyntaxException("Can't return value not inside a function");
        });

        register("continue_statement", ContinueStatement.class, (_, _, environment, _) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can't use continue statement outside of for/while statements");
        });

        register("break_statement", BreakStatement.class, (_, _, environment, _) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can't use break statement outside of for/while statements");
        });

        register("assignment_expression", AssignmentExpression.class, (assignmentExpression, context, environment, _) -> evaluateAssignmentExpression(context, assignmentExpression, environment));

        register("list_creation_expression", ListCreationExpression.class, (listCreationExpression, context, environment, _) -> {
            Interpreter interpreter = context.getInterpreter();
            List<RuntimeValue<?>> list = listCreationExpression.getList().stream().map(expression -> interpreter.evaluate(expression, environment)).collect(Collectors.toList());
            return ListClassNative.newList(environment, context, list);
        });

        register("map_creation_expression", MapCreationExpression.class, (mapCreationExpression, context, environment, _) -> {
            Map<RuntimeValue<?>, RuntimeValue<?>> map = new HashMap<>();
            Interpreter interpreter = context.getInterpreter();

            for (Expression key : mapCreationExpression.getMap().keySet()) {
                Expression value = mapCreationExpression.getMap().get(key);
                map.put(interpreter.evaluate(key, environment), interpreter.evaluate(value, environment));
            }

            return MapClassNative.newMap(environment, context, map);
        });

        register("null_check_expression", NullCheckExpression.class, (nullCheckExpression, context, environment, _) -> {
            Interpreter interpreter = context.getInterpreter();
            RuntimeValue<?> checkValue = interpreter.evaluate(nullCheckExpression.getCheckExpression(), environment).getFinalRuntimeValue();

            if (checkValue instanceof NullValue) {
                return interpreter.evaluate(nullCheckExpression.getNullExpression(), environment).getFinalRuntimeValue();
            }
            return checkValue;
        });

        register("is_expression", IsExpression.class, (isExpression, context, environment, _) -> {
            RuntimeValue<?> value = context.getInterpreter().evaluate(isExpression.getValue(), environment).getFinalRuntimeValue();

            ClassValue classValue = environment.getFileEnvironment().getClass(isExpression.getDataType());
            if (classValue == null) throw new InvalidSyntaxException("Data type with id " + isExpression.getDataType() + " doesn't exist");

            if (isExpression.isLike()) return new BooleanValue(classValue.isLikeMatches(environment.getFileEnvironment(), value.getFinalRuntimeValue()));
            return new BooleanValue(classValue.isMatches(value.getFinalRuntimeValue()));
        });

        register("operator_expression", OperatorExpression.class, (operatorExpression, context, environment, _) -> {
            Interpreter interpreter = context.getInterpreter();
            RuntimeValue<?> left = interpreter.evaluate(operatorExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = operatorExpression.getRight() != null ? interpreter.evaluate(operatorExpression.getRight(), environment).getFinalRuntimeValue() : null;

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
                    if (operatorFunction != null) return callFunction(context, operatorFunction, args);
                }
            }

            RuntimeValue<?> result = operatorExpression.getOperator().calculate(environment, left, right);
            if (result == null) throw new UnsupportedOperatorException(operatorExpression.getOperator().getSymbol());

            return result;
        });

        register("class_call_expression", ClassCallExpression.class, (classCallExpression, context, environment, extra) -> {
            Environment extraEnvironment;
            if (extra.length == 0) extraEnvironment = environment;
            else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
            else extraEnvironment = environment;

            Interpreter interpreter = context.getInterpreter();
            List<RuntimeValue<?>> args = classCallExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());
            RuntimeValue<?> rawClass = interpreter.evaluate(classCallExpression.getCaller(), environment);

            if (!(rawClass instanceof ClassValue classValue)) throw new InvalidCallException("Can't call " + rawClass.getClass().getName() + " because it's not a class");
            if (classValue.getModifiers().contains(AddonModifiers.ABSTRACT())) throw new InvalidCallException("Can't create instance of an abstract class " + classValue.getId());
            if (classValue.getModifiers().contains(AddonModifiers.ENUM())) throw new InvalidCallException("Can't create instance of an enum class " + classValue.getId());

            return callClassValue(context, classValue, extraEnvironment, args);
        });

        register("member_expression", MemberExpression.class, (memberExpression, context, environment, _) -> {
            Interpreter interpreter = context.getInterpreter();
            RuntimeValue<?> value = interpreter.evaluate(memberExpression.getObject(), environment).getFinalRuntimeValue();

            if (value instanceof NullValue) {
                if (memberExpression.isNullSafe()) return value;
                else throw new InvalidSyntaxException("Can't get member of null value");
            }

            if (value instanceof ClassValue classValue) {
                return interpreter.evaluate(memberExpression.getMember(), classValue.getEnvironment(), environment);
            }

            throw new InvalidSyntaxException("Can't get member of " + value + " because it's not a class");
        });

        register("function_call_expression", FunctionCallExpression.class, (functionCallExpression, context, environment, extra) -> {
            Environment extraEnvironment;
            if (extra.length == 0) extraEnvironment = environment;
            else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
            else extraEnvironment = environment;

            Interpreter interpreter = context.getInterpreter();
            List<RuntimeValue<?>> args = functionCallExpression.getArgs().stream().map(expression -> interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());

            RuntimeValue<?> function = interpreter.evaluate(functionCallExpression.getCaller(), environment, extraEnvironment, args);
            if (!(function instanceof FunctionValue functionValue)) {
                throw new InvalidCallException("Can't call " + function.getValue() + " because it's not a function");
            }

            return callFunction(context, functionValue, args);
        });

        register("identifier", Identifier.class, new EvaluationFunction<>() {
            @Override
            public RuntimeValue<?> evaluate(Identifier identifier, RuntimeContext context, Environment environment, Object... extra) {
                Environment requestEnvironment;
                if (extra.length == 0) requestEnvironment = environment;
                else if (extra[0] instanceof Environment env) requestEnvironment = env;
                else requestEnvironment = environment;

                if (identifier instanceof VariableIdentifier) {
                    VariableDeclarationEnvironment variableDeclarationEnvironment = environment.getVariableDeclarationEnvironment(identifier.getId());
                    if (variableDeclarationEnvironment == null) throw new InvalidIdentifierException("Variable with id " + identifier.getId() + " doesn't exist");

                    VariableValue variableValue = variableDeclarationEnvironment.getVariable(identifier.getId());
                    if (variableValue == null) throw new InvalidIdentifierException("Variable with id " + identifier.getId() + " doesn't exist");

                    for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
                        Modifier modifier = entry.getValue();
                        boolean hasModifier = variableValue.getModifiers().contains(modifier);

                        if (!modifier.canAccess(context, requestEnvironment, variableDeclarationEnvironment, identifier, hasModifier)) {
                            if (hasModifier) throw new InvalidAccessException("Can't access variable with id " + identifier.getId() + " because it has " + modifier.getId() + " modifier");
                            else throw new InvalidAccessException("Can't access variable with id " + identifier.getId() + " because it doesn't have " + modifier.getId() + " modifier");
                        }
                    }

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

                    for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
                        Modifier modifier = entry.getValue();
                        boolean hasModifier = functionValue.getModifiers().contains(modifier);

                        if (!modifier.canAccess(context, requestEnvironment, functionDeclarationEnvironment, identifier, hasModifier)) {
                            if (hasModifier) throw new InvalidAccessException("Can't access function with id " + identifier.getId() + " because it has " + modifier.getId() + " modifier");
                            else throw new InvalidAccessException("Can't access function with id " + identifier.getId() + " because it doesn't have " + modifier.getId() + " modifier");
                        }
                    }

                    return functionValue;
                }

                if (identifier instanceof ClassIdentifier) {
                    ClassValue classValue = environment.getFileEnvironment().getClass(identifier.getId());
                    if (classValue == null) return evaluate(new VariableIdentifier(identifier.getId()), context, environment, extra);

                    for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
                        Modifier modifier = entry.getValue();
                        boolean hasModifier = classValue.getModifiers().contains(modifier);

                        if (!modifier.canAccess(context, requestEnvironment, context.getGlobalEnvironment(), identifier, hasModifier)) {
                            if (hasModifier) throw new InvalidAccessException("Can't access class with id " + identifier.getId() + " because it has " + modifier.getId() + " modifier");
                            else throw new InvalidAccessException("Can't access class with id " + identifier.getId() + " because it doesn't have " + modifier.getId() + " modifier");
                        }
                    }

                    return classValue;
                }

                throw new InvalidIdentifierException("Invalid identifier " + identifier.getClass().getName());
            }
        });

        register("null_literal", NullLiteral.class, (_, _, _, _) -> new NullValue());
        register("number_literal", NumberLiteral.class, (numberLiteral, _, _, _) -> {
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
        register("string_literal", StringLiteral.class, (stringLiteral, _, environment, _) -> StringClassNative.newString(environment, stringLiteral.getValue()));
        register("boolean_literal", BooleanLiteral.class, (booleanLiteral, _, _, _) -> new BooleanValue(booleanLiteral.isValue()));

        register("this_literal", ThisLiteral.class, (_, _, environment, _) -> {
            Environment parent = environment.getParent(env -> env instanceof ClassEnvironment);
            if (!(parent instanceof ClassEnvironment classEnvironment)) throw new RuntimeException("Can't use 'this' keyword not inside a class");
            if (environment.isShared()) throw new RuntimeException("Can't use 'this' keyword inside a shared environment");
            return new NativeClassValueImpl(
                    classEnvironment.getBaseClasses().stream().map(ClassEnvironment::getId).collect(Collectors.toSet()),
                    classEnvironment);
        });
    }



    private static RuntimeValue<?> evaluateAssignmentExpression(RuntimeContext context, AssignmentExpression assignmentExpression, Environment environment) {
        Interpreter interpreter = context.getInterpreter();

        if (assignmentExpression.getId() instanceof VariableIdentifier variableIdentifier) {
            RuntimeValue<?> value = interpreter.evaluate(assignmentExpression.getValue(), environment).getFinalRuntimeValue();
            environment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
            return value;
        }
        if (assignmentExpression.getId() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = interpreter.evaluate(memberExpression, environment);
            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> value = interpreter.evaluate(assignmentExpression.getValue(), environment).getFinalRuntimeValue();
                variableValue.setValue(value);
                return value;
            }
            throw new InvalidSyntaxException("Can't assign value to not variable " + memberExpressionValue);
        }
        throw new InvalidSyntaxException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    private static boolean parseCondition(RuntimeContext context,Expression rawCondition,  Environment environment) {
        RuntimeValue<?> condition = context.getInterpreter().evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, boolean isDefault, FileEnvironment fileEnvironment) {
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

        if (!returnDataType.isMatches(returnValue, fileEnvironment)) {
            throw new InvalidSyntaxException("Returned value's data type is different from specified (" + returnDataType.getId() + ")" + defaultString);
        }

        return returnValue;
    }

    public static boolean hasRepeatedBaseClasses(Set<String> baseClassesList, List<String> baseClasses, FileEnvironment fileEnvironment) {
        for (String baseClass : baseClassesList) {
            if (baseClasses.contains(baseClass)) {
                return true;
            }
            baseClasses.add(baseClass);

            ClassValue classValue = fileEnvironment.getClass(baseClass);
            if (classValue == null) {
                throw new InvalidIdentifierException("Class with id " + baseClass + " doesn't exist");
            }
            if (classValue.getModifiers().contains(AddonModifiers.FINAL())) throw new InvalidAccessException("Can't inherit final class with id " + baseClass);

            boolean check = hasRepeatedBaseClasses(classValue.getBaseClasses(), baseClasses, fileEnvironment);
            if (check) return true;
        }

        return false;
    }

    public static boolean hasRepeatedVariables(Set<String> baseClassesList, List<String> variables, FileEnvironment fileEnvironment) {
        for (String baseClass : baseClassesList) {
            ClassValue classValue = fileEnvironment.getClass(baseClass);
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

            boolean check = hasRepeatedVariables(classValue.getBaseClasses(), variables, fileEnvironment);
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

    public static ClassValue callClassValue(RuntimeContext context, ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = initClassEnvironment(context, classValue, callEnvironment, args);

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            if (runtimeClassValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : runtimeClassValue.getEnvironment().getFileEnvironment().getNativeClasses()) {
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
                    if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
                        return (ClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }
            }

            return new RuntimeClassValueImpl(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
        }

        if (classValue instanceof NativeClassValue nativeClassValue) return nativeClassValue.newInstance(nativeClassValue.getBaseClasses(), classEnvironment);

        throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
    }

    public static ClassEnvironment initClassEnvironment(RuntimeContext context, ClassValue classValue, Environment callEnvironment, List<RuntimeValue<?>> args) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getFileEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);
        Interpreter interpreter = context.getInterpreter();

        Set<String> calledBaseClasses = new HashSet<>();
        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            for (Statement statement : runtimeClassValue.getBody()) {
                interpreter.evaluate(statement, classEnvironment);
            }
            for (FunctionDeclarationStatement functionDeclarationStatement : extensionFunctions) {
                if (functionDeclarationStatement.getClassId().equals(runtimeClassValue.getId())) {
                    interpreter.evaluate(new FunctionDeclarationStatement(
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
                        for (Class<?> nativeClass : runtimeConstructorValue.getParentEnvironment().getFileEnvironment().getNativeClasses()) {
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

                            ClassValue parentClassValue = callEnvironment.getFileEnvironment().getClass(classEnv.getId());
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
                            RuntimeValue<?> value = interpreter.evaluate(statement, constructorEnvironment, classEnvironment);
                            if (!(value instanceof BaseClassIdValue baseClassIdValue)) throw new RuntimeException("Unknown error occurred");
                            if (!classValue.getBaseClasses().contains(baseClassIdValue.getValue()))
                                throw new InvalidSyntaxException("Can't call base class " + baseClassIdValue.getValue() + " because it's not base class of class " + classValue.getId());
                            calledBaseClasses.add(baseClassIdValue.getValue());
                        }
                        else interpreter.evaluate(statement, constructorEnvironment);
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

                    if (nativeConstructorValue.getModifiers().contains(AddonModifiers.PROTECTED()) && !callEnvironment.hasParent(env -> { //TODO move to modifiers class
                        if (env instanceof ClassEnvironment classEnv) {
                            if (classEnv.getId().equals(classValue.getId())) return true;

                            ClassValue parentClassValue = callEnvironment.getFileEnvironment().getClass(classEnv.getId());
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

                    nativeConstructorValue.run(args, context, constructorEnvironment);
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
            ClassValue baseClassValue = callEnvironment.getFileEnvironment().getClass(baseClass);
            classEnvironment.addBaseClass(initClassEnvironment(context, baseClassValue, constructorEnvironment, new ArrayList<>()));
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

    public static ClassValue callEmptyClassValue(RuntimeContext context, ClassValue classValue) {
        ClassEnvironment classEnvironment = initEmptyClassEnvironment(context, classValue);

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            if (runtimeClassValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                for (Class<?> nativeClass : classEnvironment.getFileEnvironment().getNativeClasses()) {
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
                    if (!ClassValue.class.isAssignableFrom(method.getReturnType())) {
                        throw new RuntimeException("Return value of native method with id " + method.getName() + " is invalid");
                    }

                    try {
                        Object object = method.invoke(null, classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
                        return (ClassValue) object;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Failed to call native method", e);
                    }
                }
            }

            return new RuntimeClassValueImpl(classValue.getBaseClasses(), classEnvironment, runtimeClassValue.getBody());
        }
        if (classValue instanceof NativeClassValue nativeClassValue) return nativeClassValue.newInstance(nativeClassValue.getBaseClasses(), classEnvironment);

        throw new InvalidCallException("Can't call " + classValue.getClass().getName() + " because it's unknown class");
    }

    public static ClassEnvironment initEmptyClassEnvironment(RuntimeContext context, ClassValue classValue) {
        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classValue.getEnvironment().getFileEnvironment(),
                classValue.getId(),
                classValue.getModifiers());

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment);
        Interpreter interpreter = context.getInterpreter();

        if (classValue instanceof RuntimeClassValue runtimeClassValue) {
            for (Statement statement : runtimeClassValue.getBody()) {
                interpreter.evaluate(statement, classEnvironment);
            }
            for (FunctionDeclarationStatement functionDeclarationStatement : extensionFunctions) {
                if (functionDeclarationStatement.getClassId().equals(runtimeClassValue.getId())) {
                    interpreter.evaluate(new FunctionDeclarationStatement(
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
            ClassValue baseClassValue = classEnvironment.getFileEnvironment().getClass(baseClass);
            classEnvironment.addBaseClass(initClassEnvironment(context, baseClassValue, constructorEnvironment, new ArrayList<>()));
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

    private static RuntimeValue<?> callFunction(RuntimeContext context, FunctionValue functionValue, List<RuntimeValue<?>> args) {
        if (functionValue.getArgs().size() != args.size()) {
            throw new InvalidCallException("Expected " + functionValue.getArgs().size() + " args but found " + args.size());
        }

        FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                functionValue.getParentEnvironment(),
                functionValue.getModifiers().contains(AddonModifiers.SHARED()));
        Interpreter interpreter = context.getInterpreter();

        if (functionValue instanceof NativeFunctionValue nativeFunctionValue) {
            RuntimeValue<?> returnValue = nativeFunctionValue.run(args, context, functionEnvironment);
            if (returnValue != null) returnValue = returnValue.getFinalRuntimeValue();
            return checkReturnValue(
                    returnValue,
                    nativeFunctionValue.getReturnDataType(),
                    nativeFunctionValue.getId(),
                    true,
                    functionEnvironment.getFileEnvironment());
        }
        if (functionValue instanceof RuntimeFunctionValue runtimeFunctionValue) {
            if (runtimeFunctionValue.getModifiers().contains(AddonModifiers.NATIVE())) {
                ArrayList<Class<?>> params1 = new ArrayList<>(Collections.nCopies(functionValue.getArgs().size(), RuntimeValue.class));
                params1.add(RuntimeContext.class);
                params1.add(FunctionEnvironment.class);
                Class<?>[] array1 = params1.toArray(Class[]::new);

                ArrayList<Class<?>> params2 = new ArrayList<>(Collections.nCopies(functionValue.getArgs().size(), RuntimeValue.class));
                params2.add(FunctionEnvironment.class);
                Class<?>[] array2 = params2.toArray(Class[]::new);

                for (Class<?> nativeClass : runtimeFunctionValue.getParentEnvironment().getFileEnvironment().getNativeClasses()) {
                    Method method;
                    boolean hasContext;

                    try {
                        method = nativeClass.getDeclaredMethod(functionValue.getId(), array1);
                        hasContext = true;
                    }
                    catch (NoSuchMethodException ignore) {
                        try {
                            method = nativeClass.getDeclaredMethod(functionValue.getId(), array2);
                            hasContext = false;
                        }
                        catch (NoSuchMethodException ignore1) {
                            continue;
                        }
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
                        if (hasContext) methodArgs.add(context);
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
                                    functionEnvironment.getFileEnvironment());
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
                    result = interpreter.evaluate(statement, functionEnvironment);
                    if (result != null) {
                        checkReturnValue(
                                result.getFinalRuntimeValue(),
                                runtimeFunctionValue.getReturnDataType(),
                                runtimeFunctionValue.getId(),
                                false,
                                functionEnvironment.getFileEnvironment());
                    }
                    if (i + 1 < runtimeFunctionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                    break;
                }
                RuntimeValue<?> value = interpreter.evaluate(statement, functionEnvironment);
                if (value instanceof ReturnInfoValue returnInfoValue) {
                    hasReturnStatement = true;
                    result = returnInfoValue.getFinalRuntimeValue();
                    if (result.getFinalValue() != null) {
                        checkReturnValue(
                                result.getFinalRuntimeValue(),
                                runtimeFunctionValue.getReturnDataType(),
                                runtimeFunctionValue.getId(),
                                false,
                                functionEnvironment.getFileEnvironment());
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