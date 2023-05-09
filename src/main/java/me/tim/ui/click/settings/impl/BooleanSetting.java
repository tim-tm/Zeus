package me.tim.ui.click.settings.impl;

import me.tim.Statics;
import me.tim.ui.click.settings.Setting;
import me.tim.util.render.RenderUtil;

import javax.vecmath.Vector2f;
import java.awt.*;

public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description);
        this.value = defaultValue;
    }

    @Override
    public float draw(Vector2f position, Vector2f size, float offset, int mouseX, int mouseY) {
        if (this.getValue()) RenderUtil.drawRoundedRect(position.x, position.y + offset + size.y, position.x + size.x, position.y + size.y * 2 + offset, 6f, new Color(200, 25, 200));
        Statics.getFontRenderer().drawString(this.getName(), (int) (position.x + size.x / 2 - Statics.getFontRenderer().getStringWidth(this.getName()) / 2), (int) (position.y + size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + offset + size.y), -1);
        return size.y;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
