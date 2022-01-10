package com.duncpro.bukkit.persistence;

import com.google.common.collect.MoreCollectors;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public class PersistentDataTypes {
    public static final Set<PersistentDataType<?, ?>> PRIMITIVES = Set.of(
            PersistentDataType.BYTE,
            PersistentDataType.DOUBLE,
            PersistentDataType.FLOAT,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.INTEGER,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.SHORT,
            PersistentDataType.STRING
    );

    @SuppressWarnings("unchecked")
    public static <T> PersistentDataType<T, T> forPrimitive(Class<T> primitiveJavaType) {
        return PRIMITIVES.stream()
                .filter(primitiveConverter -> primitiveConverter.getPrimitiveType().isAssignableFrom(primitiveJavaType))
                .map(primitiveConverter -> (PersistentDataType<T, T>) primitiveConverter)
                .collect(MoreCollectors.onlyElement());
    }
}
