package com.duncpro.bukkit.geometry;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Set;

/**
 * Idempotent operations for {@link Vector} and {@link Vector}.
 */
public class Vectors {
    public static Block sum(Block a, double x, double y, double z) {
        final var newVector = sum(a.getLocation().toVector(), x, y, z);
        return a.getWorld().getBlockAt(newVector.getBlockX(), newVector.getBlockY(), newVector.getBlockZ());
    }

    public static Vector sum(Vector a, double x, double y, double z) {
        return a.clone().add(new Vector(x, y, z));
    }

    public static Vector sum(Vector a, HorizontalPosition b) {
        return a.clone().add(new Vector(b.getX(), 0, b.getZ()));
    }

    public static Vector sum(Vector a, Vector b) {
        return a.clone().add(b);
    }

    public static Vector difference(Vector a, HorizontalPosition b) {
        return a.clone().subtract(new Vector(b.getX(), 0, b.getZ()));
    }

    public static Vector difference(Location a, HorizontalPosition b) {
        return difference(a.toVector(), b);
    }

    public static Location difference(Location a, Vector b) {
        return a.clone().subtract(b);
    }

    public static Vector quotient(Vector a, Vector b) {
        return a.clone().divide(b);
    }

    public static Vector quotient(Vector a, double b) {
        return a.clone().divide(mono(b));
    }

    public static Vector quotient(Vector a, double x, double y, double z) {
        return quotient(a, new Vector(x, y, z));
    }

    public static Vector remainder(Vector a, int b) {
        return new Vector(a.getX() % b, a.getY() % b, a.getZ() % b);
    }

    public static Vector mono(double xyz) {
        return new Vector(xyz, xyz, xyz);
    }

    public static double get(Axis axis, Vector vector) {
        return switch (axis) {
            case X -> vector.getX();
            case Y -> vector.getY();
            case Z -> vector.getZ();
        };
    }

    public static int getBlock(Axis axis, Vector vector) {
        return switch (axis) {
            case X -> vector.getBlockX();
            case Y -> vector.getBlockY();
            case Z -> vector.getBlockZ();
        };
    }

    public static Vector difference(Vector vector, Axis component, double value) {
        vector = vector.clone();
        switch (component) {
            case X -> { vector.setX(vector.getX() - value); }
            case Y -> { vector.setY(vector.getY() - value); }
            case Z -> { vector.setZ(vector.getZ() - value); }
        };
        return vector;
    }

    public static Vector sum(Vector vector, Axis component, double value) {
        vector = vector.clone();
        switch (component) {
            case X -> { vector.setX(vector.getX() + value); }
            case Y -> { vector.setY(vector.getY() + value); }
            case Z -> { vector.setZ(vector.getZ() + value); }
        };
        return vector;
    }

    public static Block difference(Block block, Axis component, int value) {
        final var difference = difference(block.getLocation().toVector(), component, value);
        return difference.toLocation(block.getWorld()).getBlock();
    }

    public static Block sum(Block block, Axis component, int value) {
        final var sum = sum(block.getLocation().toVector(), component, value);
        return sum.toLocation(block.getWorld()).getBlock();
    }

    public static Vector withNewComponent(Vector vector, Axis component, double value) {
        final var cloned = vector.clone();
        switch (component) {
            case X -> { cloned.setX(value); }
            case Y -> { cloned.setY(value); }
            case Z -> { cloned.setZ(value); }
        };
        return cloned;
    }

    public static Block withNewComponent(Block block, Axis component, double value) {
        final var pos = block.getLocation().toVector();
        switch (component) {
            case X -> { pos.setX(value); }
            case Y -> { pos.setY(value); }
            case Z -> { pos.setZ(value); }
        };
        return pos.toLocation(block.getWorld()).getBlock();
    }

    public static void shift(Vector vector, CardinalDirection direction, double amount) {
        switch (direction) {
            case NORTH -> subtract(vector, Axis.Z, 1 * amount);
            case SOUTH -> add(vector, Axis.Z, 1 * amount);
            case EAST -> add(vector, Axis.X, 1 * amount);
            case WEST -> subtract(vector, Axis.X, 1 * amount);
        }
    }

    public static void shift(Location loc, CardinalDirection direction, double amount) {
        switch (direction) {
            case NORTH -> subtract(loc, Axis.Z, 1 * amount);
            case SOUTH -> add(loc, Axis.Z, 1 * amount);
            case EAST -> add(loc, Axis.X, 1 * amount);
            case WEST -> subtract(loc, Axis.X, 1 * amount);
        }
    }

    public static void add(Location loc, Axis axis, double value) {
        switch (axis) {
            case X -> loc.setX(loc.getX() + value);
            case Y -> loc.setY(loc.getY() + value);
            case Z -> loc.setZ(loc.getZ() + value);
        }
    }

    public static void add(Vector vector, Axis axis, double value) {
        switch (axis) {
            case X -> vector.setX(vector.getX() + value);
            case Y -> vector.setY(vector.getY() + value);
            case Z -> vector.setZ(vector.getZ() + value);
        }
    }

    public static void set(Vector vector, Axis axis, double value) {
        switch (axis) {
            case X -> vector.setX(value);
            case Y -> vector.setY(value);
            case Z -> vector.setZ(value);
        }
    }

    public static Vector shifted(Vector vector, CardinalDirection direction, double amount) {
        return switch (direction) {
            case NORTH -> difference(vector, Axis.Z, 1 * amount);
            case SOUTH -> sum(vector, Axis.Z, 1 * amount);
            case EAST -> sum(vector, Axis.X, 1 * amount);
            case WEST -> difference(vector, Axis.X, 1 * amount);
        };
    }

    public static Vector difference(Vector a, Vector b) {
        return a.clone().subtract(b);
    }

    public static Vector abs(Vector a) {
        return new Vector(Math.abs(a.getX()), Math.abs(a.getY()), Math.abs(a.getZ()));
    }

    public static Vector zerosExcept(Axis axis, double value) {
        return withNewComponent(mono(0), axis, value);
    }

    public static Vector copyOf(Block block) {
        return block.getLocation().toVector();
    }

    public static void subtract(Vector original, Axis component, double value) {
        original.subtract(Vectors.zerosExcept(component, value));
    }

    public static void subtract(Location original, Axis component, double value) {
        original.subtract(Vectors.zerosExcept(component, value));
    }

    public static Vector product(Vector velocity, double v) {
        return velocity.clone().multiply(v);
    }


    public static Vector of(CartesianPoint abstractPoint, Axis otherAxis, double otherValue) {
        if (Set.of(abstractPoint.leftAxis, abstractPoint.rightAxis).contains(otherAxis)) throw new IllegalArgumentException();

        final var vector = new Vector();

        set(vector, otherAxis, otherValue);
        set(vector, abstractPoint.leftAxis, abstractPoint.rightValue);
        set(vector, abstractPoint.leftAxis, abstractPoint.rightValue);

        return vector;
    }
}
