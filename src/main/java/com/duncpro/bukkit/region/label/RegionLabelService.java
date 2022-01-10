package com.duncpro.bukkit.region.label;

import com.duncpro.bukkit.concurrency.BukkitThreadSafe;
import com.duncpro.bukkit.region.selection.CuboidRegionSelection;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@BukkitThreadSafe
public interface RegionLabelService {
    <T extends PersistentRegionLabel> void setRuntimeLabel(CuboidRegionSelection region, T label);

    PersistentRegion registerPersistentRegion(String displayName, UUID id, CuboidRegionSelection region);

    void deletePersistentRegion(UUID id);

    Set<? extends PersistentRegionLabel> getLabels(Vector pos);

    <T extends PersistentRegionLabel> Map<CuboidRegionSelection, T> getLabeledRegions(Class<T> labelType);
}
