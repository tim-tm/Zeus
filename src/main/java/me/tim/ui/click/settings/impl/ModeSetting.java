package me.tim.ui.click.settings.impl;

import me.tim.Statics;
import me.tim.ui.click.settings.Setting;
import me.tim.util.render.shader.RenderUtil;

import javax.vecmath.Vector2f;
import java.awt.*;

public class ModeSetting extends Setting {
    private ModeTemplate[] modes;
    private ModeTemplate currentMode;
    private boolean extended;
    private float otherOffset;

    public ModeSetting(String name, String description, ModeTemplate[] modes, ModeTemplate defaultMode) {
        super(name, description);
        this.modes = modes;
        this.currentMode = defaultMode;
    }

    @Override
    public float draw(Vector2f position, Vector2f size, float offset, int mouseX, int mouseY) {
        RenderUtil.drawRect(position.x, position.y + offset + size.y, position.x + size.x, position.y + size.y * 2 + offset, new Color(35, 35, 35));
        Statics.getFontRenderer().drawString(this.getName(), (int) (position.x + size.x / 2 - Statics.getFontRenderer().getStringWidth(this.getName()) / 2), (int) (position.y + size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + offset + size.y), -1);

        float offsetN = size.y;
        if (this.extended) offsetN += offset;

        if (this.isExtended()) {
            offsetN += size.y;
            for (ModeSetting.ModeTemplate mode : this.getModes()) {
                RenderUtil.drawRect(position.x, position.y + offsetN, position.x + size.x, position.y + size.y + offsetN, mode.equals(this.getCurrentMode()) ? new Color(200, 25, 200, 255) : new Color(35, 35, 35, 255));
                Statics.getFontRenderer().drawString(mode.getName(), (int) (position.x + size.x / 2 - Statics.getFontRenderer().getStringWidth(mode.getName()) / 2), (int) (position.y + size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + offsetN), -1);

                // Don't increment if it's the last module.
                if (this.getModes()[this.getModes().length - 1].equals(mode)) continue;
                offsetN += size.y;
            }
        }

        if (this.extended) offsetN -= this.otherOffset;
        return offsetN;
    }

    public ModeTemplate[] getModes() {
        return modes;
    }

    public void setModes(ModeTemplate[] modes) {
        this.modes = modes;
    }

    public ModeTemplate getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(ModeTemplate currentMode) {
        this.currentMode = currentMode;
    }

    public void setCurrentMode(String mode) {
        for (ModeTemplate modeTemplate : this.getModes()) {
            if (modeTemplate.getName().equals(mode)) this.currentMode = modeTemplate;
        }
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public void setOtherOffset(float otherOffset) {
        this.otherOffset = otherOffset;
    }

    public interface ModeTemplate {
        String getName();
    }
}
