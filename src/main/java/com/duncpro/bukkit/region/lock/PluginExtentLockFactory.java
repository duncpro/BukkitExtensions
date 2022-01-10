package com.duncpro.bukkit.region.lock;

import com.duncpro.bukkit.log.InjectLogger;
import com.duncpro.bukkit.plugin.BukkitServiceNotAvailableException;
import com.duncpro.bukkit.plugin.BukkitServiceProvider;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginExtentLockFactory {
    @Inject
    private Plugin plugin;

    @Inject
    private BukkitServiceProvider<ExtentLockService> lockService;

    @InjectLogger
    private Logger logger;

    public CooperativeLock retrieveLock(CuboidRegionSelection selection) {
        try {
            return lockService.get().retrieveLock(plugin, selection);
        } catch (BukkitServiceNotAvailableException e) {
            logger.log(Level.FINE,
                    "No locking service is available on this server. Lock will have no effect.", e);
            return new NoopCooperativeLock();
        }
    }

}
