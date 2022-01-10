package com.duncpro.bukkit.structure;

import com.duncpro.bukkit.Materials;
import com.duncpro.bukkit.geometry.CardinalDirection;
import com.duncpro.bukkit.geometry.VoxelCuboid;
import com.duncpro.bukkit.geometry.WorldPerspectiveCuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.duncpro.bukkit.geometry.Vectors.shift;

/**
 * A schematic with a logical frontside, backside, leftside, rightside, topside, and bottomside.
 * The schematic is indexed where x = 0 corresponds to the leftmost blocks, z = 0 corresponds
 * to the frontmost blocks, and y = 0 corresponds to the bottommost blocks.
 */
public abstract class LogicallyOrientedSchematic {
    public abstract CompletableFuture<Void> save(File file);

    public abstract WorldPerspectiveCuboid paste(Location origin, CardinalDirection facing, boolean pasteAirBlocks);

    public final WorldPerspectiveCuboid pasteCentered2d(Location around, CardinalDirection facing, boolean pasteAirBlocks) {
        final var offsetOrigin = around.clone();
        shift(offsetOrigin, facing.left(), getLength() / 2.0);
        shift(offsetOrigin, facing, getDepth() / 2.0);
        return paste(offsetOrigin, facing, pasteAirBlocks);
    }

    public abstract BlockData getBlockData(Vector at);

    public abstract int getLength();

    public abstract int getHeight();

    public abstract int getDepth();

    public abstract Vector getDimensions();

    private final Lock lock = new ReentrantLock();
    private volatile Set<Vector> collisionMask = null;

    public Set<Vector> getCollisionFilter() {
        lock.lock();
        if (collisionMask == null) {
            collisionMask = VoxelCuboid.streamBlockPoints(this.getDimensions())
                    .filter(offset -> !Materials.AIR.contains(this.getBlockData(offset).getMaterial()))
                    .collect(Collectors.toUnmodifiableSet());
        }
        lock.unlock();
        return collisionMask;
    }
}
