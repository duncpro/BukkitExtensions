package com.duncpro.bukkit.persistence;

import com.duncpro.bukkit.concurrency.BukkitThreadSafe;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@BukkitThreadSafe
public interface RelationalDatabaseService {
    DataSource getDataSource();

    default Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
}
