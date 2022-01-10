package com.duncpro.bukkit.region.label;

/**
 * A label which can be applied to some region of a Minecraft world.
 * No single block may have different instances of the same label applied it.
 * In other words, overlapping regions must not each declare their own instance of some label.
 * They may however declare the same instance on each of them, that is allowed.
 * Labels can be used to represent simple boolean or state or can have data attached to them in the form of fields.
 * Labels are serialized using JAX-RS.
 */
public interface PersistentRegionLabel {

}
