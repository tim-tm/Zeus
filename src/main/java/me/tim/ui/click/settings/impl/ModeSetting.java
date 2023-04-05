package me.tim.ui.click.settings.impl;

import me.tim.ui.click.settings.Setting;

public class ModeSetting extends Setting {
    private ModeTemplate[] modes;
    private ModeTemplate currentMode;
    private boolean extended;

    public ModeSetting(String name, String description, ModeTemplate[] modes, ModeTemplate defaultMode) {
        super(name, description);
        this.modes = modes;
        this.currentMode = defaultMode;
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

    public interface ModeTemplate {
        String getName();
    }
}
