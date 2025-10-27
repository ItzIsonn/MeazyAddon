package me.itzisonn_.meazy_addon.parser.data_type;

import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.data_type.DataTypeFactory;

public class DataTypeFactoryImpl implements DataTypeFactory {
    @Override
    public DataType create(String id, boolean isNullable) {
        return new DataTypeImpl(id, isNullable);
    }

    @Override
    public DataType create(String id) {
        return new DataTypeImpl(id, true);
    }

    @Override
    public DataType create() {
        return new DataTypeImpl("Any", true);
    }
}
