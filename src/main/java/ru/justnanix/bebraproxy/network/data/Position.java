package ru.justnanix.bebraproxy.network.data;

import lombok.Data;

@Data
public class Position {
    private final double x, y, z;
    private float yaw, pitch;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
