package com.duncpro.bukkit.region;

import com.duncpro.bukkit.region.selection.CuboidRegionSelection;

public interface BulkEditService {
    void eraseBlocks(CuboidRegionSelection region);
}
