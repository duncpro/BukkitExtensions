package com.duncpro.bukkit.persistence.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bukkit.util.Vector;

public class BukkitTypesJsonModule extends SimpleModule {
    public BukkitTypesJsonModule() {
        super("BukkitTypes");
        addSerializer(Vector.class, new VectorJsonSerializer());
        addDeserializer(Vector.class, new VectorJsonDeserializer());
    }
}
