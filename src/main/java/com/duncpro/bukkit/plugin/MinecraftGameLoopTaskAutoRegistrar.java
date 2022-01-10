package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.misc.ThrowingRunnable;
import com.google.inject.Scopes;
import com.google.inject.spi.ProvisionListener;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

class MinecraftGameLoopTaskAutoRegistrar implements ProvisionListener {
    private final Plugin plugin;

    public MinecraftGameLoopTaskAutoRegistrar(Plugin plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        final var type = provision.getBinding().getKey().getTypeLiteral().getRawType();
        final var isSingleton = Scopes.isSingleton(provision.getBinding());

        if (!Runnable.class.isAssignableFrom(type)) return;
        if (!type.isAnnotationPresent(MinecraftGameLoopTask.class)) return;
        if (!isSingleton) throw new UnsupportedOperationException();

        final var annotation = type.getAnnotation(MinecraftGameLoopTask.class);
        final var instance = (Runnable) provision.provision();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, instance,
                0, annotation.period());
        plugin.getLogger().log(Level.FINER, "Registered class \"" + type.getName() + "\" as a Minecraft Game Loop" +
                "  task with period = " + annotation.period() + " ticks.");
    }
}
