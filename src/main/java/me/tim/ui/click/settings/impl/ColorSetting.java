package me.tim.ui.click.settings.impl;

import me.tim.ui.click.settings.Setting;

import javax.vecmath.Vector2f;
import java.awt.*;

public class ColorSetting extends Setting {
    private Color color;

    public ColorSetting(String name, String description, Color defaultColor) {
        super(name, description);
        this.color = defaultColor;
    }

    @Override
    public float draw(Vector2f position, Vector2f size, float offset, int mouseX, int mouseY) {
        return 0;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
