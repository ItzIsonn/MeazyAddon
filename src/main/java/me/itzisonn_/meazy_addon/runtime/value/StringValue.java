package me.itzisonn_.meazy_addon.runtime.value;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy_addon.runtime.environment.default_classes.primitive.StringClassEnvironment;
import me.itzisonn_.meazy.runtime.value.classes.DefaultClassValue;

public class StringValue extends DefaultClassValue {
    public StringValue(String value) {
        super(new StringClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), value));
    }

    public StringValue(StringClassEnvironment stringClassEnvironment) {
        super(stringClassEnvironment);
    }

    @Override
    public String getValue() {
        return getEnvironment().getVariable("value").getValue().getFinalValue().toString();
    }

    @Override
    public String toString() {
        return getValue();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        else if (!(o instanceof StringValue other)) return false;
        else {
            return other.getValue().equals(this.getValue());
        }
    }
}