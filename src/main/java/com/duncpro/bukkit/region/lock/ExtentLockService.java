package com.duncpro.bukkit.region.lock;

import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.plugin.Plugin;

public interface ExtentLockService {
    /**
     * Note that this method does not "acquire" the lock, only retrieves it.
     */
    CooperativeLock retrieveLock(Plugin owner, CuboidRegionSelection cuboid);
}
