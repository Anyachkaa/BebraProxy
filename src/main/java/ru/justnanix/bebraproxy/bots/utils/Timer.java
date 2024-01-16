package ru.justnanix.bebraproxy.bots.utils;

public class Timer {
    /**
     * How many full ticks have turned over since the last call to updateTimer(), capped at 10.
     */
    public int elapsedTicks;
    public float syncSysClock;

    public float timerSpeed = 1.0F;

    /**
     * The time reported by the system clock at the last sync, in milliseconds
     */
    private long lastSyncSysClock;
    private final float tps;

    public Timer(float tps) {
        this.tps = 1000.0F / tps;
        this.lastSyncSysClock = System.currentTimeMillis();
    }

    /**
     * Updates all fields of the Timer using the current time
     */
    public void updateTimer() {
        long i = System.currentTimeMillis();
        this.syncSysClock = (float) (i - this.lastSyncSysClock) * timerSpeed / this.tps;
        this.lastSyncSysClock = i;
        this.elapsedTicks += this.syncSysClock;
    }
}
