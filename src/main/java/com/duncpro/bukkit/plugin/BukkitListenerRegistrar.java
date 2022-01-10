package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.misc.ThrowingRunnable;
import com.google.inject.Scopes;
import com.google.inject.spi.ProvisionListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

class BukkitListenerRegistrar implements ProvisionListener {
    private final Plugin plugin;

    BukkitListenerRegistrar(Plugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        final var type = provision.getBinding().getKey().getTypeLiteral().getRawType();
        final var isSingleton = Scopes.isSingleton(provision.getBinding());

        if (!Listener.class.isAssignableFrom(type)) return;
        if (!isSingleton) throw new UnsupportedOperationException("Static Bukkit Listeners (those injected by Guice)" +
                " must be singleton scoped.");

        final var instance = (Listener) provision.provision();
        plugin.getLogger().log(Level.FINER, "Registering \"" + type.getName() + "\" as a Bukkit Event Listener.");
        plugin.getServer().getPluginManager().registerEvents(instance, plugin);
    }
}
