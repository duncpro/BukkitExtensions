package com.duncpro.bukkit.region.lock;

public class NoopCooperativeLock implements CooperativeLock {
    @Override
    public boolean tryAcquire() {
        return true;
    }

    @Override
    public void release() {}
}
