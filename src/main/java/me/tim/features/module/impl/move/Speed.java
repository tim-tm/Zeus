package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventMove;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.common.EnumUtil;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {
    private ModeSetting modeSetting;
    private NumberSetting vanillaSpeedSetting;

    private SpeedMode mode;

    public Speed() {
        super("Speed", "Move faster!", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.modeSetting = new ModeSetting("Mode", "Speed-Mode", SpeedMode.values(), SpeedMode.VANILLA));

        this.settings.add(this.vanillaSpeedSetting = new NumberSetting("Speed", "Vanilla Speed", 0.265f, 5, 0.3f));
    }

    @EventTarget
    private void onMove(EventMove eventMove) {
        this.mode = (SpeedMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), SpeedMode.values());
        if (this.mode == null) return;
        this.setSuffix(this.mode.getName());
        this.vanillaSpeedSetting.setVisible(this.mode.equals(SpeedMode.VANILLA));

        switch (this.mode) {
            case VANILLA:
                Statics.setMoveSpeed(eventMove, this.vanillaSpeedSetting.getValue());
                break;
        }
    }

    private enum SpeedMode implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla");

        private final String name;

        SpeedMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
