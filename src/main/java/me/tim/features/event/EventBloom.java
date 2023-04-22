package me.tim.features.event;

import me.tim.features.event.api.Event;

import java.awt.*;

public class EventBloom extends Event {
    private final int width, height;
    private Color bloomColor;

    public EventBloom(Color bloomColor, int width, int height) {
        this.bloomColor = bloomColor;
        this.width = width;
        this.height = height;
    }

    public Color getBloomColor() {
        return bloomColor;
    }

    public void setBloomColor(Color bloomColor) {
        this.bloomColor = bloomColor;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
