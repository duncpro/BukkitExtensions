package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.command.CommandHandlerRegistrar;
import com.duncpro.bukkit.command.CommandSupportModule;
import com.duncpro.bukkit.concurrency.BukkitThreadPool;
import com.duncpro.bukkit.concurrency.NextTickSync;
import com.duncpro.bukkit.persistence.json.BukkitTypesJsonModule;
import com.duncpro.bukkit.persistence.json.PersistentChunkMapFactory;
import com.duncpro.bukkit.region.lock.PluginExtentLockFactory;
import com.duncpro.bukkit.log.PluginLoggerGuiceTypeListener;
import com.duncpro.bukkit.metadata.TemporaryMetadataService;
import com.duncpro.bukkit.misc.ThrowingRunnable;
import com.duncpro.bukkit.persistence.Local;
import com.duncpro.bukkit.persistence.RelationalDatabaseService;
import com.duncpro.bukkit.persistence.RelationalDatabaseServiceImpl;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.guice.ObjectMapperModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.File;
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

class BukkitPluginSupportModule<P extends IocJavaPlugin> extends AbstractModule {
    final Stack<ThrowingRunnable> preDestroyHooks = new Stack<>();
    private final P plugin;
    private final Class<P> pluginJavaType;
    private final ObjectMapperModule jacksonModule;

    BukkitPluginSupportModule(P plugin, Class<P> pluginJavaType, ObjectMapperModule jacksonModule) {
        this.plugin = requireNonNull(plugin);
        this.pluginJavaType = requireNonNull(pluginJavaType);
        this.jacksonModule = requireNonNull(jacksonModule);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void bindPluginDependencies() {
        for (final var dependencyName : plugin.getDescription().getDepend()) {
            final Plugin dependency = plugin.getServer().getPluginManager().getPlugin(dependencyName);
            if (dependency == null) throw new IllegalStateException();
            final Class dependencyClass = dependency.getClass();
            bind(dependencyClass).toInstance(dependency);
            plugin.getLogger().log(Level.FINER, "Exposing dependency: " + dependencyName + " to plugin components" +
                    " via dependency injection.");
        }
    }

    @Override
    public void configure() {
        binder().requireExplicitBindings();

        bind(Plugin.class).to(pluginJavaType);
        bind(JavaPlugin.class).toInstance(plugin);
        bind(pluginJavaType).toInstance(plugin);
        bind(Server.class).toInstance(plugin.getServer());

        bindListener(Matchers.any(), new PreDestroySupport(preDestroyHooks));
        bindListener(Matchers.any(), new PostConstructSupport(plugin));
        bindListener(Matchers.any(), new BukkitListenerRegistrar(plugin));
        bindListener(Matchers.any(), new MinecraftGameLoopTaskAutoRegistrar(plugin));
        bindListener(Matchers.any(), new PluginLoggerGuiceTypeListener(plugin));
        bindListener(Matchers.any(), new CommandHandlerRegistrar(plugin));
        bindListener(Matchers.any(), new BukkitServiceImplAutoRegistrar(plugin));

        bind(PersistentChunkMapFactory.class);
        bind(RelationalDatabaseServiceImpl.class).asEagerSingleton();
        bind(RelationalDatabaseService.class).to(RelationalDatabaseServiceImpl.class);
        bind(PluginConfigService.class).asEagerSingleton();
        bind(TemporaryMetadataService.class);
        bind(PluginExtentLockFactory.class);
        install(new CommandSupportModule());

        bindPluginDependencies();

        install(jacksonModule
                .registerModule(new BukkitTypesJsonModule())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule()));
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

    @Provides
    @Local
    public DataSource provideDataSource(RelationalDatabaseService relationalDatabaseService) {
        return relationalDatabaseService.getDataSource();
    }
}
