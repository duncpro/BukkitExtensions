package com.duncpro.bukkit.region.selection;

import org.bukkit.entity.Player;

public interface RegionSelectionService {
    RegionSelection getSelection(Player player) throws IncompleteSelectionException;

    void applySelection(Player player, RegionSelection selection);
}
