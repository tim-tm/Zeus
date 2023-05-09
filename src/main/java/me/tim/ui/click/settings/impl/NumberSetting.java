package me.tim.ui.click.settings.impl;

import me.tim.Statics;
import me.tim.ui.click.settings.Setting;
import me.tim.util.common.MathUtil;
import me.tim.util.render.RenderUtil;
import net.minecraft.util.MathHelper;

import javax.vecmath.Vector2f;
import java.awt.*;

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

    @Override
    public float draw(Vector2f position, Vector2f size, float offset, int mouseX, int mouseY) {
        String settingName = this.getName();
        if (this.isDragging()) {
            float diff = this.getMaxValue() - this.getMinValue();
            float val = this.getMinValue() + (MathHelper.clamp_float((mouseX - position.x) / size.x, 0, 1)) * diff;
            this.setValue(val);

            float factor = MathUtil.percentage(this.getValue() - this.getMinValue(), this.getMaxValue() - this.getMinValue());
            RenderUtil.drawRoundedRect(position.x, position.y + offset + size.y, position.x + (size.x * factor), position.y + size.y * 2 + offset, 6f, new Color(200, 25, 200));
            settingName = String.valueOf(Math.round(this.getValue() * 100D) / 100D);
        }

        if (this.isHovered(new Vector2f(position.x, position.y + offset + size.y), size, mouseX, mouseY)) {
            settingName = String.valueOf(Math.round(this.getValue() * 100D) / 100D);
        }

        Statics.getFontRenderer().drawString(settingName, (int) (position.x + size.x / 2 - Statics.getFontRenderer().getStringWidth(settingName) / 2), (int) (position.y + size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + offset + size.y), -1);
        return size.y;
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
