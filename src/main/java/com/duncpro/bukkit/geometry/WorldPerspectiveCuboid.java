package com.duncpro.bukkit.geometry;

import com.duncpro.bukkit.region.WorldSnapshot;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public class WorldPerspectiveCuboid extends PerspectiveCuboid {
    private final World world;

    public WorldPerspectiveCuboid(World world, Vector pointA, Vector pointB, CardinalDirection front) {
        super(pointA, pointB, front);
        this.world = requireNonNull(world);
    }

    public WorldPerspectiveCuboid(CuboidRegionSelection selection, CardinalDirection front) {
        this(selection.getWorld(), selection.getPrimaryPoint(), selection.getSecondaryPoint(), front);
    }

    public Block getBlockAt(Vector relativePosition) {
        return this.getAbsolutePosition(relativePosition).toLocation(world).getBlock();
    }

    /**
     * Returns true if none of the blocks IN THIS REGION, are occupied within the world associated with this
     * {@link WorldPerspectiveCuboid}.
     */
    public boolean checkOccupancy() {
        return super.checkOccupancy(WorldSnapshot.mock(world));
    }

    /**
     * Returns true if none of the GIVEN blocks, are occupied within the world associated with this
     * {@link WorldPerspectiveCuboid}.
     */
    public boolean checkOccupancy(Set<Vector> filter) {
        return super.checkOccupancy(WorldSnapshot.mock(world), filter);
    }

    @Override
    public WorldPerspectiveCuboid padded(double amount) {
        return super.padded(amount).in(world);
    }

    @Override
    public WorldPerspectiveCuboid shiftRelative(Direction direction, double magnitude) {
        return super.shiftRelative(direction, magnitude).in(world);
    }

    @Override
    public WorldPerspectiveCuboid getWall(Side side) {
        return super.getWall(side).in(world);
    }

    public World getWorld() {
        return world;
    }

    /**
     * Returns a {@link CuboidRegionSelection} representing this {@link WorldPerspectiveCuboid}.
     * The primary point in the selection will be set to the frontmost, bottommost, leftmost, point
     * in this {@link WorldPerspectiveCuboid}, and the secondary point will be set to the topmost, backmost,
     * rightmost, point.
     */
    public CuboidRegionSelection asSelection() {
        return this.asSelection(world);
    }
}
