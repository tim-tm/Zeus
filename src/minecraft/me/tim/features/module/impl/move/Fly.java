package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.EventCollide;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class Fly extends Module {
    private ModeSetting modeSetting;
    private NumberSetting speedSetting;
    private FlyModes mode;
    private BlockPos startPos;

    public Fly() {
        super("Fly", "Fly around!", Keyboard.KEY_F, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(modeSetting = new ModeSetting("Modes", "Select different Fly's!", FlyModes.values(), FlyModes.VANILLA));
        this.settings.add(speedSetting = new NumberSetting("Speed", "Adjust your Fly's speed!", 0.265f, 5, 0.265f));
    }

    @EventTarget
    private void onUpdate(EventPreMotion event) {
        this.setSuffix(this.modeSetting.getCurrentMode().getName());

        this.mode = (FlyModes) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), FlyModes.values());
        if (mode == null) return;
        this.setSuffix(this.mode.getName());

        switch (mode) {
            case VANILLA:
                Statics.getPlayer().capabilities.allowFlying = true;
                Statics.getPlayer().capabilities.isFlying = true;
                Statics.speed(this.speedSetting.getValue());
                break;
            case GLIDE:
                Statics.getPlayer().motionY = -0.1f;
                Statics.speed(this.speedSetting.getValue());
                break;
        }
    }

    @EventTarget
    private void onCollide(EventCollide event) {
        if (mode == null) {
            return;
        }

        if (mode.equals(FlyModes.COLLIDE) || this.mode.equals(FlyModes.BLINK)) {
            if (event.getBlock() instanceof BlockAir && event.getBlockPos().getY() < this.startPos.getY()) {
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(
                        event.getBlockPos().getX(),
                        event.getBlockPos().getY(),
                        event.getBlockPos().getZ(),
                        event.getBlockPos().getX() + 1,
                        this.startPos.getY(),
                        event.getBlockPos().getZ() + 1
                ));
                Statics.speed(this.speedSetting.getValue());
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (event.getState().equals(EventPacket.State.RECEIVE) || this.mode == null) return;

        if (this.mode.equals(FlyModes.BLINK) && (event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C07PacketPlayerDigging)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.startPos = Statics.getPlayer().getPosition();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Statics.getPlayer() != null) {
            Statics.getPlayer().capabilities.allowFlying = false;
            Statics.getPlayer().capabilities.isFlying = false;
        }
    }

    enum FlyModes implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla"),
        GLIDE("Glide"),
        COLLIDE("Collide"),
        BLINK("Blink");

        private final String name;

        FlyModes(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
