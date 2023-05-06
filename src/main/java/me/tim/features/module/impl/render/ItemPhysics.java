package me.tim.features.module.impl.render;

import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

public class ItemPhysics extends Module {
    private NumberSetting timerSetting;

    public ItemPhysics() {
        super("ItemPhysics", "Different physics for items!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.timerSetting = new NumberSetting("Speed", "How fast should Items fall!", 0.15f, 2f, 1f));
    }

    public NumberSetting getTimerSetting() {
        return timerSetting;
    }
}
