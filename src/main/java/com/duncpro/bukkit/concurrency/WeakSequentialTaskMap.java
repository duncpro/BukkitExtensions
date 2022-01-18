package com.duncpro.bukkit.concurrency;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * This class provides a mechanism for running a sequence of tasks on some {@link Executor}, where each sequence
 * is unique on some key of type {@link K}. This is a weak map, meaning, once no references to the key remains,
 * the key will be removed from the map. A reference to the key is stored with each task, therefore, while a task
 * is in the queue, the sequence will never be purged from the map.
 */
@ThreadSafe
public class WeakSequentialTaskMap<K> {
    private final Map<K, CompletableFuture<Void>> sequences = Collections.synchronizedMap(new WeakHashMap<>());

    private final Executor executor;

    public WeakSequentialTaskMap(Executor executor) {
        this.executor = requireNonNull(executor);
    }

    public synchronized CompletableFuture<Void> queueTask(K key, Consumer<K> action) {
        final Runnable task = () -> action.accept(key);
        return sequences.compute(key, ($, prev) ->
            prev == null ? CompletableFuture.runAsync(task, executor) :
                    prev.thenRunAsync(task, executor)
        );
    }
}
