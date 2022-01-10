package com.duncpro.bukkit.log;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public class PluginLoggerMembersInjector<T> implements MembersInjector<T> {
    private final Plugin plugin;
    private final Field field;

    public PluginLoggerMembersInjector(Plugin plugin, Field field) {
        this.plugin = plugin;
        this.field = requireNonNull(field);
    }

    public void injectMembers(T t) {
        final var logger = Logger.getLogger(field.getDeclaringClass().getName());
        logger.setParent(plugin.getLogger());
        logger.setUseParentHandlers(true);
        logger.setLevel(Level.ALL);
        field.setAccessible(true);

        try {
            field.set(t, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
