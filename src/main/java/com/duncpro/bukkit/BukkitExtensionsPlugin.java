package com.duncpro.bukkit;

import com.duncpro.bukkit.item.ItemGlowService;
import com.duncpro.bukkit.plugin.IocJavaPlugin;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

public class BukkitExtensionsPlugin extends IocJavaPlugin {
    @Override
    protected Module createIocModule() {
        return new BukkitExtensionsPluginModule();
    }

    @Override
    protected Set<Class<?>> getThirdPartyServiceDependencies() {
        return Set.of(ItemGlowService.class);
    }
}
