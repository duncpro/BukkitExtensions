package com.duncpro.bukkit.geometry;

import org.bukkit.Axis;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Stream;

import static com.duncpro.bukkit.misc.MathUtil.roundToNearest;
import static java.util.Objects.requireNonNull;

public enum CardinalDirection {
    NORTH(new Vector(0, 0, -1)), // Towards negative Z
    EAST(new Vector(1, 0, 0)), // Towards positive X
    SOUTH(new Vector(0, 0, 1)), // Towards positive Z
    WEST(new Vector(-1, 0, 0)); // Towards negative X

    private final Vector offset;

    CardinalDirection(Vector offset) {
        this.offset = requireNonNull(offset);
    }

    public Vector offset() {
        return offset.clone();
    }

    // Not from the perspective of someone looking down, but from someone looking at it.
    public CardinalDirection left() {
        return VoxelRotation.degrees(-90).on(this);
    }

    public static Vector most(CardinalDirection direction, Vector a, Vector b) {
        return switch (direction) {
            case NORTH -> Stream.of(a, b)
                    .min(Comparator.comparing(Vector::getZ))
                    .orElseThrow();
            case SOUTH -> Stream.of(a, b)
                    .max(Comparator.comparing(Vector::getZ))
                    .orElseThrow();
            case EAST -> Stream.of(a, b)
                    .max(Comparator.comparing(Vector::getX))
                    .orElseThrow();
            case WEST -> Stream.of(a, b)
                    .min(Comparator.comparing(Vector::getX))
                    .orElseThrow();
        };
    }

    public static Axis axis(CardinalDirection direction) {
        return switch (direction) {
            case NORTH, SOUTH -> Axis.Z;
            case EAST, WEST ->  Axis.X;
        };
    }

    public static CardinalDirection opposite(CardinalDirection direction) {
        return switch (direction) {
            case NORTH -> CardinalDirection.SOUTH;
            case SOUTH -> CardinalDirection.NORTH;
            case EAST ->  CardinalDirection.WEST;
            case WEST -> CardinalDirection.EAST;
        };
    }

    public static CardinalDirection fromBlockFace(BlockFace face) {
        return switch (face) {
            case NORTH -> CardinalDirection.NORTH;
            case SOUTH -> CardinalDirection.SOUTH;
            case EAST ->  CardinalDirection.EAST;
            case WEST -> CardinalDirection.WEST;
            default -> throw new IllegalStateException("Unexpected value: " + face);
        };
    }

    public static CardinalDirection thatEntityIsFacing(Entity entity) {
        return fromYaw(entity.getLocation().getYaw());
    }

    public static CardinalDirection fromYaw(float yaw) {
        final var x = roundToNearest(yaw, Set.of(0f, 90f, 180f, -90f, -180f));
        if (x == 0f) return SOUTH;
        if (x == 90f) return WEST;
        if (x == -90f) return EAST;
        if (x == 180f || x == -180f) return NORTH;
        throw new AssertionError();
    }

    public static CardinalDirection forChangeInHorizontalPosition(double fromOrigin, double toDestination, Axis alongAxis) {
        if (alongAxis == Axis.Y) throw new IllegalArgumentException();

        if (alongAxis == Axis.X) {
            return toDestination > fromOrigin ? EAST : WEST;
        }
        if (alongAxis == Axis.Z) {
            return toDestination > fromOrigin ? SOUTH : NORTH;
        }

        throw new AssertionError();
    }

    public static CardinalDirection random() {
        final var i = new Random().nextInt(0, CardinalDirection.values().length);
        return CardinalDirection.values()[i];
    }
}
