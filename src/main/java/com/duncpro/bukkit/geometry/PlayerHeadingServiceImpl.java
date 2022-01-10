package com.duncpro.bukkit.geometry;

import com.duncpro.bukkit.log.InjectLogger;
import com.duncpro.bukkit.physics.MinecraftTimeUnit;
import com.duncpro.bukkit.plugin.BukkitServiceImpl;
import com.duncpro.bukkit.plugin.MinecraftGameLoopTask;
import com.duncpro.bukkit.plugin.PluginConfig;
import com.duncpro.bukkit.plugin.PostConstruct;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

import static com.duncpro.bukkit.geometry.PlayerHeadingServiceImpl.*;
import static com.duncpro.bukkit.geometry.Vectors.shift;
import static java.lang.Math.*;

@BukkitServiceImpl(priority = ServicePriority.Normal, service = PlayerHeadingService.class)
@MinecraftGameLoopTask(period = OBSERVATION_PERIOD)
public class PlayerHeadingServiceImpl implements PlayerHeadingService, Runnable, Listener {

    public static final int OBSERVATION_PERIOD = 20 /* ticks, or 1 second */;

    @Inject
    @PluginConfig
    private YamlConfiguration pluginConfig;

    @Inject
    private Server server;

    @InjectLogger
    private Logger logger;

    private final Map<Player, List<Location>> locationObservations = new HashMap<>();

    private int locationHistoryBufferSize;

    @PostConstruct
    public void onEnable() {
        // Don't worry won't actually ever be null. User configuration is automatically merged
        // with the default configuration by the config service.
        //noinspection ConstantConditions
        final var historicalPositionExpiration = Duration.parse(pluginConfig.getString("heading-history-length"));

        locationHistoryBufferSize = Math.toIntExact(historicalPositionExpiration
                .dividedBy(Duration.of(OBSERVATION_PERIOD, MinecraftTimeUnit.TICK)));
    }

    @Override
    public Optional<Heading> getHeading(Player player, Duration overDuration) {
        final var historicalPosition = getHistoricalPosition(player, overDuration);
        final var currentPosition = player.getLocation();

        if (historicalPosition.isEmpty()) return Optional.empty();
        if (!Objects.equals(historicalPosition.get().getWorld(), currentPosition.getWorld())) return Optional.empty();
        if (Objects.equals(currentPosition.toVector(), historicalPosition.get().toVector())) return Optional.empty();

        currentPosition.setY(0);
        historicalPosition.get().setY(0);

        // The directionality is known, we are only concerned about the distances relative
        // to the player's current position.

        final var directionZ = CardinalDirection.forChangeInHorizontalPosition(historicalPosition.get().getZ(),
                currentPosition.getZ(), Axis.Z);

        final var directionX = CardinalDirection.forChangeInHorizontalPosition(historicalPosition.get().getX(),
                currentPosition.getX(), Axis.X);

        //           b
        //         ---- ?  <- Future Position (unknown)
        //        |    /
        //      a |   /  * <- Hypotenuse length (distance in future to predict, known)
        //        |  /
        //        | /
        //    ____* <- Player Current Position (known)
        //   |   /|
        //   |  / |
        //   | /  |
        //   |/   |
        //   * ___|  <- Player Previous Position (known) (0, 0)
        //
        //

        final var distanceZ = abs(historicalPosition.get().getZ() - currentPosition.getZ());
        final var distanceX = abs(historicalPosition.get().getX() - currentPosition.getX());
        final var angle = atan(distanceX / distanceZ);

        final var distanceTraveled = historicalPosition.get().distance(currentPosition);
        final var speed = distanceTraveled / MinecraftTimeUnit.TICK.of(overDuration);

        return Optional.of(new Heading(directionX, directionZ, angle, speed));
    }

    public Optional<Location> getHistoricalPosition(Player player, Duration ago) {
        var requestedIndex = Math.toIntExact(ago.dividedBy(Duration.of(OBSERVATION_PERIOD, MinecraftTimeUnit.TICK)));
        final var actualIndex = min(requestedIndex, locationHistoryBufferSize);
        if (requestedIndex > locationHistoryBufferSize) logger.warning("A plugin is requesting positioning data which" +
                " has expired and can therefore not be provided. THe plugin may not function correctly.");
        final var history = locationObservations.getOrDefault(player, Collections.emptyList());
        if (history.size() <= actualIndex) return Optional.empty();
        return Optional.of(history.get(history.size() - 1 - requestedIndex));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        locationObservations.put(event.getPlayer(), new ArrayList<>());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        locationObservations.remove(event.getPlayer());
    }

    @Override
    public void run() {
        server.getOnlinePlayers().forEach(player -> {
            final var history = locationObservations.computeIfAbsent(player, $ -> new ArrayList<>());
            history.add(player.getLocation());
            if (history.size() > locationHistoryBufferSize) history.remove(0);
        });
    }

    @Override
    public Optional<HorizontalPosition> predictPosition(Player player, Duration inTime, Duration headingObservationTimeframe) {
        final var heading = getHeading(player, headingObservationTimeframe);
        if (heading.isEmpty()) return Optional.empty();
        final var speed = heading.get().speed();

        final var distanceHypotenuse = speed * MinecraftTimeUnit.TICK.of(inTime);

        //      x
        //    ---- ?  <- Future Position (unknown)
        //   |    /
        // z |   /  * <- Hypotenuse length (distance in future to predict, known)
        //   |  /
        //   | /
        //   * <- Player Current Position (known)


        final var distanceX = sin(heading.get().angle()) * distanceHypotenuse;
        final var distanceZ = cos(heading.get().angle()) * distanceHypotenuse;

        final var predictedPosition = new Vector();
        predictedPosition.add(player.getLocation().toVector());
        shift(predictedPosition, heading.get().directionX(), distanceX);
        shift(predictedPosition, heading.get().directionZ(), distanceZ);
        return Optional.of(HorizontalPosition.of(predictedPosition));
    }
}
