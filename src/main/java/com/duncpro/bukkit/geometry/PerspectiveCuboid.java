package com.duncpro.bukkit.geometry;

import com.duncpro.bukkit.Materials;
import com.duncpro.bukkit.region.WorldSnapshot;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.Axis;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Set;

import static com.duncpro.bukkit.geometry.CardinalDirection.*;
import static com.duncpro.bukkit.geometry.Vectors.*;
import static java.util.Objects.requireNonNull;

/**
 * This class offers a mechanism for representing cuboids which have a logical orientation.
 *
 * Given some cuboid region in the world, and a {@link CardinalDirection} describing the front face of the
 * cuboid, one can query the blocks within the cuboid in an ergonomic, human friendly fashion, in terms
 * of leftness, rightness, frontness, and backness, instead of northness, southness, eastness and westness.
 *
 * The coordinates of the blocks within a {@link PerspectiveCuboid} are fixed, independent of the orientation
 * in which the cuboid is placed within the world. The blocks in all {@link PerspectiveCuboid}s are always indexed
 * from 0 to max axis length (exclusive).
 *
 * X = 0 = the leftmost position in the slice
 * Y = 0 = the bottommost position in the slice
 * Z = 0 = is the frontmost position in the slice.
 */
public class PerspectiveCuboid {
    private final Vector pointA;
    private final Vector pointB;
    private final CardinalDirection frontFace;

    protected PerspectiveCuboid(Vector pointA, Vector pointB, CardinalDirection front) {
        this.pointA = requireNonNull(pointA).clone();
        this.pointB = requireNonNull(pointB).clone();
        this.frontFace = requireNonNull(front);
    }

    public static PerspectiveCuboid fromAbsolutePosition(Vector pointA, Vector pointB, CardinalDirection front) {
        return new PerspectiveCuboid(pointA, pointB, front);
    }

    public static PerspectiveCuboid ofRelativeDimensions(Vector origin, Vector dimensions, CardinalDirection facing) {
        final var otherCorner = calculateTopBackRightCorner(origin, facing, dimensions);
        return new PerspectiveCuboid(origin, otherCorner, facing);
    }

    public static PerspectiveCuboid horizontallyCenteredAround(Vector point, Vector dimensions, CardinalDirection facing) {
        final var offsetOrigin = point.clone();
        shift(offsetOrigin, facing.left(), dimensions.getX() / 2.0);
        shift(offsetOrigin, facing, dimensions.getZ() / 2.0);
        return ofRelativeDimensions(offsetOrigin, dimensions, facing);
    }

    private CardinalDirection leftFace() {
        return VoxelRotation.degrees(-90).on(frontFace);
    }

    /**
     * Returns the distance, in blocks, from the bottommost block to the topmost block
     * within this {@link PerspectiveCuboid}.
     */
    public double getHeight() {
        return getAbsoluteDimensions().getY();
    }

    /**
     * Returns the distance, in blocks, from the leftmost block to the rightmost block
     * within this {@link PerspectiveCuboid}.
     */
    public double getLength() {
        return get(axis(leftFace()), getAbsoluteDimensions());
    }

    /**
     * Returns the distance, in blocks, from the frontmost block to the backmost block
     * within this {@link PerspectiveCuboid}.
     */
    public double getDepth() {
        return get(axis(frontFace), getAbsoluteDimensions());
    }

    /**
     * Calculates the frontmost bottommost leftmost block in the slice.
     */
    public Vector getOrigin() {
        var origin = Vectors.mono(0);

        origin.setY(Math.min(pointA.getY(), pointB.getY()));

        set(origin, axis(frontFace), get(axis(frontFace), most(frontFace, pointA, pointB)));

        set(origin, axis(leftFace()), get(axis(leftFace()), most(leftFace(), pointA, pointB)));

        return origin;
    }

    /**
     * The blocks in all {@link PerspectiveCuboid}s are indexed the same way, from 0 to max axis length (exclusive).
     * Where x = 0 is the leftmost block in the slice
     * Where y = 0 is the bottommost block in the slice
     * Where z = 0 is the frontmost block in the slice.
     */
    public Vector getAbsolutePosition(Vector relativePos) throws IndexOutOfBoundsException {
        var origin = getOrigin();

        shift(origin, opposite(leftFace()), relativePos.getX());
        shift(origin, opposite(frontFace), relativePos.getZ());
        add(origin, Axis.Y, relativePos.getY());

        return origin;
    }

    public Vector getRelativePosition(Vector absolutePosition) {
        return abs(difference(absolutePosition, getOrigin()));
    }

    public Vector getCenterpoint() {
        return getAbsoluteDimensions().divide(mono(2));
    }

    /**
     * Returns the absolute dimensions of this cuboid in terms of Minecraft coordinates.
     */
    public Vector getAbsoluteDimensions() {
        final var delta = abs(difference(pointA, pointB));
        return sum(delta, mono(1));
    }

