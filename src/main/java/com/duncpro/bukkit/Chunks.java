package com.duncpro.bukkit;

import org.bukkit.util.Vector;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.pow;


public class Chunks {
    public static final int CHUNK_HORIZONTAL_AXIS_LENGTH = 16;

    public static Vector chunkRelativePosition(Vector positionInWorldBlocks) {
        var x = positionInWorldBlocks.getX() % CHUNK_HORIZONTAL_AXIS_LENGTH;
        if (x < 0) x = (x + CHUNK_HORIZONTAL_AXIS_LENGTH);
        final var y = positionInWorldBlocks.getY();
        var z = positionInWorldBlocks.getZ() % CHUNK_HORIZONTAL_AXIS_LENGTH;
        if (z < 0) z = (z + CHUNK_HORIZONTAL_AXIS_LENGTH);
        return new Vector(x, y, z);
    }

    public static Stream<Vector> streamCrossSectionAtY(int y) {
        return IntStream.range(0, (int) pow(CHUNK_HORIZONTAL_AXIS_LENGTH, 2))
                .mapToObj(i -> new Vector(i / CHUNK_HORIZONTAL_AXIS_LENGTH, y, i % CHUNK_HORIZONTAL_AXIS_LENGTH));
    }
}
