package com.duncpro.bukkit.log;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.logging.LogRecord;

import static java.util.Objects.requireNonNull;

public class AsyncPluginLogEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    final LogRecord record;
    final Plugin plugin;
    final String pluginName;
    final String formattedMessage;

    public AsyncPluginLogEvent(LogRecord record, Plugin plugin, String pluginName, String formattedMessage) {
        super(true);
        this.record = requireNonNull(record);
        this.plugin = requireNonNull(plugin);
        this.pluginName = requireNonNull(pluginName);
        this.formattedMessage = formattedMessage;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
