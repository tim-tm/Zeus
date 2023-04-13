package me.tim.features.event;

import me.tim.features.event.api.Event;

public class EventBloom extends Event {
    private int width, height;

    public EventBloom(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
