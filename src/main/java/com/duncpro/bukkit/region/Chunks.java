package com.duncpro.bukkit.region;

import com.duncpro.bukkit.geometry.HorizontalPosition;
import com.duncpro.bukkit.geometry.VoxelCuboid;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.duncpro.bukkit.geometry.HorizontalPosition.*;
import static com.duncpro.bukkit.geometry.VoxelCuboid.streamBlockPoints;
import static java.lang.Math.abs;

public class Chunks {
    public static int CHUNK_HORIZONTAL_AXIS_LENGTH = 16;

    /*
     * Since Minecraft can generate worlds with a few different heights, it is not sufficient to have a single
     * cached block offset array. Therefore this mapping of world heights to block offsets is used.
     */
    private static final Map<Integer, Set<Vector>> innerBlockOffsetsCache = new ConcurrentHashMap<>();

    public static Stream<Vector> innerBlockOffsets(int yAxisLength) {
        final Function<Integer, Set<Vector>> generator = $ ->
                VoxelCuboid.streamBlockPoints(CHUNK_HORIZONTAL_AXIS_LENGTH, yAxisLength, CHUNK_HORIZONTAL_AXIS_LENGTH)
                        .collect(Collectors.toUnmodifiableSet());

        return innerBlockOffsetsCache.computeIfAbsent(yAxisLength, generator)
                .stream()
                .map(Vector::clone);
    }

    public static Stream<Vector> innerBlockOffsets(Chunk chunk) {
        return innerBlockOffsets(height(chunk));
    }

    public static Vector innerBlockOffset(Vector positionInWorldBlocks) {
        var x = positionInWorldBlocks.getX() % CHUNK_HORIZONTAL_AXIS_LENGTH;
        if (x < 0) x = (x + CHUNK_HORIZONTAL_AXIS_LENGTH);
        final var y = positionInWorldBlocks.getY();
        var z = positionInWorldBlocks.getZ() % CHUNK_HORIZONTAL_AXIS_LENGTH;
        if (z < 0) z = (z + CHUNK_HORIZONTAL_AXIS_LENGTH);
        return new Vector(x, y, z);
    }

    public static Vector innerBlockOffset(Location positionInWorldBlocks) {
        return innerBlockOffset(positionInWorldBlocks.toVector());
    }

    private static boolean isValidHorizontalBlockOffset(Vector vector) {
        if (vector.getX() < 0 || vector.getX() >= CHUNK_HORIZONTAL_AXIS_LENGTH) return false;
        if (vector.getZ() < 0 || vector.getZ() >= CHUNK_HORIZONTAL_AXIS_LENGTH) return false;
        return true;
    }

    public static boolean isValidInnerBlockOffset(Vector vector, World inWorld) {
        if (!isValidHorizontalBlockOffset(vector)) return false;
        return vector.getY() >= inWorld.getMinHeight() && vector.getY() < inWorld.getMaxHeight();
    }

    /**
     * Chunks are not positioned using the same unit of measure as blocks. This function returns the "chunk coordinates"
     * of the given {@link Chunk}. Block coordinates can be converted to chunk coordinates by dividing by a factor of
     * {@link Chunks#CHUNK_HORIZONTAL_AXIS_LENGTH}. Chunks coordinates can be converted block coordinates
     * by multiplying by {@link Chunks#CHUNK_HORIZONTAL_AXIS_LENGTH}.
     */
    public static HorizontalPosition chunkId(Chunk chunk) {
        return new HorizontalPosition(chunk.getX(), chunk.getZ());
    }

    public static HorizontalPosition chunkId(HorizontalPosition blockPos) {
        return floor(quotient(floor(blockPos), CHUNK_HORIZONTAL_AXIS_LENGTH));
    }

    public static HorizontalPosition chunkId(Vector blockPos) {
        return chunkId(HorizontalPosition.of(blockPos));
    }

    public static HorizontalPosition blockCoordinates(Chunk chunk) {
        return product(chunkId(chunk), CHUNK_HORIZONTAL_AXIS_LENGTH);
    }

    public static Block blockAt(Chunk chunk, Vector offset) {
        return chunk.getBlock(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ());
    }

    public static int height(World world) {
        return abs(world.getMinHeight() - world.getMaxHeight());
    }

    public static int height(Chunk chunk) {
        return height(chunk.getWorld());
    }

}
