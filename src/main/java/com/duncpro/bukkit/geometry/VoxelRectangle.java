package com.duncpro.bukkit.geometry;

import com.google.common.collect.MoreCollectors;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record VoxelRectangle(int length, int height) implements Serializable {

    public VoxelRectangle add(int length, int height) {
        return new VoxelRectangle(this.length + length, this.height + height);
    }

    /**
     * Returns a {@link Stream} of points, where each point represents a voxel within the rectangle.
     */
    public Stream<Map.Entry<Integer, Integer>> streamArea() {
        return IntStream.range(0, height * length)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i / height, i % length));
    }

    /**
     * Iterates over all the voxels which compose this rectangle.
     * This method is analogous to {@link #streamArea()}.
     */
    public void forEach(BiConsumer<Integer, Integer> consumer) {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < height; j++) {
                consumer.accept(i, j);
            }
        }
    }

    public <T> T[][] createMatrix(Class<T> elementType) {
        return (T[][]) Array.newInstance(elementType, length, height);
    }
}
