package com.duncpro.bukkit.region.lock;

import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

class ExtentLock implements CooperativeLock {
    final Plugin owner;
    private final CuboidRegionSelection extent;
    private final ExtentLockServiceImpl coordinator;
    private boolean isAcquired = false;

    ExtentLock(Plugin owner, CuboidRegionSelection extent, ExtentLockServiceImpl coordinator) {
        this.owner = requireNonNull(owner);
        this.extent = requireNonNull(extent);
        this.coordinator = requireNonNull(coordinator);
    }

    @Override
    public boolean tryAcquire() {
        final var isAlreadyAcquired = coordinator.acquiredLocks.stream()
                .anyMatch(acquired -> acquired.collides(extent));

        if (isAlreadyAcquired) {
            return false;
        }

        coordinator.logger.log(Level.FINE, "Acquired lock on: " + extent + " with dimensions: " + extent.getDimensions());

        coordinator.acquiredLocks.add(extent);
        coordinator.pluginLocks.computeIfAbsent(owner, $ -> new HashSet<>()).add(extent);
        isAcquired = true;
        return true;
    }

    @Override
    public void release() {
        if (!isAcquired) return;
        coordinator.acquiredLocks.remove(extent);
        coordinator.pluginLocks.getOrDefault(owner, new HashSet<>()).remove(extent);
        coordinator.logger.log(Level.FINE, "Released lock on: " + extent + " with dimensions: " + extent.getDimensions());
    }
}
