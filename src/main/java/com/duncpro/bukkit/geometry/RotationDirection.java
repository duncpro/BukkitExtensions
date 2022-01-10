package com.duncpro.bukkit.geometry;

public enum RotationDirection {
    CLOCKWISE(-1),
    COUNTER_CLOCKWISE(1);

    final int signum;

    RotationDirection(int signum) {
        this.signum = signum;
    }
}
