package com.duncpro.bukkit.plugin;

import com.google.inject.Scopes;
import com.google.inject.spi.ProvisionListener;
import org.bukkit.plugin.Plugin;

import static java.util.Objects.requireNonNull;

class BukkitServiceImplAutoRegistrar implements ProvisionListener {
    private final Plugin plugin;

    BukkitServiceImplAutoRegistrar(Plugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        final var type = provision.getBinding().getKey().getTypeLiteral().getRawType();

        if (!type.isAnnotationPresent(BukkitServiceImpl.class)) return;
        final var annotation = type.getAnnotation(BukkitServiceImpl.class);

        if (!Scopes.isSingleton(provision.getBinding())) {
            throw new IllegalStateException("Third-party Bukkit services must be bound as singletons.");
        }

        if (!annotation.service().isAssignableFrom(type)) throw new IllegalStateException();

        final var instance = provision.provision();
        final Class service = annotation.service();
        plugin.getServer().getServicesManager().register(service, instance, plugin, annotation.priority());
        plugin.getLogger().finer("This plugin is implementing the third-party service: " + service.getName() + "" +
                " with: " + instance.getClass().getName());
    }
}
