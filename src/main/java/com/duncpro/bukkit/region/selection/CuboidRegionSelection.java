package com.duncpro.bukkit.region.selection;

import com.duncpro.bukkit.geometry.WorldPerspectiveCuboid;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.duncpro.bukkit.region.Chunks.chunkId;
import static com.duncpro.bukkit.geometry.Vectors.*;
import static com.duncpro.bukkit.geometry.Vectors.mono;
import static java.util.Objects.requireNonNull;

/**
 * A general purpose cuboid section within a world containing at least one block.
 * This class supports decimal dimensions, with a minimum mangitude of 1 for each dimension.
 * In other words it is impossible to have a {@link CuboidRegionSelection} of zero blocks.
 * For selections which have a logical front/back/left/right consider using {@link WorldPerspectiveCuboid} instead.
 *
 */
public class CuboidRegionSelection implements RegionSelection {
    private final World world;
    private final Vector primaryPosition;
    private final Vector secondaryPosition;

    public CuboidRegionSelection(World world, Vector primaryPosition, Vector secondaryPosition) {
        this.world = requireNonNull(world);
        this.primaryPosition = requireNonNull(primaryPosition).clone();
        this.secondaryPosition = requireNonNull(secondaryPosition).clone();
    }

    public static CuboidRegionSelection ofBlock(Block block) {
        return new CuboidRegionSelection(block.getWorld(), block.getLocation().toVector(), block.getLocation().toVector());
    }

    @Override
    public Vector getPrimaryPoint() {
        return primaryPosition.clone();
    }

    @Override
    public World getWorld() {
        return world;
    }

    public Vector getSecondaryPoint() {
        return secondaryPosition.clone();
    }

    public Vector getDimensions() {
        final var delta = abs(difference(primaryPosition, secondaryPosition));
        return sum(delta, mono(1));
    }

    public int getBlockHeight() {
        return getDimensions().getBlockY();
    }

    public Vector getMinimumPoint() {
        final var x = Math.min(primaryPosition.getX(), secondaryPosition.getX());
        final var y = Math.min(primaryPosition.getY(), secondaryPosition.getY());
        final var z = Math.min(primaryPosition.getZ(), secondaryPosition.getZ());
        return new Vector(x, y, z);
    }

    public Vector getMaximumPoint() {
        final var x = Math.max(primaryPosition.getX(), secondaryPosition.getX());
        final var y = Math.max(primaryPosition.getY(), secondaryPosition.getY());
        final var z = Math.max(primaryPosition.getZ(), secondaryPosition.getZ());
        return new Vector(x, y, z);
    }

    public Set<Chunk> getChunks() {
        // Start from one side of the cartesian plane
        // Get the chunk at that side, add 16, get the chunk again
        final var chunks = new HashSet<Chunk>();

        // TODO: Optimize
        for (int x = getMinimumPoint().getBlockX(); x <= getMaximumPoint().getBlockX(); x++) {
            for (int z = getMinimumPoint().getBlockZ(); z <= getMaximumPoint().getBlockZ(); z++) {
                chunks.add(new Vector(x, 0, z).toLocation(world).getChunk());
            }
        }

        return chunks;
    }

    /**
     * Determines whether or not the given point exists within this {@link CuboidRegionSelection}.
     * Can be used to determine if a given block falls within a region.
     */
    public boolean includes(Vector point) {
        final boolean x = point.getX() >= this.getMinimumPoint().getX() && point.getX() <= this.getMaximumPoint().getX();
        final boolean y = point.getY() >= this.getMinimumPoint().getY() && point.getY() <= this.getMaximumPoint().getY();
        final boolean z = point.getZ() >= this.getMinimumPoint().getZ() && point.getZ() <= this.getMaximumPoint().getZ();
        return x && y && z;
    }

    /**
     * Performs collision detection on this {@link CuboidRegionSelection} and the given {@link CuboidRegionSelection}.
     * If the two selections overlap, true is returned, otherwise false.
     */
    public boolean collides(CuboidRegionSelection selection) {
        final var a = selection.getCorners().stream().anyMatch(this::includes);
        final var b = this.getCorners().stream().anyMatch(selection::includes);
        return a || b;
    }

    public Set<Vector> getCorners() {
        final var corners = new HashSet<Vector>();

        final var min = getMinimumPoint();
        final var max = getMaximumPoint();

        corners.add(min);
        corners.add(new Vector(min.getX(), max.getY(), min.getZ()));

        corners.add(max);
        corners.add(new Vector(max.getX(), min.getY(), max.getZ()));

        return corners;
    }

    public CuboidRegionSelection spanningWorldHeight() {
        final var newMinimum = getMinimumPoint();
        final var newMaximum = getMaximumPoint();

        newMinimum.setY(world.getMinHeight());
        newMaximum.setY(world.getMaxHeight());

        return new CuboidRegionSelection(world, newMinimum, newMaximum);
    }

    /**
     * Returns a new {@link CuboidRegionSelection} that has been snapped to the Minecraft world grid.
     * In other words, the dimensions of the returned cuboid will be whole numbers. In general this method
     * should always be invoked before comparing the positions of whole blocks using methods such as
     * {@link #includes(Vector)} and {@link #collides(CuboidRegionSelection)}.
     */
    public CuboidRegionSelection snapToGrid() {
        final var min = getMinimumPoint();
        final var max = getMaximumPoint();
        return new CuboidRegionSelection(world,
                new Vector(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                new Vector(max.getBlockX(), max.getBlockY(), max.getBlockZ())
        );
    }

    public BoundingBox asBoundingBox() {
        return BoundingBox.of(getMinimumPoint(), getMaximumPoint()).expand(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CuboidRegionSelection)) return false;
        CuboidRegionSelection that = (CuboidRegionSelection) o;
        return world.equals(that.world) && primaryPosition.equals(that.primaryPosition) && secondaryPosition.equals(that.secondaryPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, primaryPosition, secondaryPosition);
    }

    @Override
    public String toString() {
        return "CuboidRegionSelection{" +
                "world=" + world +
                ", primaryPosition=" + primaryPosition +
                ", secondaryPosition=" + secondaryPosition +
                '}';
    }
}
