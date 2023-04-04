package me.tim.features.event;

import me.tim.features.event.api.EventCancelable;
import net.minecraft.network.Packet;

public class EventPacket extends EventCancelable {
    private Packet<?> packet;
    private State state;

    public EventPacket(Packet<?> packet, State state) {
        this.packet = packet;
        this.state = state;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        SEND,
        RECEIVE;
    }
}
