package com.duncpro.bukkit.metadata;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

class UniqueMetadataValue extends FixedMetadataValue {
    final UUID uniqueId;

    UniqueMetadataValue(Plugin owningPlugin, Object value) {
        super(owningPlugin, value);
        this.uniqueId = UUID.randomUUID();
    }
}
