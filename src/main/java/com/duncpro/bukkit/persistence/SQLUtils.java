package com.duncpro.bukkit.persistence;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SQLUtils {
    public static void runScript(InputStream inputStream, DataSource dataSource) throws SQLException {
        try (final var connection = dataSource.getConnection()) {
            final var scanner = new Scanner(inputStream).useDelimiter(Pattern.quote(";"));
            while (scanner.hasNext()) {
                final var statementStr = scanner.next();
                try (final var statement = connection.prepareStatement(statementStr)) {
                    statement.executeUpdate();
                }
            }
        }
    }
}
