package me.itzisonn_.meazy_addon.runtime.value.impl.function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.function.RuntimeFunctionValue;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link RuntimeFunctionValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeFunctionValueImpl extends FunctionValueImpl implements RuntimeFunctionValue {
    private final List<Statement> body;

    /**
     * @param id Id
     * @param args Args
     * @param returnDataType Which DataType should this function return or null
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     */
    public RuntimeFunctionValueImpl(String id, List<CallArgExpression> args, List<Statement> body, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(id, args, returnDataType, parentEnvironment, modifiers);
        this.body = body;
    }
}
