package com.duncpro.bukkit.metadata;

import com.duncpro.bukkit.log.InjectLogger;
import com.google.common.collect.MoreCollectors;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class TemporaryMetadataService {
    @Inject private Plugin plugin;

    @InjectLogger private Logger logger;

    public void attachTemporaryMetadata(Metadatable to, String key, Object value, long lifespan) {
        if (lifespan < 1) throw new IllegalArgumentException();

        final var newValue = new UniqueMetadataValue(plugin, value);
        to.setMetadata(key, newValue);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> removeIfSet(to, key, newValue.uniqueId),
                lifespan);
        logger.finest("Attached temporary metadata to: " + to + " which is expiring in: " + lifespan + " ticks.");
    }

    public void attachTemporaryMetadata(Metadatable to, String key, Object value, Duration lifespan) {
        attachTemporaryMetadata(to, key, value, lifespan.toSeconds() * 20);
    }

    private void removeIfSet(Metadatable from, String key, UUID uniqueId) {
        final var currentValue = from.getMetadata(key).stream()
                .filter(metadata -> Objects.equals(metadata.getOwningPlugin(), plugin))
                .collect(MoreCollectors.toOptional());

        if (currentValue.isEmpty()) return;

        if (!(currentValue.get() instanceof UniqueMetadataValue)) return;

        if (!Objects.equals(((UniqueMetadataValue) currentValue.get()).uniqueId, uniqueId)) return;

        from.removeMetadata(key, plugin);
        logger.finest("Metadata attached to: " + from + " under key: " + key + " has expired.");
    }
}
