package com.duncpro.bukkit.region;

import com.duncpro.bukkit.geometry.HorizontalPosition;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.stream.Collectors;

class ChunkBasedWorldSnapshot implements WorldSnapshot {
    private final Map<HorizontalPosition, ChunkSnapshot> chunks;

    ChunkBasedWorldSnapshot(CuboidRegionSelection region) {
        chunks = region.getChunks().stream()
                .collect(Collectors.toUnmodifiableMap(Chunks::chunkId, Chunk::getChunkSnapshot));
    }

    @Override
    public BlockData getBlockData(Vector position) {
        final var chunkSnapshot = chunks.get(Chunks.chunkId(position));
        if (chunkSnapshot == null) throw new IndexOutOfBoundsException();
        final var positionInChunk = Chunks.innerBlockOffset(position);
        return chunkSnapshot.getBlockData(positionInChunk.getBlockX(), positionInChunk.getBlockY(), positionInChunk.getBlockZ());
    }
}
