package com.duncpro.bukkit.physics;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public class Tick implements TemporalUnit {
    @Override
    public Duration getDuration() {
        return Duration.ofMillis(50);
    }

    @Override
    public boolean isDurationEstimated() {
        return false;
    }

    @Override
    public boolean isDateBased() {
        return false;
    }

    @Override
    public boolean isTimeBased() {
        return false;
    }

    @Override
    public <R extends Temporal> R addTo(R temporal, long amount) {
        return ChronoUnit.MILLIS.addTo(temporal, 50 * amount);
    }

    @Override
    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return ChronoUnit.MILLIS.between(temporal1Inclusive, temporal2Exclusive) / 50;
    }

    public long of(Duration duration) {
        return duration.toMillis() / 50;
    }
}
