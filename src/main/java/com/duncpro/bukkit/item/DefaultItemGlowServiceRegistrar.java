package com.duncpro.bukkit.item;

import com.duncpro.bukkit.log.InjectLogger;
import com.duncpro.bukkit.plugin.PostConstruct;
import com.duncpro.bukkit.plugin.BukkitServiceImpl;
import com.duncpro.bukkit.plugin.PreDestroy;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultItemGlowServiceRegistrar {
    @Inject
    private NoopEnchantment noopEnchantment;

    @InjectLogger
    private Logger logger;

    @Inject
    private Server server;

    @Inject
    private Plugin plugin;

    @PostConstruct
    public void onEnable() {
        try {
            registerNoopEnchantmentWithCraftBukkit();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.log(Level.WARNING, "Unable to register custom enchantment and therefore unable to provide default" +
                    " implementation of GlowService", e);
            return;
        }

        registerGlowService();
    }

    private void registerNoopEnchantmentWithCraftBukkit() throws NoSuchFieldException, IllegalAccessException {
        if (Enchantment.getByKey(noopEnchantment.getKey()) != null) return;

        Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(noopEnchantment);
            f.setAccessible(false);
    }

    private void registerGlowService() {
        final var serviceManager = server.getServicesManager();

        final var impl = new ItemGlowService() {
            @Override
            public void applyGlow(ItemStack itemStack) {
                itemStack.addEnchantment(noopEnchantment, 1);
            }
        };

        serviceManager.register(ItemGlowService.class, impl, plugin, ServicePriority.Lowest);
    }
}
