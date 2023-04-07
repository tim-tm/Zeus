package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventUpdate;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

public class Step extends Module {
    private ModeSetting modeSetting;
    private NumberSetting stepHeightSetting;

    public Step() {
        super("Step", "Step higher and faster!", Keyboard.KEY_X, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.modeSetting = new ModeSetting("Mode", "Step-Mode", StepMode.values(), StepMode.VANILLA));
        this.settings.add(this.stepHeightSetting = new NumberSetting("Height", "How high would you like to step?", 1, 5, 2));
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        StepMode mode = (StepMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), StepMode.values());
        if (mode == null) {
            this.setSuffix("");
            return;
        }
        this.setSuffix(mode.getName());

        boolean flag = Statics.getPlayer().isCollidedHorizontally && (Statics.getPlayer().moveForward != 0 || Statics.getPlayer().moveStrafing != 0) && Statics.getPlayer().onGround;
        if (Statics.getPlayer().isInWater() || Statics.getPlayer().isInLava() || Statics.getPlayer().isOnLadder()) {
            return;
        }

        switch (mode) {
            case VANILLA:
                if (flag) Statics.getPlayer().stepHeight = this.stepHeightSetting.getValue();
                break;
            case NCP:
                if (flag) Statics.getPlayer().motionY = 0.42 * MathHelper.sqrt_float(this.stepHeightSetting.getValue());
                break;
            case AAC:
                if (Statics.getPlayer().isAirBorne) {
                    Statics.getTimer().timerSpeed = 0.9f + Statics.getPlayer().ticksExisted % 4 / 20f;
                }

                if (Statics.getPlayer().onGround) {
                    Statics.getPlayer().isAirBorne = false;
                    Statics.getTimer().timerSpeed = 1;
                }

                if (flag) {
                    Statics.getPlayer().isAirBorne = true;
                    Statics.getTimer().timerSpeed = 4;
                    Statics.getPlayer().motionY += 0.45 + ((this.stepHeightSetting.getValue() - 1) * 0.18);
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Statics.getPlayer() != null && Statics.getTimer() != null) {
            Statics.getPlayer().stepHeight = 0.5f;
            Statics.getTimer().timerSpeed = 1;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    private enum StepMode implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla"),
        NCP("NCP"),
        AAC("AAC");

        private final String name;

        StepMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
