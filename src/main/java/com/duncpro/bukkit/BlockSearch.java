package com.duncpro.bukkit;

import com.duncpro.bukkit.concurrency.BukkitThreadPool;
import com.duncpro.bukkit.concurrency.BukkitThreadSafe;
import com.duncpro.bukkit.concurrency.BukkitThreadUnsafe;
import com.duncpro.bukkit.concurrency.NextTickSync;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class BlockSearch {
    private Executor minecraftThread;
    private Executor asyncExecutor;

    @Inject
    public BlockSearch(@NextTickSync Executor minecraftThread, @BukkitThreadPool Executor asyncExecutor) {
        this.minecraftThread = requireNonNull(minecraftThread);
        this.asyncExecutor = requireNonNull(asyncExecutor);
    }

    public BlockSearchResult getHighestNonAirOffThread(World world, int x, int z) {
        return supplyAsync(() -> getHighestNonAir(world, x, z), minecraftThread).join().join();
    }

    @BukkitThreadUnsafe
    public CompletableFuture<BlockSearchResult> getHighestNonAir(World world, int x, int z) {
        final var maxY = world.getMaxHeight();
        final var minY = world.getMinHeight();

        final BiFunction<Vector, Boolean, Supplier<BlockSearchResult>> blockAccessor = (pos, isLast) -> () -> {
            final var block = world.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
            return new BlockSearchResult(ZonedDateTime.now(), block, block.getBlockData(), isLast);
        };

        return supplyAsync(() -> {
            for (int y = maxY; y >= minY; y--) {
                final var block = supplyAsync(blockAccessor.apply(new Vector(x, y, z), y == minY), minecraftThread).join();
                final var material = block.getBlockData().map(BlockData::getMaterial).orElseThrow();
                if (!Materials.AIR.contains(material) || block.isLast()) return block;
            }
            throw new IllegalStateException();
        }, asyncExecutor);
    }
}
