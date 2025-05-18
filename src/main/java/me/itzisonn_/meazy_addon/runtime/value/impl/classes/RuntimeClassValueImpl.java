package me.itzisonn_.meazy_addon.runtime.value.impl.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.RuntimeClassValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link RuntimeClassValue}
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeClassValueImpl extends ClassValueImpl implements RuntimeClassValue {
    private final List<Statement> body;

    /**
     * @param baseClasses Base classes
     * @param environment Environment
     * @param body Body
     *
     * @throws NullPointerException If either baseClasses, environment or body is null
     */
    public RuntimeClassValueImpl(Set<String> baseClasses, ClassEnvironment environment, List<Statement> body) throws NullPointerException {
        super(baseClasses, environment);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;
    }

    /**
     * Constructor with empty baseClasses
     *
     * @param environment Environment
     * @param body Body
     *
     * @throws NullPointerException If either environment or body is null
     */
    public RuntimeClassValueImpl(ClassEnvironment environment, List<Statement> body) throws NullPointerException {
        this(new HashSet<>(), environment, body);
    }
}