package me.tim.features.event;

import me.tim.features.event.api.Event;

public class EventRender2D extends Event {
    private int width, height;
    private float partialTicks;

    public EventRender2D(int width, int height, float partialTicks) {
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

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
