package com.duncpro.bukkit.region.label;

import java.util.Optional;
import java.util.UUID;

public interface PersistentRegion {
    UUID getId();

    String getDisplayName();

    <T extends PersistentRegionLabel> Optional<T> getFlag(Class<T> type);

    <T extends PersistentRegionLabel> void setLabel(T label);

    <T extends PersistentRegionLabel> void clearLabel(Class<T> labelType);
}
