package com.duncpro.bukkit.plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Stack;

import static java.util.Objects.requireNonNull;

public class LifecycleHooks {
    private final Stack<Runnable> preDestroyHooks = new Stack<>();

    public void runPreDestroyHooks() {
        while (!preDestroyHooks.empty()) {
            preDestroyHooks.pop().run();
        }
    }

    void registerPreDestroyHook(Runnable runnable) {
        preDestroyHooks.push(runnable);
    }
}
