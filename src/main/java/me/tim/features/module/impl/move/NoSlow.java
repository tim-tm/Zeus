package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventSlowDown;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.Timer;
import me.tim.util.common.EnumUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector2f;

public class NoSlow extends Module {
    private ModeSetting modeSetting;

    private NoSlowMode mode;
    private final Timer timer;

    public NoSlow() {
        super("NoSlow", "Never slow down while blocking!", Keyboard.KEY_NONE, Category.MOVEMENT);
        this.timer = new Timer();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.modeSetting = new ModeSetting("Mode", "NoSlow Mode", NoSlowMode.values(), NoSlowMode.VANILLA));
    }

    @EventTarget
    private void onSlowDown(EventSlowDown event) {
        this.mode = (NoSlowMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), NoSlowMode.values());
        if (this.mode == null) return;
        this.setSuffix(this.mode.getName());

        switch (this.mode) {
            case VANILLA:
                event.setCancelled(true);
                break;
            case NCP:
                event.setCancelled(true);
                Statics.sendPacket(new C08PacketPlayerBlockPlacement(Statics.getPlayer().getCurrentEquippedItem()));
                break;
            case GRIM:
                event.setMultiplier(new Vector2f(0.4f, 0.4f));
                break;
        }
    }

    public NoSlowMode getMode() {
        return mode;
    }

    public enum NoSlowMode implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla"),
        NCP("NCP"),
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
