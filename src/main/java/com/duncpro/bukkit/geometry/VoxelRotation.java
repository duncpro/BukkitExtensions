package com.duncpro.bukkit.geometry;

import com.duncpro.bukkit.misc.MathUtil;
import com.google.common.collect.Iterables;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;

import java.util.*;
import java.util.stream.Collectors;

import static com.duncpro.bukkit.misc.MathUtil.roundToNearest;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.toCollection;

public class VoxelRotation {
    private double magnitude; // absolute degrees
    private RotationDirection direction;

    public static VoxelRotation degrees(double degrees) {
        final var rotation = new VoxelRotation();
        rotation.magnitude = roundToNearest(abs(degrees % 360),
                Set.of(0d, 90d, 180d, 270d, 360d));
        rotation.direction = degrees < 0 ? RotationDirection.CLOCKWISE : RotationDirection.COUNTER_CLOCKWISE;
        return rotation;
    }

    public double getMagnitudeDegrees() {
        return magnitude;
    }

    public double toDegrees() {
        return magnitude * direction.signum;
    }

    public double toRadians() { return Math.toRadians(magnitude) * direction.signum; }

    public CardinalDirection on(CardinalDirection truth) {
        final var iterator = compassIterator(truth, direction);

        for (int i = 0; i < (getMagnitudeDegrees() / 90); i++) {
            iterator.next();
        }

        return iterator.next();
    }

    private static Iterator<CardinalDirection> compassIterator(CardinalDirection from, RotationDirection direction) {
        final var normalCompass = Arrays.stream(CardinalDirection.values())
                .collect(toCollection(ArrayList::new));

        if (direction == RotationDirection.COUNTER_CLOCKWISE) Collections.reverse(normalCompass);

        final var iterator = Iterables.cycle(normalCompass).iterator();

        for (int i = 0; i < normalCompass.indexOf(from); i++) {
            iterator.next();
        }
        return iterator;
    }

    public static VoxelRotation of(CardinalDirection from, CardinalDirection to, RotationDirection direction) {
        final var iterator = compassIterator(from, direction);

        int steps = 0;
        while (iterator.hasNext()) {
            final var at = iterator.next();
            if (at == to) break;
            steps++;
        }

        final var rotation = new VoxelRotation();
        rotation.magnitude = steps * 90;
        rotation.direction = direction;
        return rotation;
    }
}
