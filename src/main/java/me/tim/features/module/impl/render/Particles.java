package me.tim.features.module.impl.render;

import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

public class Particles extends Module {
    private NumberSetting scaleSetting;

    public Particles() {
        super("Particles", "Better Particles!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.scaleSetting = new NumberSetting("Scale", "Scale particles!", 0, 4, 1));
    }

    public NumberSetting getScaleSetting() {
        return scaleSetting;
    }
}
