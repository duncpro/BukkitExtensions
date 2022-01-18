package com.duncpro.bukkit.plugin;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

class PreDestroySupport implements TypeListener {
    private final Plugin plugin;

    PreDestroySupport(Plugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    private void invokeHandler(Method method, Object instance) {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while executing @PreDestroy " +
                    "handler.", e);
        }
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        final var lifecycleHooks = encounter.getProvider(LifecycleHooks.class);
        for (final var method : type.getRawType().getMethods()) {
            if (!method.isAnnotationPresent(PreDestroy.class)) return;
            encounter.register((InjectionListener<I>) injectee ->
                    lifecycleHooks.get().registerPreDestroyHook(() -> invokeHandler(method, injectee)));
        }
    }
}
