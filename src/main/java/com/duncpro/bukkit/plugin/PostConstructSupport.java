package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.misc.ThrowingRunnable;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

class PostConstructSupport implements TypeListener {
    private final Plugin plugin;

    PostConstructSupport(Plugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    private void invokeHandler(Method method, Object instance) {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "An unexpected error occurred while executing @PostConstruct " +
                    "handler.", e);
        }
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        encounter.register((InjectionListener<I>) injectee -> {
            for (final var method : injectee.getClass().getMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    invokeHandler(method, injectee);
                }
            }
        });
    }
}
