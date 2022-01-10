package com.duncpro.bukkit.structure;

import com.duncpro.bukkit.geometry.WorldPerspectiveCuboid;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface StructurePersistenceService {
    LogicallyOrientedSchematic createSchematic(WorldPerspectiveCuboid cuboid);

    CompletableFuture<LogicallyOrientedSchematic> loadSchematic(File file);
}
