package com.duncpro.bukkit.log;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public class PluginLoggerGuiceTypeListener implements TypeListener {
    private final Plugin plugin;

    public PluginLoggerGuiceTypeListener(Plugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class &&
                        field.isAnnotationPresent(InjectLogger.class)) {
                    encounter.register(new PluginLoggerMembersInjector<I>(plugin, field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
