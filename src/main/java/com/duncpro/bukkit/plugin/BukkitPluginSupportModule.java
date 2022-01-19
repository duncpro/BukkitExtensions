package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.command.CommandHandlerRegistrar;
import com.duncpro.bukkit.command.CommandSupportModule;
import com.duncpro.bukkit.concurrency.BukkitThreadPool;
import com.duncpro.bukkit.concurrency.NextTickSync;
import com.duncpro.bukkit.log.PluginLoggerGuiceTypeListener;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;

public class BukkitPluginSupportModule<P extends JavaPlugin> extends AbstractModule {
    private final P plugin;

    public BukkitPluginSupportModule(P plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @Override
    public void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(JavaPlugin.class).toInstance(plugin);

        //noinspection unchecked,rawtypes
        bind((Class) plugin.getClass()).toInstance(plugin);

        bind(Server.class).toInstance(plugin.getServer());

        bindListener(Matchers.any(), new PreDestroySupport(plugin));
        bindListener(Matchers.any(), new PostConstructSupport(plugin));
        bindListener(Matchers.any(), new BukkitListenerRegistrar(plugin));
        bindListener(Matchers.any(), new MinecraftGameLoopTaskAutoRegistrar(plugin));
        bindListener(Matchers.any(), new PluginLoggerGuiceTypeListener(plugin));
        bindListener(Matchers.any(), new CommandHandlerRegistrar(plugin));
        bindListener(Matchers.any(), new BukkitServiceImplAutoRegistrar(plugin));
        bindListener(Matchers.any(), new BukkitServiceCustomInjection());
        bind(PluginConfigService.class).asEagerSingleton();
        install(new CommandSupportModule());
        bind(LifecycleHooks.class).asEagerSingleton();
    }

    @Provides
    @BukkitThreadPool
    Executor provideBukkitAsyncSchedulerExecutor() {
        // Bukkit is thread safe for this method
        return (task) -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Provides
    @NextTickSync
    Executor provideMinecraftGameThreadExecutor() {
        // Bukkit is thread safe for this method
        return (task) -> {
            if (plugin.getServer().isPrimaryThread()) {
                task.run();
            } else {
                plugin.getServer().getScheduler().runTask(plugin, task);
            }
        };
    }

    @Provides
    @PluginDataFolder
    File providePluginDataFolder() {
        return plugin.getDataFolder();
    }

    @Provides
    @PluginConfig
    File providePluginConfigFile(@PluginDataFolder File dataFolder) {
        return new File(dataFolder, "config.yml");
    }

    @Provides
    @PluginConfig
    YamlConfiguration providePluginConfig(PluginConfigService pluginConfigService) {
        return pluginConfigService.loadConfiguration();
    }
}
