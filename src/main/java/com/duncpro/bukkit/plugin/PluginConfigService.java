package com.duncpro.bukkit.plugin;

import com.duncpro.bukkit.log.InjectLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

class PluginConfigService {
    @Inject
    @PluginDataFolder
    private File pluginDataFolder;

    @Inject
    @PluginConfig
    private File pluginConfigFile;

    @Inject
    private Plugin plugin;

    @InjectLogger private Logger logger;

    private InputStream getDefaultConfigStream() {
        return plugin.getClass().getResourceAsStream("/config.yml");
    }

    @PostConstruct
    public void unpackDefaultConfig() {
        if (pluginConfigFile.exists()) {
            logger.finer("Plugin configuration file already exists. Skipping default config file unpacking process.");
            return;
        }

        final var defaultConfigStream = getDefaultConfigStream();

        if (defaultConfigStream == null) {
            logger.finer("No default plugin configuration file to unpack.");
            return;
        }

        if (!pluginDataFolder.exists()) {
            if (!pluginDataFolder.mkdir()) {
                logger.warning("Unable to create plugin data file. Cannot unpack default config.");
                return;
            }
        }

        try {
            final var defaultConfigFile = defaultConfigStream.readAllBytes();
            Files.writeString(pluginConfigFile.toPath(), new String(defaultConfigFile));
        } catch (IOException e) {
            logger.log(Level.WARNING, "An unexpected error occurred while unpacking the default config.yml.", e);
        }
    }

    public YamlConfiguration loadConfiguration() {
        final var explicitConfig = YamlConfiguration.loadConfiguration(pluginConfigFile);
        final var implicitConfig = new YamlConfiguration();

        final var defaultConfigStream = getDefaultConfigStream();
        if (defaultConfigStream != null) {
           try (defaultConfigStream) {
               try (final var reader = new InputStreamReader(defaultConfigStream)) {
                   implicitConfig.load(reader);
               }
           } catch (IOException | InvalidConfigurationException e) {
               logger.log(Level.SEVERE, "Failed to apply default, implicit config values, because an error occurred" +
                       " while reading the config.yml file within the plugin jar file.", e);
           }
        }

        explicitConfig.addDefaults(implicitConfig);
        return explicitConfig;
    }
}
