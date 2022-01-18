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
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;

class BukkitPluginSupportModule<P extends JavaPlugin> extends AbstractModule {
    final Stack<Runnable> preDestroyHooks = new Stack<>();
    private final P plugin;

    BukkitPluginSupportModule(P plugin) {
        this.plugin = requireNonNull(plugin);
    }

    @Override
    public void configure() {
        binder().requireExplicitBindings();

        bind(Plugin.class).toInstance(plugin);
        bind(JavaPlugin.class).toInstance(plugin);

        //noinspection unchecked,rawtypes
        bind((Class) plugin.getClass()).toInstance(plugin);

        bind(Server.class).toInstance(plugin.getServer());

        bindListener(Matchers.any(), new PreDestroySupport(preDestroyHooks, plugin));
        bindListener(Matchers.any(), new PostConstructSupport(plugin));
        bindListener(Matchers.any(), new BukkitListenerRegistrar(plugin));
        bindListener(Matchers.any(), new MinecraftGameLoopTaskAutoRegistrar(plugin));
        bindListener(Matchers.any(), new PluginLoggerGuiceTypeListener(plugin));
        bindListener(Matchers.any(), new CommandHandlerRegistrar(plugin));
        bindListener(Matchers.any(), new BukkitServiceImplAutoRegistrar(plugin));
        bindListener(Matchers.any(), new BukkitServiceCustomInjection());
        bind(PluginConfigService.class).asEagerSingleton();
        install(new CommandSupportModule());
    }

    @Provides
    @BukkitThreadPool
    public Executor provideBukkitAsyncSchedulerExecutor() {
        // Bukkit is thread safe for this method
        return (task) -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Provides
    @NextTickSync
    public Executor provideMinecraftGameThreadExecutor() {
        // Bukkit is thread safe for this method
        return (task) -> plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Provides
    @PluginDataFolder
    public File providePluginDataFolder() {
        return plugin.getDataFolder();
    }

    @Provides
    @PluginConfig
    public File providePluginConfigFile(@PluginDataFolder File dataFolder) {
        return new File(dataFolder, "config.yml");
    }

    @Provides
    @PluginConfig
    public YamlConfiguration providePluginConfig(PluginConfigService pluginConfigService) {
        return pluginConfigService.loadConfiguration();
    }
}
