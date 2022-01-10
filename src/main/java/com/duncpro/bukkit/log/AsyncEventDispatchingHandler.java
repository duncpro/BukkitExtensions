package com.duncpro.bukkit.log;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.concurrent.Executor;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import static java.util.Objects.requireNonNull;

public class AsyncEventDispatchingHandler extends Handler {
    private final PluginManager pluginManager;
    private final Executor executor;
    private final Plugin plugin;
    private final String pluginName;

    AsyncEventDispatchingHandler(PluginManager pluginManager, Executor executor, Plugin plugin, String pluginName) {
        this.pluginManager = requireNonNull(pluginManager);
        this.executor = requireNonNull(executor);
        this.plugin = requireNonNull(plugin);
        this.pluginName = requireNonNull(pluginName);
        setFormatter(new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        final var formattedMessage = getFormatter().formatMessage(record);
        executor.execute(() -> pluginManager.callEvent(new AsyncPluginLogEvent(record, plugin, pluginName, formattedMessage)));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
