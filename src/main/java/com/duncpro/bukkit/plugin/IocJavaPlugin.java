package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.entity.TemporaryEntityService;
import com.duncpro.bukkit.geometry.PlayerHeadingService;
import com.duncpro.bukkit.item.ItemGlowService;
import com.duncpro.bukkit.region.lock.ExtentLockService;
import com.duncpro.bukkit.log.AsyncFileHandler;
import com.duncpro.bukkit.region.BulkEditService;
import com.duncpro.bukkit.region.selection.RegionSelectionService;
import com.duncpro.bukkit.structure.StructurePersistenceService;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import static com.google.inject.util.Types.newParameterizedType;

/**
 * Extension to Bukkit's {@link JavaPlugin} which supports separation of concerns and inversion of control.
 * This class is especially useful for larger plugins with many different features. A plugin should
 * extend this class instead of {@link JavaPlugin}.
 */
public abstract class IocJavaPlugin extends JavaPlugin {
    private BukkitPluginSupportModule<IocJavaPlugin> bukkitPluginSupportModule;
    private AsyncFileHandler asyncLogger;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Module getThirdPartyServiceModule() {
        final Set<Class<?>> serviceDependencies = new HashSet<>();
        serviceDependencies.add(RegionSelectionService.class);
        serviceDependencies.add(ItemGlowService.class);
        serviceDependencies.add(StructurePersistenceService.class);
        serviceDependencies.add(BulkEditService.class);
        serviceDependencies.add(ExtentLockService.class);
        serviceDependencies.add(TemporaryEntityService.class);
        serviceDependencies.add(PlayerHeadingService.class);
        serviceDependencies.addAll(getThirdPartyServiceDependencies());

        return binder -> {
            for (final var service : serviceDependencies) {
                final Key<BukkitServiceProvider> key = (Key<BukkitServiceProvider>) Key.get(newParameterizedType(BukkitServiceProvider.class, service));
                binder.bind(key).toInstance(new BukkitServiceProvider(service, getServer()));
            }
        };
    }

    /**
     * Subclasses of {@link IocJavaPlugin} are not allowed to implement onDisable themselves, since onEnable is a violation
     * of the separation of concerns principle. To run code during plugin de-initialization use the mechanism
     * provided by {@link PostConstructSupport}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public final void onEnable() {
        asyncLogger = new AsyncFileHandler(new File(getDataFolder(), "plugin.log"));
        getLogger().addHandler(asyncLogger);
        getLogger().setLevel(Level.ALL);

        bukkitPluginSupportModule = new BukkitPluginSupportModule<>(this, (Class) this.getClass(), createObjectMapperModule());
        final var pluginModule = createIocModule();
        final var modules = Modules.combine(bukkitPluginSupportModule, pluginModule, getThirdPartyServiceModule());
        Guice.createInjector(modules).injectMembers(this);
    }

    /**
     * Subclasses of {@link IocJavaPlugin} are not allowed to implement onDisable themselves, since onDisable is a violation
     * of the separation of concerns principle. To run code during plugin de-initialization use the mechanism
     * provided by {@link PreDestroySupport}.
     */
    @Override
    public final void onDisable() {
        while (!bukkitPluginSupportModule.preDestroyHooks.isEmpty()) {
            try {
                bukkitPluginSupportModule.preDestroyHooks.pop().run();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "An unexpected error occurred while disabling the plugin.", e);
            }
        }
        getLogger().removeHandler(asyncLogger);
        asyncLogger.close();
    }

    /**
     * Creates a new Guice {@link Module} which contains bindings for the plugin's {@link org.bukkit.event.Listener}s,
     * and tick tasks. {@link IocJavaPlugin}s implement this method instead of {@link JavaPlugin#onDisable()} and
     * {@link JavaPlugin#onEnable()}.
     *
     * When the {@link JavaPlugin} is enabled, an injector is created containing the module returned by this method and
     * a new {@link BukkitPluginSupportModule}. Then member injection is performed on this {@link IocJavaPlugin} subclass.
     */
    protected abstract Module createIocModule();

    /**
     * Returns a set of all the third-party Bukkit services which this plugin depends on.
     * In general, plugins can provide services to one another by registering them with Bukkit's
     * {@link org.bukkit.plugin.ServicesManager} during the {@link Plugin#onLoad()} lifecycle phase.
     *
     * All services which are returned from this method are made available for injection throughout the plugin.
     * Services which are not declared here will not be made available for injection. To inject a Bukkit service
     * create a field of type {@link BukkitServiceProvider<>} and annotate it with {@link javax.inject.Inject}.
     */
    protected Set<Class<?>> getThirdPartyServiceDependencies() {
        return Collections.emptySet();
    }

    protected ObjectMapperModule createObjectMapperModule() {
        return new ObjectMapperModule();
    }
}
