package com.duncpro.bukkit;

import com.duncpro.bukkit.entity.DefaultTemporaryEntityService;
import com.duncpro.bukkit.geometry.PlayerHeadingServiceImpl;
import com.duncpro.bukkit.item.DefaultItemGlowServiceRegistrar;
import com.duncpro.bukkit.item.NoopEnchantment;
import com.duncpro.bukkit.region.lock.ExtentLockServiceImpl;
import com.duncpro.bukkit.log.ListenCommand;
import com.duncpro.bukkit.log.ListeningPlayerRegistry;
import com.duncpro.bukkit.log.LogHandlerRegistrar;
import com.duncpro.bukkit.region.selection.TrimSelectionCommand;
import com.google.inject.AbstractModule;

public class BukkitExtensionsPluginModule extends AbstractModule {
    @Override
    public void configure() {
        // Item Glow
        bind(NoopEnchantment.class).asEagerSingleton();
        bind(DefaultItemGlowServiceRegistrar.class).asEagerSingleton();

        // Logging to Chat
        bind(ListeningPlayerRegistry.class).asEagerSingleton();
        bind(LogHandlerRegistrar.class).asEagerSingleton();
        bind(ListenCommand.class);

        bind(DefaultTemporaryEntityService.class).asEagerSingleton();

        bind(TrimSelectionCommand.class);

        bind(ExtentLockServiceImpl.class).asEagerSingleton();

        bind(PlayerHeadingServiceImpl.class).asEagerSingleton();
    }
}
