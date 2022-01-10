package com.duncpro.bukkit.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;

import static java.util.Objects.requireNonNull;

public class JavaSerializablePersistentDataType<Z> implements PersistentDataType<byte[], Z> {
    private final Class<Z> complexType;

    public JavaSerializablePersistentDataType(Class<Z> complexType) {
        this.complexType = requireNonNull(complexType);
    }

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<Z> getComplexType() {
        return complexType;
    }

    @Override
    public byte[] toPrimitive(Z z, PersistentDataAdapterContext persistentDataAdapterContext) {
        final var bos = new ByteArrayOutputStream();

        try (final var oos = new ObjectOutputStream(bos)) {
            oos.writeObject(z);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Z fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
        final var bis = new ByteArrayInputStream(bytes);

        try (final var ois = new ObjectInputStream(bis)) {
            return (Z) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
