package com.duncpro.bukkit.concurrency;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CancellationException;

import static java.util.Objects.requireNonNull;

class BukkitRunnables {
    public static BukkitRunnable cooperativelyCancelling(Runnable taskConsumer) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    taskConsumer.run();
                } catch (CancellationException e) {
                    return;
                }
            }
        };
    }
}
