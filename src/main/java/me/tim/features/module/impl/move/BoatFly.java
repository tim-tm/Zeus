package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventUpdate;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

public class BoatFly extends Module {
    private NumberSetting boost;

    private boolean riding;

    public BoatFly() {
        super("BoatFly", "Fly with boats!", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.boost = new NumberSetting("Boost", "Amount of Y-Boost", 0.3f, 3, 1.5f));
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        boolean lastRiding = this.riding;
        this.riding = Statics.getPlayer().isRiding();

        if (!this.riding && lastRiding) {
            Statics.getPlayer().setSprinting(true);
            Statics.getPlayer().motionY += this.boost.getValue();

            Statics.speed(this.boost.getValue());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.riding = false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
