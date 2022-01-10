package com.duncpro.bukkit.region.selection;

import com.duncpro.bukkit.Materials;
import com.duncpro.bukkit.geometry.Side;
import org.bukkit.Axis;

import java.util.Arrays;
import java.util.Optional;

import static com.duncpro.bukkit.geometry.Vectors.*;

public class Selections {
    private static boolean isSideBlank(CuboidRegionSelection selection, Side side) {
        return side.blockPoints(selection.getDimensions())
                .map(point -> sum(point, selection.getMinimumPoint()))
                .map(point -> point.toLocation(selection.getWorld()).getBlock().getBlockData().getMaterial())
                .allMatch(Materials.AIR::contains);
    }

    /**
     * Returns a new {@link CuboidRegionSelection} which is identical to the given one, except that it has been reduced
     * in size by removing the given amount of voxel planes from the given side.
     */
    public static Optional<CuboidRegionSelection> shrink(CuboidRegionSelection original, Side side, int amount) {
        final var min = original.getMinimumPoint();
        final var max = original.getMaximumPoint();

        switch (side.isHighOrLow()) {
            case HIGH -> subtract(max, side.getConstantAxis(), amount);
            case LOW -> add(min, side.getConstantAxis(), amount);
        }

        final var isZeroed = Arrays.stream(Axis.values())
                .anyMatch(axis -> get(axis, min) > get(axis, max));

        return isZeroed ? Optional.empty() : Optional.of(new CuboidRegionSelection(original.getWorld(), min, max));
    }

    /**
     * Returns a new selection which is identical to the given one, except all empty sides have been trimmed.
     * In other words, the cuboid has been shrunk to the smallest size possible while still containing
     * all non-air blocks.
     */
    public static Optional<CuboidRegionSelection> trimSelection(CuboidRegionSelection original) {
        for (Side side : Side.values()) {
            while (isSideBlank(original, side)) {
                final var shrunk = shrink(original, side, 1);
                if (shrunk.isEmpty()) return Optional.empty();
                original = shrunk.get();
            }
        }
        return Optional.of(original);
    }
}
