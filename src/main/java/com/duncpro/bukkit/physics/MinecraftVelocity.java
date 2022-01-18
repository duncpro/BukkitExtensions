package com.duncpro.bukkit.physics;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class MinecraftVelocity {
    /**
     * Calculates the velocity required to move an object the given {@code distance} (in blocks)
     * within the time (in ticks), represented by one unit of the given {@link TemporalUnit}.
     *
     * This value does not account for friction or gravity, and therefore must be applied continuously to the entity,
     * once each tick, in order for the object to reach the destination.
     */
    public static Vector of(Vector distance, TemporalUnit time) {
        final double tps = time.getDuration().toSeconds() * 20;
        return distance.divide(new Vector(tps, tps, tps));
    }
}
