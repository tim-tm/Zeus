package me.tim.ui.click.settings.impl;

import me.tim.ui.click.settings.Setting;

import java.awt.*;

public class ColorSetting extends Setting {
    private Color color;

    public ColorSetting(String name, String description, Color defaultColor) {
        super(name, description);
        this.color = defaultColor;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
