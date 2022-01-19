package com.duncpro.bukkit;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import javax.swing.text.html.Option;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class BlockSearchResult {
    private final ZonedDateTime observedAt;
    private final Block block;
    private final BlockData blockData;
    private final boolean isLast;

    BlockSearchResult(ZonedDateTime observedAt, Block block, BlockData blockData, boolean isLast) {
        this.observedAt = requireNonNull(observedAt);
        this.block = block;
        this.blockData = blockData;
        this.isLast = isLast;
    }

    public ZonedDateTime getObservedAt() {
        return observedAt;
    }

    public Optional<Block> getBlock() {
        return Optional.ofNullable(block);
    }

    public Optional<BlockData> getBlockData() {
        return Optional.ofNullable(blockData);
    }

    public boolean isLast() {
        return isLast;
    }
}