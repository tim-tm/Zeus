package me.tim.features.event;

import me.tim.features.event.api.EventCancelable;

import javax.vecmath.Vector2f;

public class EventSlowDown extends EventCancelable {
    private Vector2f multiplier;

    public EventSlowDown(Vector2f multiplier) {
        this.multiplier = multiplier;
    }

    public Vector2f getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Vector2f multiplier) {
        this.multiplier = multiplier;
    }
}
