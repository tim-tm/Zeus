package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "Configure Settings!", Keyboard.KEY_RSHIFT, Category.RENDER);
    }

    @Override
    protected void setupSettings() { }

    @Override
    public void onEnable() {
        this.setEnabled(false);
    }

    @Override
    public void onToggle() {
        Statics.getMinecraft().displayGuiScreen(Statics.getZeus().clickGUI);
    }
}
