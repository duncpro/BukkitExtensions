package com.duncpro.bukkit;

import com.duncpro.bukkit.concurrency.BukkitThreadUnsafe;
import org.bukkit.World;

import static java.util.Objects.requireNonNull;

public record WorldDetails (int minHeight, int maxHeight) {
    @BukkitThreadUnsafe
    public static WorldDetails of(World world) {
        return new WorldDetails(world.getMinHeight(), world.getMaxHeight());
    }
}