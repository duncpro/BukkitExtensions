package com.duncpro.bukkit.region.lock;

public interface CooperativeLock {
    boolean tryAcquire();

    void release();
}
