package me.tim.util.player.rotation;

import me.tim.Statics;
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

    public void apply(BlockUtil util) {
        EntityEgg entityEgg = new EntityEgg(Statics.getWorld());
        entityEgg.posX = util.getPos().getX() + 0.5D;
        entityEgg.posY = util.getPos().getY() + 0.5D;
        entityEgg.posZ = util.getPos().getZ() + 0.5D;
        entityEgg.posX += util.getEnumFacing().getDirectionVec().getX() * 0.25D;
        entityEgg.posY += util.getEnumFacing().getDirectionVec().getY() * 0.25D;
        entityEgg.posZ += util.getEnumFacing().getDirectionVec().getZ() * 0.25D;
        this.apply(entityEgg);
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
