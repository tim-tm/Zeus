package me.tim.util.net;

import net.minecraft.network.Packet;

public class TimePacket {
    private final Packet<?> packet;
    private final long time;

    public TimePacket(Packet<?> packet) {
        this.packet = packet;
        this.time = System.currentTimeMillis();
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public long getTime() {
        return time;
    }
}
