package com.duncpro.bukkit.geometry;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VoxelCuboidTest {
    @Test
    void generateBlockOffsets() {
        int xBound = new Random().nextInt(30);
        int yBound = new Random().nextInt(30);
        int zBound = new Random().nextInt(30);


        final var offsets = VoxelCuboid.streamBlockPoints(new Vector(xBound, yBound, zBound))
                .collect(Collectors.toSet());

        for (int x = 0; x < xBound; x++) {
            for (int y = 0; y < yBound; y++) {
                for (int z = 0; z < zBound; z++) {
                    assertTrue(offsets.contains(new Vector(x, y, z)));
                }
            }
        }

        assertEquals(xBound * yBound * zBound, offsets.size());
    }
}
