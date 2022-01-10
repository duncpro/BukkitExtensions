package com.duncpro.bukkit.geometry;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class HorizontalPosition extends CartesianPoint {

    public HorizontalPosition(double x, double z) {
        super(x, Axis.X, z, Axis.Z);
    }

    public double getX() {
        return this.leftValue;
    }

    public double getZ() {
        return this.rightValue;
    }

    public static HorizontalPosition of(Block targetBlock) {
        return of(targetBlock.getLocation());
    }

    public static HorizontalPosition of(Location location) {
        return of(location.toVector());
    }

    public Vector toVector(double y) {
        return new Vector(getX(), y, getZ());
    }

    public Vector toVector() {
        return toVector(0);
    }

    public int getBlockX() {
        return toVector().getBlockX();
    }

    public int getBlockZ() {
        return toVector().getBlockZ();
    }

    public static HorizontalPosition of(Vector vector) {
        return new HorizontalPosition(vector.getX(), vector.getZ());
    }

    public static HorizontalPosition floor(HorizontalPosition a) {
        return new HorizontalPosition(Math.floor(a.getX()), Math.floor(a.getZ()));
    }

    public static HorizontalPosition remainder(HorizontalPosition a, double b) {
        return new HorizontalPosition(a.getX() % b, a.getZ() % b);
    }

    public static HorizontalPosition abs(HorizontalPosition a) {
        return new HorizontalPosition(Math.abs(a.getX()), Math.abs(a.getZ()));
    }

    public static HorizontalPosition product(HorizontalPosition a, double b) {
        return new HorizontalPosition(a.getX() * b, a.getZ() * b);
    }

    public static HorizontalPosition quotient(HorizontalPosition a, double b) {
        return new HorizontalPosition(a.getX() / b, a.getZ() / b);
    }

    public static HorizontalPosition difference(HorizontalPosition a, HorizontalPosition b) {
        return new HorizontalPosition(a.getX() - b.getX(), a.getZ() - b.getZ());
    }

    public static HorizontalPosition sum(HorizontalPosition a, HorizontalPosition b) {
        return new HorizontalPosition(a.getX() + b.getX(), a.getZ() + b.getZ());
    }

    public static HorizontalPosition snappedToGrid(HorizontalPosition a) {
        return floor(a);
    }


    public static HorizontalPosition shifted(HorizontalPosition pos, CardinalDirection direction, double amount) {
        return switch (direction) {
            case NORTH -> difference(pos, new HorizontalPosition(0, 1 * amount));
            case SOUTH -> sum(pos, new HorizontalPosition(0, 1 * amount));
            case EAST -> sum(pos, new HorizontalPosition(1 * amount, 0));
            case WEST -> difference(pos, new HorizontalPosition(1 * amount, 0));
        };
    }
}
