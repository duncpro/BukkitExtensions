package com.duncpro.bukkit.command.context;

import com.duncpro.bukkit.plugin.BukkitServiceNotAvailableException;
import com.duncpro.bukkit.plugin.BukkitServiceProvider;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import com.duncpro.bukkit.region.selection.IncompleteSelectionException;
import com.duncpro.bukkit.region.selection.RegionSelectionService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class CuboidRegionSelectionContextElementType extends CommandContextElementType {

    @Inject
    private BukkitServiceProvider<RegionSelectionService> regionSelectionService;

    public CuboidRegionSelectionContextElementType() {
        super(CuboidRegionSelection.class);
    }

    @Override
    public Object resolve(CommandSender sender) throws IncorrectCommandContextException {
        if (!(sender instanceof final Player player)) {
            throw new IncorrectCommandContextException("This command requires a WorldEdit selection" +
                    " to be made before it can execute. Therefore you must be a player to use this command.");
        }

        CuboidRegionSelection selection;
        try {
            final var raw = regionSelectionService.get().getSelection(player);
            if (!(raw instanceof CuboidRegionSelection)) {
                player.sendMessage("Not a cuboid");
               throw new IncorrectCommandContextException("You must have a cuboid selected to use this command.");
            }
            selection = (CuboidRegionSelection) raw;
        } catch (IncompleteSelectionException e) {
            throw new IncorrectCommandContextException("You must have a cuboid selected to use this command.");
        } catch (BukkitServiceNotAvailableException e) {
            throw new IncorrectCommandContextException("This command requires the presence of a block selection service." +
                    " Unfortunately no such service exists on this server. Consider installing a plugin which provides" +
                    " block selection capabilities in order to use this command.");
        }

        return selection;
    }
}
