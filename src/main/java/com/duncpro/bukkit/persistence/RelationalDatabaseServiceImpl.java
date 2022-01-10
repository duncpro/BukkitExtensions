package com.duncpro.bukkit.persistence;

import com.duncpro.bukkit.log.InjectLogger;
import com.duncpro.bukkit.plugin.PluginDataFolder;
import com.duncpro.bukkit.plugin.PostConstruct;
import com.duncpro.bukkit.plugin.PreDestroy;
import org.bukkit.plugin.Plugin;
import org.h2.jdbcx.JdbcDataSource;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ThreadSafe
public class RelationalDatabaseServiceImpl implements RelationalDatabaseService {
    private volatile JdbcDataSource dataSource;

    @Inject
    @PluginDataFolder
    private File pluginDataFolder;

    @InjectLogger
    private Logger logger;

    @Inject
    private Plugin plugin;

    public DataSource getDataSource() {
        if (dataSource == null)
            throw new IllegalStateException("Can not access database before plugin has been enabled.");
        return dataSource;
    }

    @PostConstruct
    public void onEnable() {
        dataSource = new JdbcDataSource();

        if (!pluginDataFolder.exists()) {
            if (!pluginDataFolder.mkdir()) {
                logger.warning("Plugin data folder does not exist nor could it be created. Data stored within the " +
                        "relational database will not persist between server reloads.");
                dataSource.setURL("jdbc:h2:mem:" + plugin.getName());
                return;
            }
        }

        dataSource.setURL("jdbc:h2:" + new File(pluginDataFolder, "persistent-data").getAbsolutePath());
    }

    @PreDestroy
    public void onDisable() {
        try (final var connection = dataSource.getConnection()) {
            try (final var statement = connection.prepareStatement("SHUTDOWN;")) {
                statement.execute();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An unexpected error occurred while shutting down the database. Data may" +
                    " have been lost.", e);
        }
    }
}