    /**
     * Returns the relative dimensions of this cuboid, which are fixed, regardless of how this cuboid
     * is positioned in the world.
     */
    public Vector getRelativeDimensions() {
        return new Vector(getLength(), getHeight(), getDepth());
    }

    public CardinalDirection getFrontFace() {
        return frontFace;
    }

    /**
     * Projects this {@link PerspectiveCuboid} onto a new location.
     */
    public PerspectiveCuboid project(Vector newOrigin, CardinalDirection nowFacing) {
        final var otherCorner = calculateTopBackRightCorner(newOrigin, nowFacing, this.getRelativeDimensions());
        return new PerspectiveCuboid(newOrigin, otherCorner, nowFacing);
    }

    public PerspectiveCuboid project(Vector newOrigin) {
        return project(newOrigin, frontFace);
    }

    public boolean checkOccupancy(WorldSnapshot world, Set<Vector> filter) {
        return filter.stream()
                .map(this::getAbsolutePosition)
                .map(pos -> world.getBlockData(pos).getMaterial())
                .allMatch(Materials.AIR::contains);
    }

    public boolean checkOccupancy(WorldSnapshot world) {
        return VoxelCuboid.streamBlockPoints(getRelativeDimensions())
                .map(this::getAbsolutePosition)
                .map(pos -> world.getBlockData(pos).getMaterial())
                .allMatch(Materials.AIR::contains);
    }

    private static Vector calculateTopBackRightCorner(Vector origin, CardinalDirection facing, Vector dimensions) {
        // Subtract the origin from the the dimensions
        dimensions = dimensions.subtract(mono(1));

        final var newLeftFace = VoxelRotation.degrees(-90).on(facing);

        final var backTopRightCorner = origin.clone();
        shift(backTopRightCorner, opposite(newLeftFace), dimensions.getX());
        shift(backTopRightCorner, opposite(facing), dimensions.getZ());
        add(backTopRightCorner, Axis.Y, dimensions.getY());
        return backTopRightCorner;
    }

    /**
     * Returns a {@link PerspectiveCuboid} which is identical to this one except that the dimensions have
     * been expanded by {@code amount * 2} and the origin shifted left, down, and forward by amount.
     * Therefore the produced cuboid will retain the centerpoint of this one.
     */
    public PerspectiveCuboid padded(double amount) {
        final var newDimensions = getRelativeDimensions()
                .add(new Vector(amount * 2, amount * 2, amount * 2));

        final var shiftedOrigin = this
                .shiftRelative(Direction.LEFT, amount)
                .shiftRelative(Direction.DOWN, amount)
                .shiftRelative(Direction.FORWARD, amount)
                .getOrigin();

        return PerspectiveCuboid.ofRelativeDimensions(shiftedOrigin, newDimensions, getFrontFace());
    }

    public PerspectiveCuboid getWall(Side side) {
        final var d0 = side.getVariableAxis().get(0);
        final var d1 = side.getVariableAxis().get(1);

        final var pointARelative = new CartesianPoint(0, d0, 0, d1);
        final var pointBRelative = new CartesianPoint(get(d0, getRelativeDimensions()), d0, get(d1, getRelativeDimensions()), d1);
        final var cRelative = side.getConstantAxisValue(getRelativeDimensions());

        final var pointA = getAbsolutePosition(Vectors.of(pointARelative, side.getConstantAxis(), cRelative));
        final var pointB = getAbsolutePosition(Vectors.of(pointBRelative, side.getConstantAxis(), cRelative));
        return PerspectiveCuboid.fromAbsolutePosition(pointA, pointB, this.getFrontFace());
    }

    public PerspectiveCuboid shiftRelative(Direction direction, double magnitude) {
        return switch (direction) {
            case UP -> shiftRelative(zerosExcept(Axis.Y, magnitude));
            case DOWN -> shiftRelative(zerosExcept(Axis.Y, magnitude * -1));
            case LEFT -> shiftRelative(zerosExcept(Axis.X, magnitude * -1));
            case RIGHT -> shiftRelative(zerosExcept(Axis.X, magnitude));
            case BACK -> shiftRelative(zerosExcept(Axis.Z, magnitude));
            case FORWARD -> shiftRelative(zerosExcept(Axis.Z, magnitude * -1));
        };
    }

    public PerspectiveCuboid shiftRelative(Vector relativePos) {
        final var newOrigin = getOrigin();
        shift(newOrigin, opposite(leftFace()), relativePos.getX());
        shift(newOrigin, opposite(frontFace), relativePos.getZ());
        add(newOrigin, Axis.Y, relativePos.getY());
        return ofRelativeDimensions(newOrigin, getRelativeDimensions(), frontFace);
    }

    public WorldPerspectiveCuboid in(World world) {
        return new WorldPerspectiveCuboid(world, pointA, pointB, frontFace);
    }

    protected CuboidRegionSelection asSelection(World world) {
        return new CuboidRegionSelection(world, getOrigin(), calculateTopBackRightCorner(getOrigin(), frontFace, getRelativeDimensions()));
    }
}
