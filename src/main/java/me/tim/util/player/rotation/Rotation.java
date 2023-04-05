package me.tim.util.player.rotation;

import me.tim.Statics;
import me.tim.util.common.MathUtil;
import me.tim.util.player.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

import javax.vecmath.Vector2f;

public class Rotation {
    private float lastYaw, lastPitch, yaw, pitch;

    public void apply(Entity entity) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;

        Vector2f target = RotationUtil.getRotations(entity);
        float f = Statics.getGameSettings().mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 1.2F;
        float f2 = target.x - this.yaw;
        float f3 = target.y - this.pitch;
        f2 -= f2 % f1;
        f3 -= f3 % f1;

        this.yaw = this.lastYaw + f2;
        this.pitch = this.lastPitch + f3;
    }

    public void apply(Vec3 vec3) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;

        Vector2f target = RotationUtil.getRotations(vec3);
        float f = Statics.getGameSettings().mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 1.2F;
        float f2 = target.x - this.yaw;
        float f3 = target.y - this.pitch;
        f2 -= f2 % f1;
        f3 -= f3 % f1;

        this.yaw = this.lastYaw + f2;
        this.pitch = this.lastPitch + f3;
    }

    public void apply(float newYaw, float newPitch) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;

        float f = Statics.getGameSettings().mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 1.2F;
        float f2 = newYaw - this.yaw;
        float f3 = newPitch - this.pitch;
        f2 -= f2 % f1;
        f3 -= f3 % f1;

        this.yaw = this.lastYaw + f2;
        this.pitch = this.lastPitch + f3;
    }

    public void apply(Vec3i facing) {
        Vec3 pos = new Vec3(new BlockPos(Statics.getPlayer().posX, Statics.getPlayer().posY - 0.5, Statics.getPlayer().posZ));

        Vector2f blockFace = RotationUtil.faceVector(pos.add(new Vec3(facing)).addVector(0.5d, -3d, 0.5d));
        this.apply(blockFace.x, blockFace.y);
    }

    public void reset() {
        this.yaw = Statics.getPlayer().rotationYaw;
        this.pitch = Statics.getPlayer().rotationPitch;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
