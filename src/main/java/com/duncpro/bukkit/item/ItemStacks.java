package com.duncpro.bukkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class ItemStacks {
    public static ItemStack newItemStack(Material material, Consumer<ItemMeta> metaFactory) {
        final var item = new ItemStack(material);
        final var metaCopy = item.getItemMeta();
        metaFactory.accept(metaCopy);
        item.setItemMeta(metaCopy);
        return item;
    }
}
