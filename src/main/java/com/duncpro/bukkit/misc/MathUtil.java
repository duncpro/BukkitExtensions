package com.duncpro.bukkit.misc;

import java.util.Comparator;
import java.util.Set;

import static java.lang.Math.abs;

public class MathUtil {
    public static float roundToNearest(float valueToRound, Set<Float> markerValues) {
        return markerValues.stream()
                .min(Comparator.comparing(marker -> abs(valueToRound - marker)))
                .orElseThrow(() -> new IllegalArgumentException("No marker values given"));
    }

    public static double roundToNearest(double valueToRound, Set<Double> markerValues) {
        return markerValues.stream()
                .min(Comparator.comparing(marker -> abs(valueToRound - marker)))
                .orElseThrow(() -> new IllegalArgumentException("No marker values given"));
    }
}
