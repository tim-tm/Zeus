package me.tim.util.player;

import me.tim.Statics;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;

public class PlayerData {
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;

    public PlayerData(double posX, double posY, double posZ, float yaw, float pitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PlayerData(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }

    public void sendPacket(boolean onGround) {
        Statics.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(this.posX, this.posY, this.posZ, this.yaw, this.pitch, onGround));
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
