package com.duncpro.bukkit.geometry;

import org.bukkit.Axis;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.duncpro.bukkit.geometry.HighLow.HIGH;
import static com.duncpro.bukkit.geometry.HighLow.LOW;
import static com.duncpro.bukkit.geometry.Vectors.set;

public enum Side {
    TOP(Axis.Y, HIGH),
    BOTTOM(Axis.Y, LOW),
    LEFT(Axis.X, LOW),
    RIGHT(Axis.X, HIGH),
    BACK(Axis.Z, HIGH),
    FRONT(Axis.Z, LOW);

    private final Axis constantAxis;

    private final HighLow highLow;

    Side(Axis constantAxis, HighLow highLow) {
        this.constantAxis = constantAxis;
        this.highLow = highLow;
    }

    public Stream<Vector> blockPoints(Vector dimensions) {
        Axis d0 = getVariableAxis().get(0);
        Axis d1 = getVariableAxis().get(1);

        return new VoxelRectangle(Vectors.getBlock(d0, dimensions), Vectors.getBlock(d1, dimensions))
                .streamArea()
                .map(point2d -> {
                    final var v0 = point2d.getKey();
                    final var v1 = point2d.getValue();
                    Vector point3d = new Vector();
                    set(point3d, d0, v0);
                    set(point3d, d1, v1);
                    set(point3d, constantAxis, getConstantAxisValue(dimensions));
                    return point3d;
                });
    }

    public int getConstantAxisValue(Vector dimensions) {
        return switch (highLow) {
            case HIGH -> Vectors.getBlock(constantAxis, dimensions) - 1;
            case LOW -> 0;
        };
    }

    public Axis getConstantAxis() {
        return constantAxis;
    }

    public List<Axis> getVariableAxis() {
        return Arrays.stream(Axis.values())
                .filter(Predicate.not(Predicate.isEqual(constantAxis)))
                .collect(Collectors.toList());
    }

    public HighLow isHighOrLow() {
        return highLow;
    }
}
