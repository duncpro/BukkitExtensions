package com.duncpro.bukkit.log;

import com.duncpro.bukkit.command.annotation.CommandHandler;
import com.duncpro.bukkit.command.annotation.Context;
import com.duncpro.bukkit.command.annotation.NamedParameter;
import com.duncpro.bukkit.command.annotation.PositionedParameter;
import com.duncpro.bukkit.concurrency.ConcurrencyUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandHandler(command = "listen")
public class ListenCommand implements Runnable {

    @PositionedParameter(label = "plugin")
    private String pluginName;

    @NamedParameter(abbreviation = "l", description = "The maximum log level which should be printed. By default FINER.")
    private String logLevelName = null;

    @Context
    private Player player;

    @Inject
    private ListeningPlayerRegistry listeningPlayerRegistry;

    @Inject
    private Server server;

    @Inject
    private Logger logger;

    @Override
    public void run() {
        if (logLevelName == null) logLevelName = Level.FINER.getName();

        final Level level;
        try {
            level = Level.parse(logLevelName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("The given log level is not valid.");
            return;
        }

        final Plugin plugin = server.getPluginManager().getPlugin(pluginName);

        if (plugin == null) {
            player.sendMessage("No plugin by that name is currently running on the server.");
            return;
        }
        final String pluginName = plugin.getName();

        listeningPlayerRegistry.setLogLevel(player, plugin, level)
                .thenRun(() -> player.sendMessage("You will now receive log messages from plugin: " + pluginName
                    + " up to level: " + level))
                .whenComplete(ConcurrencyUtil.logErrors(logger));
    }
}
