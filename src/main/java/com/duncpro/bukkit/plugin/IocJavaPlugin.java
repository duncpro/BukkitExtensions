package com.duncpro.bukkit.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class IocJavaPlugin extends JavaPlugin {
    @Override
    public final void onEnable() {
        final var modules = new HashSet<Module>();
        modules.add(new BukkitPluginSupportModule<>(this));
        modules.addAll(createModules());
        Guice.createInjector(modules).injectMembers(this);
    }


    @Inject
    private LifecycleHooks lifecycleHooks;

    @Override
    public final void onDisable() {
        lifecycleHooks.runPreDestroyHooks();
        injector = null;
    }

    protected abstract Set<Module> createModules();

    @Inject
    private Injector injector;

    public final Injector getInjector() {
        if (injector == null) throw new IllegalPluginAccessException("Plugin not enabled");
        return injector;
    }
}
