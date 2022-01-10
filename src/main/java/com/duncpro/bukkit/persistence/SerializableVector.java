package com.duncpro.bukkit.persistence;

import com.duncpro.bukkit.geometry.Vectors;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.*;

import static java.util.Objects.requireNonNull;

/**
 * An serializable subclass of {@link Vector}.
 * See also {@link Vectors}.
 */
public class SerializableVector extends Vector implements Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = -6740446953908696524L;

    public SerializableVector() {
        super();
    }

    public static SerializableVector copyOf(Vector original) {
        final var copy = new SerializableVector();
        requireNonNull(original);
        copy.setX(original.getX());
        copy.setY(original.getY());
        copy.setZ(original.getZ());
        return copy;
    }

    public static SerializableVector copyOf(Location original) {
        final var copy = new SerializableVector();
        requireNonNull(original);
        copy.setX(original.getX());
        copy.setY(original.getY());
        copy.setZ(original.getZ());
        return copy;
    }

    public SerializableVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.writeDouble(x);
        stream.writeDouble(y);
        stream.writeDouble(z);
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        setX(stream.readDouble());
        setY(stream.readDouble());
        setZ(stream.readDouble());
    }
}
