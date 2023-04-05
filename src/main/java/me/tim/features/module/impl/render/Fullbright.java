package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import org.lwjgl.input.Keyboard;

public class Fullbright extends Module {

    public Fullbright() {
        super("Fullbright", "Brighten the world!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() { }

    @Override
    public void onEnable() {
        super.onEnable();
        Statics.getGameSettings().saturation = 100;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Statics.getGameSettings().saturation = 1;
    }
}
