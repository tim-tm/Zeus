package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.EventTick;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;

public class Velocity extends Module {
    private ModeSetting modeSetting;
    private NumberSetting percentageSetting, ticksSetting;

    private VelocityModes mode;
    private boolean actionRequested;
    private int ticks;

    public Velocity() {
        super("Velocity", "Modify your Velocity!", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(modeSetting = new ModeSetting("Modes", "Select different Velocity's!", VelocityModes.values(), VelocityModes.ZERO));
        this.settings.add(this.percentageSetting = new NumberSetting("Percentage", "Percentage of knockback!", 0, 100, 50));
        this.settings.add(this.ticksSetting = new NumberSetting("Ticks", "Cancel-Ticks!", 1, 10, 3));
    }

    @EventTarget
    private void onUpdate(EventPreMotion event) {
        this.mode = (VelocityModes) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), VelocityModes.values());
        if (this.mode == null) return;

        this.percentageSetting.setVisible(this.mode.equals(VelocityModes.PERCENTAGE) || this.mode.equals(VelocityModes.REVERSE));
        this.ticksSetting.setVisible(this.mode.equals(VelocityModes.TICK));

        String suffix = this.mode.getName();
        if (this.mode.equals(VelocityModes.PERCENTAGE)) {
            suffix = this.percentageSetting.getValue() + "%";
        }

        this.setSuffix(suffix);

        if (this.actionRequested) {
            switch (this.mode) {
                case SNEAK:
                    Statics.sendPacket(new C0BPacketEntityAction(Statics.getPlayer(), C0BPacketEntityAction.Action.START_SNEAKING));
                    Statics.sendPacket(new C0BPacketEntityAction(Statics.getPlayer(), C0BPacketEntityAction.Action.STOP_SNEAKING));
                    this.actionRequested = false;
                    break;
                case GRIM:
                    if (this.ticks > 1 && this.ticks <= 8) {
                        Statics.multMotion(0.265f / this.ticks);
                    } else if (this.ticks > 8) {
                        this.actionRequested = false;
                    }
                    break;
                case TICK:
                    if (this.ticks > this.ticksSetting.getValue()) {
                        Statics.getPlayer().motionX = 0;
                        Statics.getPlayer().motionY = 0;
                        Statics.getPlayer().motionZ = 0;
                        this.actionRequested = false;
                    }
                    break;
            }
        }
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (this.actionRequested) {
            this.ticks++;
        } else {
            this.ticks = 0;
        }
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (event.getState().equals(EventPacket.State.SEND)) return;

        if (!(event.getPacket() instanceof S12PacketEntityVelocity)) return;
        S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

        if (packet.entityID != Statics.getPlayer().getEntityId()) return;
        if (this.mode == null) return;

        switch (this.mode) {
            case ZERO:
                packet.motionX = 0;
                packet.motionY = 0;
                packet.motionZ = 0;
                event.setPacket(packet);
                break;
            case CANCEL:
                event.setCancelled(true);
                break;
            case ONLYY:
                packet.motionX = 0;
                packet.motionZ = 0;
                event.setPacket(packet);
                break;
            case JUMP:
                if (Statics.getPlayer().onGround) {
                    Statics.getPlayer().jump();
                }
                break;
            case PERCENTAGE:
                packet.motionX *= this.percentageSetting.getValue() / 100.f;
                packet.motionY *= this.percentageSetting.getValue() / 100.f;
                packet.motionZ *= this.percentageSetting.getValue() / 100.f;
                event.setPacket(packet);
                break;
            case GRIM:
            case SNEAK:
            case TICK:
                this.actionRequested = true;
                break;
            case REVERSE:
                packet.motionX = (int) (-packet.motionX * (this.percentageSetting.getValue() / 100.f));
                packet.motionZ = (int) (-packet.motionZ * (this.percentageSetting.getValue() / 100.f));
                break;
            case INVALID:
                Statics.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(1337, 69, 420, false));
                break;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.actionRequested = false;
    }

    private enum VelocityModes implements ModeSetting.ModeTemplate {
        ZERO("Zero"),
        CANCEL("Cancel"),
        ONLYY("OnlyY"),
        JUMP("Jump"),
        PERCENTAGE("Percentage"),
        SNEAK("Sneak"),
        GRIM("Grim"),
        TICK("Tick"),
        REVERSE("Reverse"),
        INVALID("Invalid");

        private final String name;

        VelocityModes(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
