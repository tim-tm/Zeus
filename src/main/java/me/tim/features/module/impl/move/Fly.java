package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.*;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.common.EnumUtil;
import me.tim.util.player.PlayerData;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

public class Fly extends Module {
    private ModeSetting modeSetting;
    private NumberSetting speedSetting;
    private FlyModes mode;
    private PlayerData startData;

    public Fly() {
        super("Fly", "Fly around!", Keyboard.KEY_F, Category.MOVEMENT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(modeSetting = new ModeSetting("Modes", "Select different Fly's!", FlyModes.values(), FlyModes.VANILLA));
        this.settings.add(speedSetting = new NumberSetting("Speed", "Adjust your Fly's speed!", 0.265f, 5, 0.265f));
    }

    @EventTarget
    private void onMove(EventMove eventMove) {
        if (this.mode == null) return;

        if (this.mode.equals(FlyModes.PACKET)) {
            Statics.motionFly(eventMove, true, 2);
        } else {
            Statics.setMoveSpeed(eventMove, this.speedSetting.getValue());
        }
    }

    @EventTarget
    private void onPre(EventPreMotion event) {
        this.setSuffix(this.modeSetting.getCurrentMode().getName());

        this.mode = (FlyModes) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), FlyModes.values());
        if (mode == null) return;
        this.setSuffix(this.mode.getName());

        switch (mode) {
            case VANILLA:
                Statics.getPlayer().capabilities.allowFlying = true;
                Statics.getPlayer().capabilities.isFlying = true;
                break;
            case GLIDE:
                Statics.getPlayer().motionY = -0.1f;
                break;
            case PACKET:
                for (int i = 0; i < 3; i++) {
                    this.startData.sendPacket(false);
                }
                break;
        }
    }

    @EventTarget
    private void onPost(EventPostMotion eventPostMotion) {
        if (this.mode != null && this.mode.equals(FlyModes.PACKET)) {
            for (int i = 0; i < 2; i++) {
                Statics.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(Statics.getPlayer().posX, Statics.getPlayer().posY, Statics.getPlayer().posZ, this.startData.getYaw(), this.startData.getPitch(), false));
            }
        }
    }

    @EventTarget
    private void onCollide(EventCollide event) {
        if (mode == null) {
            return;
        }

        if (mode.equals(FlyModes.COLLIDE) || this.mode.equals(FlyModes.BLINK)) {
            if (event.getBlock() instanceof BlockAir && event.getBlockPos().getY() < this.startData.getPosY()) {
                event.setAxisAlignedBB(AxisAlignedBB.fromBounds(
                        event.getBlockPos().getX(),
                        event.getBlockPos().getY(),
                        event.getBlockPos().getZ(),
                        event.getBlockPos().getX() + 1,
                        this.startData.getPosY(),
                        event.getBlockPos().getZ() + 1
                ));
                Statics.speed(this.speedSetting.getValue());
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (this.mode == null) return;

        switch (this.mode) {
            case BLINK:
                if (event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C07PacketPlayerDigging) {
                    event.setCancelled(true);
                }
                break;
            case PACKET:
                if (event.getPacket() instanceof S08PacketPlayerPosLook) {
                    if (Statics.getPlayer().ticksExisted <= 20) return;

                    S08PacketPlayerPosLook packetPlayerPosLook = (S08PacketPlayerPosLook) event.getPacket();
                    event.setCancelled(true);
                    this.startData.setPosX(packetPlayerPosLook.getX());
                    this.startData.setPosY(packetPlayerPosLook.getY());
                    this.startData.setPosZ(packetPlayerPosLook.getZ());
                    this.startData.setYaw(packetPlayerPosLook.getYaw());
                    this.startData.setPitch(packetPlayerPosLook.getPitch());
                }
                break;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (Statics.getPlayer() != null)
            this.startData = new PlayerData(Statics.getPlayer());
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
        BLINK("Blink"),
        PACKET("Packet");

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
