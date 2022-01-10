package com.duncpro.bukkit.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoxelRotationTest {

    @Test
    void on() {
        assertEquals(CardinalDirection.WEST, VoxelRotation.degrees(90).on(CardinalDirection.NORTH));
        assertEquals(CardinalDirection.EAST, VoxelRotation.degrees(-90).on(CardinalDirection.NORTH));
        assertEquals(CardinalDirection.NORTH, VoxelRotation.degrees(-360).on(CardinalDirection.NORTH));
        assertEquals(CardinalDirection.NORTH, VoxelRotation.degrees(-360 * 2).on(CardinalDirection.NORTH));
    }

    @Test
    void of() {
        final var a = VoxelRotation.of(CardinalDirection.EAST, CardinalDirection.NORTH, RotationDirection.COUNTER_CLOCKWISE).toDegrees();
        assertEquals(90, a);

        final var b = VoxelRotation.of(CardinalDirection.EAST, CardinalDirection.NORTH, RotationDirection.CLOCKWISE).toDegrees();
        assertEquals(-270, b);
    }
}
