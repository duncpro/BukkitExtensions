package com.duncpro.bukkit.region.selection;

import com.duncpro.bukkit.command.annotation.CommandHandler;
import com.duncpro.bukkit.command.annotation.Context;
import com.duncpro.bukkit.plugin.BukkitServiceNotAvailableException;
import com.duncpro.bukkit.plugin.BukkitServiceProvider;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@CommandHandler(command = "/trim")
public class TrimSelectionCommand implements Runnable {

    @Inject
    private BukkitServiceProvider<RegionSelectionService> regionSelectionServiceProvider;

    @Context
    private Player player;

    @Override
    public void run() {
        try {
            final var service = regionSelectionServiceProvider.get();
            final var selection = service.getSelection(player);

            if (!(selection instanceof CuboidRegionSelection)) {
                player.sendMessage("This command only supports cuboid regions.");
                return;
            }

            final var trimmed = Selections.trimSelection((CuboidRegionSelection) selection);

            if (trimmed.isEmpty()) {
                player.sendMessage("Unable to trim selection because the selection is empty (contains no blocks" +
                        " other than air)");
                return;
            }

            service.applySelection(player, trimmed.get());
            player.sendMessage("Trimmed selection from " + ((CuboidRegionSelection) selection).getDimensions() +
                    " blocks to " + trimmed.get().getDimensions() + " blocks.");

        } catch (BukkitServiceNotAvailableException e) {
            player.sendMessage("This command is not available since there is no plugin currently implementing" +
                    " " + RegionSelectionService.class.getName() + ".");
        } catch (IncompleteSelectionException e) {
            player.sendMessage("This command is not available since there is no region currently selected.");
        }
    }
}
