package com.duncpro.bukkit.region.label;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;

public class PlayerMoveRegionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;

    private final PersistentRegion enteredRegion;

    private final PersistentRegion exitedRegion;

    public PlayerMoveRegionEvent(Player player, PersistentRegion enteredRegion, PersistentRegion exitedRegion) {
        this.player = Objects.requireNonNull(player);
        this.enteredRegion = Objects.requireNonNull(enteredRegion);
        this.exitedRegion = Objects.requireNonNull(exitedRegion);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public PersistentRegion getEnteredRegion() {
        return enteredRegion;
    }

    public PersistentRegion getExitedRegion() {
        return exitedRegion;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
