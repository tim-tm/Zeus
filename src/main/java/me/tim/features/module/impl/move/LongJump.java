package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventMove;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

public class LongJump extends Module {
    private NumberSetting factorSetting;

    private int ticks;
    private float motion;

    public LongJump() {
        super("LongJump", "Jump further!", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.factorSetting = new NumberSetting("Factor", "Movement Factor!", 0, 2, 0.215f));
    }

    @EventTarget
    private void onMove(EventMove eventMove) {
        if (Statics.getPlayer() == null) return;

        float val = this.factorSetting.getValue();
        if (Statics.getPlayer().onGround) {
            if (this.ticks != 0) {
                eventMove.setX(0);
                eventMove.setY(0);
                eventMove.setZ(0);
                return;
            }
            Statics.getPlayer().jump();
            Statics.setMoveSpeed(eventMove, val * 1.4f);
            this.ticks = 0;
        } else {
            switch (this.ticks) {
                case 0:
                    this.motion = (float) (Statics.getCacheSpeed() * 1.7f);
                    break;
                case 5:
                    this.motion *= 1.04f;
                    Statics.getPlayer().motionY += 0.032;
                    this.motion *= 0.982;
                    break;
                case 6:
                    Statics.getPlayer().motionY += 0.02;
                    this.motion *= 0.982;
                    break;
                case 9:
                    Statics.getPlayer().motionY *= 0.74;
                    this.motion *= 0.982;
                    break;
            }
            this.ticks++;
            Statics.setMoveSpeed(eventMove, this.motion);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.ticks = 0;
    }
}
