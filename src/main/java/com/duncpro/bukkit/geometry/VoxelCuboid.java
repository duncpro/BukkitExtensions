package com.duncpro.bukkit.geometry;

import org.bukkit.util.Vector;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;

public class VoxelCuboid {
    /**
     * Lazily generates a stream of {@link Vector}s, one for each block within a 3 dimensional cuboid of blocks.
     * Each axis is indexed 0 to axisLength exclusive. Generating such a stream can be computationally expensive,
     * especially for larger cuboids. Therefore the caller is advised to cache the output of this function
     * instead of making repeated calls.
     */
    public static Stream<Vector> streamBlockPoints(int xAxisLength, int yAxisLength, int zAxisLength) {
        final Function<Integer, Vector> oneDToThreeD = i -> {
            int xOffset = i % xAxisLength;
            int yOffset = (i / (xAxisLength * zAxisLength));
            int zOffset = (i / xAxisLength) - (yOffset * zAxisLength);

            return new Vector(xOffset, yOffset, zOffset);
        };

        return range(0, xAxisLength * yAxisLength * zAxisLength)
                .mapToObj(oneDToThreeD::apply);
    }

    public static Stream<Vector> streamBlockPoints(Vector vector) {
        return streamBlockPoints(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static Stream<Vector> streamSideBlockPoints(Side side, Vector dimensions) {
        return side.blockPoints(dimensions);
    }
}
