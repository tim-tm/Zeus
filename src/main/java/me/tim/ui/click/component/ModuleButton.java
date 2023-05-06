package me.tim.ui.click.component;

import me.tim.Statics;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.Setting;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.render.RenderUtil;

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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.position.x, this.position.y, this.position.x + this.size.x, this.position.y + this.size.y, this.module.isEnabled() ? new Color(200, 25, 200, 255) : new Color(35, 35, 35, 255));
        Statics.getFontRenderer().drawString(module.getName(), (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(module.getName()) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2), -1);

        if (this.extended && !this.module.getSettings().isEmpty()) {
            this.offset = 0;
            float oOffset = 0;
            for (Setting setting : this.module.getSettings()) {
                if (!setting.isVisible()) continue;
                if (setting instanceof ModeSetting) {
                    ModeSetting modeSetting = (ModeSetting) setting;
                    if (modeSetting.isExtended()) {
                        modeSetting.setOtherOffset(oOffset);
                        oOffset += size.y;
                        oOffset += modeSetting.getModes().length * size.y;
                    }
                }
                this.offset += setting.draw(position, size, offset, mouseX, mouseY);
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
