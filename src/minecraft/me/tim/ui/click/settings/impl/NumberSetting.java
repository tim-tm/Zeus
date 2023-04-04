package me.tim.ui.click.settings.impl;

import me.tim.ui.click.settings.Setting;

public class NumberSetting extends Setting {
    private float value;
    private final float minValue, maxValue;
    private boolean dragging;

    public NumberSetting(String name, String description, float minValue, float maxValue, float defaultValue) {
        super(name, description);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = defaultValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }
}
