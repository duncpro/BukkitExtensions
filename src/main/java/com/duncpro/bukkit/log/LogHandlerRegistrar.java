package com.duncpro.bukkit.log;

import com.duncpro.bukkit.concurrency.BukkitThreadPool;
import com.duncpro.bukkit.plugin.PostConstruct;
import com.duncpro.bukkit.plugin.PreDestroy;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

public class LogHandlerRegistrar implements Listener {
    private final Map<Plugin, AsyncEventDispatchingHandler> pluginLogHandlers = new HashMap<>();

    @Inject
    private Server server;

    @InjectLogger
    private Logger logger;

    @Inject
    @BukkitThreadPool
    private Executor asyncExecutor;

    @PostConstruct
    public void onEnable() {
        for (final var plugin : server.getPluginManager().getPlugins()) {
            installLogHandler(plugin);
        }
    }

    @PreDestroy
    public void onDisable() {
        for (final var plugin : server.getPluginManager().getPlugins()) {
            uninstallLogHandler(plugin);
        }
    }

    private void installLogHandler(Plugin plugin) {
        if (pluginLogHandlers.containsKey(plugin)) return; // Already installed

        final var handler = new AsyncEventDispatchingHandler(server.getPluginManager(),
                asyncExecutor, plugin, plugin.getName());
        plugin.getLogger().addHandler(handler);
        pluginLogHandlers.put(plugin, handler);
    }

    private void uninstallLogHandler(Plugin plugin) {
        final var handler = pluginLogHandlers.remove(plugin);
        if (handler == null) return; // Wasn't installed
        plugin.getLogger().removeHandler(handler);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        installLogHandler(event.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        uninstallLogHandler(event.getPlugin());
    }
}
