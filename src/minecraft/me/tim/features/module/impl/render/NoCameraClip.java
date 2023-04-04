package me.tim.features.module.impl.render;

import me.tim.features.module.Category;
import me.tim.features.module.Module;
import org.lwjgl.input.Keyboard;

public class NoCameraClip extends Module {
    public NoCameraClip() {
        super("NoCameraClip", "Clip through walls in third person!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() { }
}
