package com.duncpro.bukkit;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class WorldNotLoadedException extends Exception {
    private final UUID worldId;

    public WorldNotLoadedException(UUID worldId) {
        this.worldId = requireNonNull(worldId);
    }

    public UUID getWorldId() {
        return worldId;
    }
}
