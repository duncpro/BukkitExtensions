package com.duncpro.bukkit.log;

import com.duncpro.bukkit.concurrency.NextTickSync;
import com.duncpro.bukkit.concurrency.WeakSequentialTaskMap;
import com.duncpro.bukkit.concurrency.BukkitThreadPool;
import com.duncpro.bukkit.persistence.Local;
import com.duncpro.bukkit.persistence.SQLUtils;
import com.duncpro.bukkit.plugin.PostConstruct;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListeningPlayerRegistry implements Listener {
    @Inject
    @Local
    private DataSource dataSource;

    @InjectLogger
    private Logger logger;

    @Inject
    @BukkitThreadPool
    private Executor ioExecutor;

    @Inject
    @NextTickSync
    private Executor minecraftExecutor;

    @Inject
    private Server server;

    // No need to clear this onDisable, since the garbage collector should take care of it because
    // there are no references to real Bukkit objects.
    private final ConcurrentMap<UUID, Map<String, Level>> playerLoggers = new ConcurrentHashMap<>();

    private WeakSequentialTaskMap<Player> tasks;

    @PostConstruct
    public void onEnable() {
        tasks = new WeakSequentialTaskMap<>(ioExecutor);

        try {
            SQLUtils.runScript(getClass().getResourceAsStream("/create-player-logger-table.sql"), dataSource);
        } catch (SQLException e) {
           logger.log(Level.WARNING, "Unable to create player logger table in persistent store. It may be impossible" +
                   " to persist player logger settings.", e);
        }

        for (final var player : server.getOnlinePlayers()) {
            loadPlayerLoggersFromPersistentStore(player);
        }
    }

    @EventHandler
    public void onPluginLog(AsyncPluginLogEvent event) {
        for (final var entry : playerLoggers.entrySet()) {
            final var playerId = entry.getKey();
            final var level = entry.getValue().get(event.pluginName);
            if (level == null) continue;

            minecraftExecutor.execute(() -> {
                final var player = server.getPlayer(playerId);
                if (player == null) return; // Player is offline now

                if (event.record.getLevel().intValue() >= level.intValue()) {
                    player.sendMessage(ChatColor.YELLOW + "[" + event.pluginName + "] " + ChatColor.WHITE +
                            event.formattedMessage);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerLoggersFromPersistentStore(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final var playerId = event.getPlayer().getUniqueId();
        tasks.queueTask(event.getPlayer(), $ -> playerLoggers.remove(playerId));
    }

    private void loadPlayerLoggersFromPersistentStore(Player player) {
        final var playerId = player.getUniqueId();

        tasks.queueTask(player, $ -> {
            final Map<String, Level> loggers;

            try {
                loggers = readLoggersFromPersistentStore(playerId);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Unable to load player loggers from persistent store. Loggers set during prior server" +
                        " sessions are forgotten.", e);
                return;
            }

            if (loggers.size() > 0) {
                player.sendMessage("BukkitExtensions has been configured to print console log messages to your" +
                        " chat screen. Log messages from the following plugins will be echoed to your chat screen...");
            }

            for (final var entry : loggers.entrySet()) {
                if (entry.getValue() == Level.OFF) continue;
                player.sendMessage(" - " + entry.getKey() + " (level: " + entry.getValue() + ")");
            }

            playerLoggers.put(playerId, loggers);
        });
    }

    private Map<String, Level> readLoggersFromPersistentStore(UUID playerId) throws SQLException {
        final var playerLoggers = new ConcurrentHashMap<String, Level>();

        try (final var connection = dataSource.getConnection()) {
            try (final var statement = connection.prepareStatement("SELECT log_level, source FROM player_logger" +
                    " WHERE player = ?")) {
                statement.setString(1, playerId.toString());

                try (final var results = statement.executeQuery()) {
                    while (results.next()) {
                        final var source = results.getString("source");
                        final var logLevel = Level.parse(results.getString("log_level"));
                        playerLoggers.put(source, logLevel);
                    }
                }
            }
        }

        return playerLoggers;
    }

    private void persistPlayerLogger(UUID playerId, String source, Level level) throws SQLException {
        try (final var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (final var statement = connection.prepareStatement("DELETE FROM player_logger WHERE" +
                    " player = ? AND source = ?;")) {
                statement.setString(1, playerId.toString());
                statement.setString(2, source);
                statement.executeUpdate();
            }
            try (final var statement = connection.prepareStatement("INSERT INTO player_logger (log_level, source, player)" +
                    " VALUES (?, ?, ?);")) {
                statement.setString(1, level.toString());
                statement.setString(2, source);
                statement.setString(3, playerId.toString());
                statement.executeUpdate();
            }
            connection.commit();
        }
    }

    CompletableFuture<Void> setLogLevel(Player player, Plugin plugin, Level level) {
        final var playerId = player.getUniqueId();
        final var pluginName = plugin.getName();

        final Consumer<Player> persistor = $ -> {
            try {
                persistPlayerLogger(playerId, pluginName, level);
            } catch (SQLException e) {
                throw new CompletionException("Failed to persist change in player loggers to database.", e);
            }
            playerLoggers.get(playerId).put(pluginName, level);
        };

        return tasks.queueTask(player, persistor);
    }
}
