package com.duncpro.bukkit.persistence.json;

import com.duncpro.bukkit.persistence.PersistentDataContainerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

public class JsonSerializablePersistentDataType<Z> implements PersistentDataType<byte[], Z> {
    private final Class<Z> elementType;
    private final ObjectMapper jackson;

    public JsonSerializablePersistentDataType(Class<Z> elementType, ObjectMapper jackson) {
        this.elementType = requireNonNull(elementType);
        this.jackson = requireNonNull(jackson);
    }

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<Z> getComplexType() {
        return elementType;
    }

    @Override
    public byte[] toPrimitive(Z z, PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            return jackson.writeValueAsString(z).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new PersistentDataContainerException(e);
        }
    }

    @Override
    public Z fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            return jackson.readValue(new String(bytes, StandardCharsets.UTF_8), elementType);
        } catch (JsonProcessingException e) {
            throw new PersistentDataContainerException(e);
        }
    }
}
