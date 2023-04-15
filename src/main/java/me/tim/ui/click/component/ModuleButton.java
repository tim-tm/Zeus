package me.tim.ui.click.component;

import me.tim.Statics;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.Setting;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.ColorSetting;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.util.MathHelper;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.io.IOException;

public class ModuleButton implements CUIComponent {
    private final Module module;
    private Vector2f position;
    private Vector2f size;
    public float offset;
    private boolean extended;

    public ModuleButton(Module module, Vector2f position, Vector2f size) {
        this.module = module;
        this.position = position;
        this.size = size;
        this.extended = false;
    }

    private void drawSetting(Setting setting, int mouseX, int mouseY) {
        if (setting instanceof ModeSetting) {
            ModeSetting modeSetting = (ModeSetting) setting;
            RenderUtil.drawRect(this.position.x, this.position.y + this.offset + this.size.y, this.position.x + this.size.x, this.position.y + this.size.y * 2 + this.offset, new Color(35, 35, 35, 215));
            Statics.getFontRenderer().drawString(setting.getName(), (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(setting.getName()) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + this.offset + this.size.y), -1);

            if (modeSetting.isExtended()) {
                float offset = this.offset + this.size.y * 2;
                for (ModeSetting.ModeTemplate mode : modeSetting.getModes()) {
                    RenderUtil.drawRect(this.position.x, this.position.y + offset, this.position.x + this.size.x, this.position.y + this.size.y + offset, mode.equals(modeSetting.getCurrentMode()) ? new Color(200, 25, 200, 255) : new Color(35, 35, 35, 255));
                    Statics.getFontRenderer().drawString(mode.getName(), (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(mode.getName()) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + offset), -1);
                    offset += this.size.y;
                }
                this.offset = offset - this.size.y * 2;
            }
        }

        if (setting instanceof NumberSetting) {
            NumberSetting numberSetting = (NumberSetting) setting;
            RenderUtil.drawRect(this.position.x, this.position.y + this.offset + this.size.y, this.position.x + this.size.x, this.position.y + this.size.y * 2 + this.offset, new Color(35, 35, 35, 215));

            String settingName = setting.getName();
            if (numberSetting.isDragging()) {
                float diff = numberSetting.getMaxValue() - numberSetting.getMinValue();
                float val = numberSetting.getMinValue() + (MathHelper.clamp_float((mouseX - this.position.x) / this.size.x, 0, 1)) * diff;
                numberSetting.setValue(val);

                float factor = numberSetting.getValue() / numberSetting.getMaxValue();
                RenderUtil.drawRect(this.position.x, this.position.y + this.offset + this.size.y, this.position.x + (this.size.x * factor), this.position.y + this.size.y * 2 + this.offset, new Color(200, 25, 200, 255));
                settingName = String.valueOf(Math.round(numberSetting.getValue() * 100D) / 100D);
            }

            if (this.isHovered(new Vector2f(this.position.x, this.position.y + this.offset + this.size.y), this.size, mouseX, mouseY)) {
                settingName = String.valueOf(Math.round(numberSetting.getValue() * 100D) / 100D);
            }

            Statics.getFontRenderer().drawString(settingName, (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(settingName) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + this.offset + this.size.y), -1);
        }

        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting) setting;
            RenderUtil.drawRect(this.position.x, this.position.y + this.offset + this.size.y, this.position.x + this.size.x, this.position.y + this.size.y * 2 + this.offset, booleanSetting.getValue() ? new Color(200, 25, 200, 255) : new Color(35, 35, 35, 255));
            Statics.getFontRenderer().drawString(setting.getName(), (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(setting.getName()) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2 + this.offset + this.size.y), -1);
        }

        if (setting instanceof ColorSetting) {
            ColorSetting colorSetting = (ColorSetting) setting;
            /*float red = MathHelper.clamp_float(colorSetting.getColor().getRed() / 255f, 0, 1);
            float green = MathHelper.clamp_float(colorSetting.getColor().getGreen() / 255f, 0, 1);
            float blue = MathHelper.clamp_float(colorSetting.getColor().getBlue() / 255f, 0, 1);
            */
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.position.x, this.position.y, this.position.x + this.size.x, this.position.y + this.size.y, this.module.isEnabled() ? new Color(200, 25, 200, 255) : new Color(35, 35, 35, 255));
        Statics.getFontRenderer().drawString(module.getName(), (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(module.getName()) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2), -1);

        if (this.extended && !this.module.getSettings().isEmpty()) {
            this.offset = 0;
            for (Setting setting : this.module.getSettings()) {
                if (!setting.isVisible()) continue;

                this.drawSetting(setting, mouseX, mouseY);
                this.offset += this.size.y;
            }
        } else {
            this.offset = 0;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.isHovered(this.position, this.size, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    this.module.toggle();
                    break;
                case 1:
                    this.extended = !this.extended;
                    break;
            }
        }

        if (!this.extended) return;
        int index = 1;
        for (Setting setting : this.module.getSettings()) {
            if (!setting.isVisible()) continue;

            if (this.isHovered(new Vector2f(this.position.x, this.position.y + (index * this.size.y)), this.size, mouseX, mouseY)) {
                switch (mouseButton) {
                    case 0:
                        if (setting instanceof NumberSetting) {
                            NumberSetting numberSetting = (NumberSetting) setting;
                            numberSetting.setDragging(true);
                        }
                        break;
                    case 1:
                        if (setting instanceof ModeSetting) {
                            ((ModeSetting) setting).setExtended(!((ModeSetting) setting).isExtended());
                        }
                        break;
                }
                if (setting instanceof BooleanSetting) {
                    BooleanSetting booleanSetting = (BooleanSetting) setting;
                    booleanSetting.setValue(!booleanSetting.getValue());
                }
            }

            if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;

                int i = index + 1;
                for (ModeSetting.ModeTemplate mode : modeSetting.getModes()) {
                    if (this.isHovered(new Vector2f(this.position.x, this.position.y + (i * this.size.y)), this.size, mouseX, mouseY) && modeSetting.isExtended()) {
                        modeSetting.setCurrentMode(mode);
                    }
                    i++;
                    if (modeSetting.isExtended()) index++;
                }
            }
            index++;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0 && this.extended) {
            for (Setting setting : this.module.getSettings()) {
                if (!setting.isVisible()) continue;

                if (setting instanceof NumberSetting) {
                    NumberSetting numberSetting = (NumberSetting) setting;
                    numberSetting.setDragging(false);
                }
            }
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {

    }

    public Module getModule() {
        return module;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }
}
