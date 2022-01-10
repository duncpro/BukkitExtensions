package com.duncpro.bukkit.region.selection;

import org.bukkit.World;
import org.bukkit.util.Vector;

public interface RegionSelection {
    Vector getPrimaryPoint();

    World getWorld();
}
