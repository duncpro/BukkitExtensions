package com.duncpro.bukkit.geometry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;

/**
 * This class provides a mechanism for predicting players future positions based on positional observations
 * made periodically over time.
 */
public interface PlayerHeadingService {
    Optional<Location> getHistoricalPosition(Player player, Duration ago);

    /**
     * Computes the player's heading using the following two points:
     * 1. Player's current position
     * 2. Player's position at (Current Time) - overDuration
     */
    Optional<Heading> getHeading(Player player, Duration overDuration);
    /**
     * Predicts the player's future position after {@code inTime} has passed by computing the player's heading and
     * average speed using historical positional observations.
     */
    Optional<HorizontalPosition> predictPosition(Player player, Duration inTime, Duration headingObservationTimeframe);
}
