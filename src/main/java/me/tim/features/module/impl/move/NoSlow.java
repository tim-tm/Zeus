package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.lwjgl.input.Keyboard;

public class NoSlow extends Module {
    private ModeSetting modeSetting;

    private NoSlowMode mode;

    public NoSlow() {
        super("NoSlow", "Never slow down while blocking!", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.modeSetting = new ModeSetting("Mode", "NoSlow Mode", NoSlowMode.values(), NoSlowMode.VANILLA));
    }

    @EventTarget
    private void onUpdate(EventPreMotion event) {
        this.mode = (NoSlowMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), NoSlowMode.values());
        if (this.mode == null) return;
        this.setSuffix(this.mode.getName());

        switch (this.mode) {
            case GRIM:
                if (Statics.getPlayer().isUsingItem()) Statics.sendPacket(new C08PacketPlayerBlockPlacement(Statics.getPlayer().getCurrentEquippedItem()));
                break;
        }
    }

    public NoSlowMode getMode() {
        return mode;
    }

    public enum NoSlowMode implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla"),
        GRIM("Grim");

        private final String name;

        NoSlowMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
