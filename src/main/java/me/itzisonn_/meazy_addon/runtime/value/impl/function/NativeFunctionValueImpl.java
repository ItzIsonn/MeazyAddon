package me.itzisonn_.meazy_addon.runtime.value.impl.function;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.function.NativeFunctionValue;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link NativeFunctionValue}
 */
@EqualsAndHashCode(callSuper = true)
public abstract class NativeFunctionValueImpl extends FunctionValueImpl implements NativeFunctionValue {
    /**
     * @param id Id
     * @param args Args
     * @param returnDataType Which DataType should this function return or null
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     */
    public NativeFunctionValueImpl(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(id, args, returnDataType, parentEnvironment, modifiers);
    }
}

