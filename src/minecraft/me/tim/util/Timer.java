package me.tim.util;

public class Timer {
    private long ms;

    public Timer() {
        this.ms = this.getCurrentMS();
    }

    private long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public final long getElapsedTime() {
        return this.getCurrentMS() - this.ms;
    }

    public final boolean elapsed(final long milliseconds) {
        return this.getCurrentMS() - this.ms > milliseconds;
    }

    public final void reset() {
        this.ms = this.getCurrentMS();
    }
}
