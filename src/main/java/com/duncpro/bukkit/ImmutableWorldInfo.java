package com.duncpro.bukkit;

import com.duncpro.bukkit.concurrency.BukkitThreadUnsafe;
import org.bukkit.World;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record ImmutableWorldInfo (
        String name,
        int minHeight,
        int maxHeight,
        UUID id,
        long seed,
        World.Environment environment

) implements WorldInfo {
    @BukkitThreadUnsafe
    public static ImmutableWorldInfo copyOf(WorldInfo world) {
        return new ImmutableWorldInfo(
                world.getName(),
                world.getMinHeight(),
                world.getMaxHeight(),
                world.getUID(),
                world.getSeed(),
                world.getEnvironment());
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public UUID getUID() {
        return id;
    }

    @NotNull
    @Override
    public World.Environment getEnvironment() {
        return environment;
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public int getMinHeight() {
        return minHeight;
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }
}