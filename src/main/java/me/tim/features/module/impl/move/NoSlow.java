package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.EventSlowDown;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
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
    private void onPre(EventPreMotion eventPreMotion) {
        this.mode = (NoSlowMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), NoSlowMode.values());
        if (this.mode == null) return;
        this.setSuffix(this.mode.getName());

        if (Statics.getPlayer().isUsingItem()) {
            switch (this.mode) {
                case GRIM:
                    Statics.sendPacket(new C09PacketHeldItemChange(Statics.getPlayer().inventory.currentItem % 8 + 1));
                    Statics.sendPacket(new C09PacketHeldItemChange(Statics.getPlayer().inventory.currentItem));
                    break;
            }
        }
    }

    @EventTarget
    private void onSlowDown(EventSlowDown event) {
        if (this.mode == null) return;

        switch (this.mode) {
            case VANILLA:
            case GRIM:
                event.setCancelled(true);
                break;
            case NCP:
                event.setCancelled(true);
                Statics.sendPacket(new C08PacketPlayerBlockPlacement(Statics.getPlayer().getCurrentEquippedItem()));
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
