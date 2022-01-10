package com.duncpro.bukkit.region.label;

import java.util.Optional;
import java.util.UUID;

/**
 * Some region within the world which can be labeled.
 */
public interface PersistentRegion {
    UUID getId();

    String getDisplayName();

    <T extends PersistentRegionLabel> Optional<T> getLabel(Class<T> type);

    <T extends PersistentRegionLabel> void setLabel(T label);

    <T extends PersistentRegionLabel> void clearLabel(Class<T> labelType);
}
