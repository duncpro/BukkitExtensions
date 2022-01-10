package com.duncpro.bukkit.region;

import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public interface WorldSnapshot {
    BlockData getBlockData(Vector position);

    static WorldSnapshot mock(World world) {
        return position -> world.getBlockData(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    static WorldSnapshot ofIntersectingChunks(CuboidRegionSelection region) {
        return new ChunkBasedWorldSnapshot(region);
    }
}
