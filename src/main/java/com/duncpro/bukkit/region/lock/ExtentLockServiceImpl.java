package com.duncpro.bukkit.region.lock;

import com.duncpro.bukkit.log.InjectLogger;
import com.duncpro.bukkit.plugin.BukkitServiceImpl;
import com.duncpro.bukkit.plugin.PreDestroy;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@BukkitServiceImpl(service = ExtentLockService.class, priority = ServicePriority.Normal)
public class ExtentLockServiceImpl implements ExtentLockService, Listener {
    final Set<CuboidRegionSelection> acquiredLocks = new HashSet<>();
    final Map<Plugin, Set<CuboidRegionSelection>> pluginLocks = new HashMap<>();

    @InjectLogger
    Logger logger;

    @Override
    public CooperativeLock retrieveLock(Plugin owner, CuboidRegionSelection extent) {
        return new ExtentLock(owner, extent, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        boolean isLocked = acquiredLocks.stream()
                .anyMatch(region -> region.snapToGrid().includes(event.getBlock().getLocation().toVector()));

        if (isLocked) {
            event.setCancelled(true);
            logger.fine("Preventing: " + event.getPlayer().getName() + " from building because this region" +
                    " is temporarily locked by a plugin.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockPlaceEvent event) {
        boolean isLocked = acquiredLocks.stream()
                .anyMatch(region -> region.snapToGrid().includes(event.getBlock().getLocation().toVector()));

        if (isLocked) {
            event.setCancelled(true);
            logger.fine("Preventing: " + event.getPlayer().getName() + " from building because this region" +
                    " is temporarily locked by a plugin.");
        }
    }

    @PreDestroy
    private void onOrchestratorDisable() {
        pluginLocks.clear();
        acquiredLocks.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        final var locksForThisPlugin = pluginLocks.remove(event.getPlugin());
        if (locksForThisPlugin == null) return;
        locksForThisPlugin.forEach(acquiredLocks::remove);
        locksForThisPlugin.clear();
    }
}
