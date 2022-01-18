package com.duncpro.bukkit.plugin;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.bukkit.Server;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BukkitServiceCustomInjection implements TypeListener {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        final var serviceInjectionFields = Arrays.stream(type.getRawType().getDeclaredFields())
                .filter(field -> BukkitServiceProvider.class.isAssignableFrom(field.getType()))
                .filter(field -> field.getGenericType() instanceof ParameterizedType)
                .filter(field -> field.isAnnotationPresent(BukkitService.class))
                .collect(Collectors.toSet());

        if (serviceInjectionFields.isEmpty()) return;

        final var bukkitServerProvider = encounter.getProvider(Server.class);

        for (final var field : serviceInjectionFields) {
            final var serviceType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

            if (!(serviceType instanceof Class<?>))
                throw new UnsupportedOperationException("Bukkit does not support parameterized services");

            field.trySetAccessible();

            encounter.register((MembersInjector<I>) instance -> {
                try {
                    field.set(instance, new BukkitServiceProvider((Class<?>) serviceType, bukkitServerProvider));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            });
        }
    }
}
