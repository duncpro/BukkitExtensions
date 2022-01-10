package com.duncpro.bukkit.entity;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import static com.duncpro.bukkit.geometry.Vectors.difference;

public class Entities {
    public static Location getLowerBodyPosition(Entity entity) {
        return entity.getLocation();
    }

    public static Block getPlatformBlock(Entity entity) {
        return difference(getLowerBodyPosition(entity), new Vector(0, 1, 0)).getBlock();
    }
}
