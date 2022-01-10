package com.duncpro.bukkit.entity;

import org.bukkit.entity.Entity;

/**
 * Bukkit's {@link Entity#remove()} cannot be reliably used to remove entities from
 * the world before a server restart occurs. This class exposes a dependable mechanism for
 * preventing entities from crossing server restart boundaries.
 */
public interface TemporaryEntityService {
    /**
     * Prevent the given entity from being reloaded from disk.
     */
    void setTemporary(Entity entity);
}
