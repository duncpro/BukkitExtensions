package com.duncpro.bukkit;

import com.duncpro.bukkit.geometry.CardinalDirection;
import com.duncpro.bukkit.geometry.Vectors;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.duncpro.bukkit.geometry.Vectors.of;
import static com.duncpro.bukkit.geometry.Vectors.sum;
import static java.util.function.Function.identity;

public class Blocks {
    public static Set<Vector> getCardinalAdjacentPos(Vector centerPos) {
        final var offsets = new HashSet<Vector>();

        final var up = new Vector(0, 1, 0);
        final var down = new Vector(0, -1, 0);
        offsets.add(up);
        offsets.add(down);

        Arrays.stream(CardinalDirection.values())
                .map(CardinalDirection::offset)
                .forEach(offsets::add);

        return offsets.stream()
                .map(offset -> sum(centerPos, offset))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Vector> getDiagonalAdjacentPos(Vector centerPos) {
        final var xs = Set.of(CardinalDirection.WEST.offset(), CardinalDirection.EAST.offset());
        final var ys = Set.of(new Vector(0, 1, 0), new Vector(0, -1, 0));
        final var zs = Set.of(CardinalDirection.NORTH.offset(), CardinalDirection.SOUTH.offset());

        final var adjacent = new HashSet<Vector>();

        for (Vector x : xs) {
            for (Vector y : ys) {
                for (Vector z : zs) {
                    final var composite = new Vector();
                    composite.add(x);
                    composite.add(y);
                    composite.add(z);
                    composite.add(centerPos);
                    adjacent.add(composite);
                }
            }
        }

        return adjacent;
    }

    public static Set<Vector> getAdjacentPos(Vector centerPos) {
        return Stream.of(getDiagonalAdjacentPos(centerPos), getCardinalAdjacentPos(centerPos))
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Block> getAdjacent(Block center) {
        return getAdjacentPos(center.getLocation().toVector()).stream()
                .map(pos -> pos.toLocation(center.getWorld()).getBlock())
                .collect(Collectors.toUnmodifiableSet());
    }
}
