package com.duncpro.bukkit;

import org.bukkit.Material;

import java.util.Set;

public class Materials {
    public static Set<Material> AIR = Set.of(
            Material.AIR,
            Material.VOID_AIR,
            Material.CAVE_AIR
    );

    public static Set<Material> TREE_TRUNKS = Set.of(
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG
    );

    public static Set<Material> TREE_LEAVES = Set.of(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );
}
