package me.itzisonn_.meazy_addon.runtime.value.impl.function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy_addon.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy_addon.runtime.value.impl.ModifierableRuntimeValueImpl;
import me.itzisonn_.registry.RegistryEntry;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class FunctionValueImpl extends ModifierableRuntimeValueImpl<Object> implements FunctionValue {
    protected final String id;
    protected final List<ParameterExpression> parameters;
    protected final DataType returnDataType;
    protected final FunctionDeclarationEnvironment parentEnvironment;
    protected boolean isOverridden = false;

    public FunctionValueImpl(String id, List<ParameterExpression> parameters, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(null, modifiers);

        if (id == null) throw new NullPointerException("Id can't be null");
        if (parameters == null) throw new NullPointerException("Parameters can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");

        this.id = id;
        this.parameters = parameters;
        this.returnDataType = returnDataType;
        this.parentEnvironment = parentEnvironment;
    }



    @Override
    public void setOverridden() {
        if (!(parentEnvironment instanceof ClassEnvironment)) throw new RuntimeException("Can't make function overridden because it's not inside a class");
        isOverridden = true;
    }

    @Override
    public boolean isLike(Object o) {
        if (o == this) return true;
        if (!(o instanceof FunctionValue other)) return false;

        Object this$id = getId();
        Object other$id = other.getId();
        if (this$id == null) {
            if (other$id != null) return false;
        }
        else if (!this$id.equals(other$id)) return false;

        Object this$parameters = getParameters();
        Object other$parameters = other.getParameters();
        if (this$parameters == null) {
            if (other$parameters != null) return false;
        }
        else if (!this$parameters.equals(other$parameters)) return false;

        Object this$returnDataType = getReturnDataType();
        Object other$returnDataType = other.getReturnDataType();
        if (this$returnDataType == null) {
            return other$returnDataType == null;
        }
        else return this$returnDataType.equals(other$returnDataType);
    }



    @Override
    public boolean isAccessible(Environment environment) {
        Identifier identifier = new FunctionIdentifier(id);

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getParentEnvironment(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
