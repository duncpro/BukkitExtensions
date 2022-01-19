package com.duncpro.bukkit.plugin;

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
        for (final var method : type.getRawType().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PostConstruct.class)) return;
            method.trySetAccessible();
            encounter.register((InjectionListener<I>) injectee -> {
                invokeHandler(method, injectee);
            });
        }
    }
}
