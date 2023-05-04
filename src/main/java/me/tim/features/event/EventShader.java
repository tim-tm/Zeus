package me.tim.features.event;

import me.tim.features.event.api.Event;

public class EventShader extends Event {
    private final int width, height;

    public EventShader(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
