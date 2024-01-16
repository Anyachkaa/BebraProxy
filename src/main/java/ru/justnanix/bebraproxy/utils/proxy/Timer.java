package ru.justnanix.bebraproxy.utils.proxy;

public class Timer {
    /**
     * How many full ticks have turned over since the last call to updateTimer(), capped at 10.
     */
    public int elapsedTicks;
    public float renderPartialTicks;
    public float field_194148_c;

    public float timerSpeed = 1.0F;

    /**
     * The time reported by the system clock at the last sync, in milliseconds
     */
    private long lastSyncSysClock;
    private float field_194149_e;

    public Timer(float tps) {
        this.field_194149_e = 1000.0F / tps;
        this.lastSyncSysClock = System.currentTimeMillis();
    }

    /**
     * Updates all fields of the Timer using the current time
     */
    public void updateTimer() {
        long i = System.currentTimeMillis();
        this.field_194148_c = (float) (i - this.lastSyncSysClock) * timerSpeed / this.field_194149_e;
        this.lastSyncSysClock = i;
        this.renderPartialTicks += this.field_194148_c;
        this.elapsedTicks = (int) this.renderPartialTicks;
        this.renderPartialTicks -= (float) this.elapsedTicks;
    }
}
