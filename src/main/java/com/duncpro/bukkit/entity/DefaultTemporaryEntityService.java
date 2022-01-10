package com.duncpro.bukkit.entity;

import com.duncpro.bukkit.log.InjectLogger;
import com.duncpro.bukkit.persistence.JavaSerializablePersistentDataType;
import com.duncpro.bukkit.persistence.NamespacedKeys;
import com.duncpro.bukkit.plugin.BukkitServiceImpl;
import com.duncpro.bukkit.plugin.PostConstruct;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import javax.inject.Inject;

import java.util.logging.Logger;

import static java.util.Optional.ofNullable;

@BukkitServiceImpl(service = TemporaryEntityService.class, priority = ServicePriority.Lowest)
public class DefaultTemporaryEntityService implements TemporaryEntityService, Listener {

    @Inject
    private Plugin plugin;

    private NamespacedKey persistentKey;

    @PostConstruct
    public void onLoad() {
        persistentKey = NamespacedKeys.get(plugin, "temporary");
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (final var entity : event.getEntities()) {
            final var isTemporaryEntity = ofNullable(entity.getPersistentDataContainer().get(persistentKey,
                    new JavaSerializablePersistentDataType<>(Boolean.class))).orElse(false);

            if (isTemporaryEntity) {
                entity.remove();
            }
        }
    }

    @Override
    public void setTemporary(Entity entity) {
        entity.getPersistentDataContainer().set(persistentKey,
                new JavaSerializablePersistentDataType<>(Boolean.class), true);
    }
}
