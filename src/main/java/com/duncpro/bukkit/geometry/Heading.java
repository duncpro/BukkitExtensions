package com.duncpro.bukkit.geometry;

/**
 * Represents a player's heading during some timeframe.
 * @param directionX The direction the player is moving along the x-axis, either {@link CardinalDirection#EAST} or
 *  {@link CardinalDirection#WEST}
 * @param directionZ The direction the player is moving along the z-axis, either {@link CardinalDirection#NORTH} or
 *  {@link CardinalDirection#SOUTH}
 * @param angle The angle of the player's heading where the x axis is 0 and PI and the z axis is PI/2 and 3/2PI.
 * @param speed The average speed at which the player is moving in 3D space. Measured in blocks per tick.
 */
public record Heading(
        CardinalDirection directionX,
        CardinalDirection directionZ,
        double angle,
        double speed
) {}
