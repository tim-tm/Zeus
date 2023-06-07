package me.tim.util.player.rotation;

import me.tim.Statics;
import me.tim.features.event.EventStrafe;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.player.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

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
        this.lastYaw = Statics.getPlayer().prevRotationYaw;
        this.lastPitch = Statics.getPlayer().prevRotationPitch;
    }

    public void strafe(EventStrafe eventStrafe, StrafeMode mode, boolean keepSprint) {
        if (mode.equals(StrafeMode.OFF) || this.getYaw() == eventStrafe.getYaw()) return;

        if (Statics.getPlayer().isSprinting() && !keepSprint) {
            eventStrafe.setFriction(Statics.getPlayer().onGround ? 0.09999999f : 0.02f);
            Statics.getPlayer().setSprinting(false);
        }

        switch (mode) {
            case SILENT:
                final int dif = (int) ((MathHelper.wrapAngleTo180_float(Statics.getPlayer().rotationYaw - this.getYaw() - 23.5F - 135.0F) + 180.0F) / 45.0F);
                final float strafe = eventStrafe.getStrafe();
                final float forward = eventStrafe.getForward();
                float friction = eventStrafe.getFriction();

                float calcForward = 0.0F;
                float calcStrafe = 0.0F;
                switch (dif) {
                    case 0: {
                        calcForward = forward;
                        calcStrafe = strafe;
                        break;
                    }

                    case 1: {
                        calcForward += forward;
                        calcStrafe -= forward;
                        calcForward += strafe;
                        calcStrafe += strafe;
                        break;
                    }

                    case 2: {
                        calcForward = strafe;
                        calcStrafe = -forward;
                        break;
                    }

                    case 3: {
                        calcForward -= forward;
                        calcStrafe -= forward;
                        calcForward += strafe;
                        calcStrafe -= strafe;
                        break;
                    }

                    case 4: {
                        calcForward = -forward;
                        calcStrafe = -strafe;
                        break;
                    }

                    case 5: {
                        calcForward -= forward;
                        calcStrafe += forward;
                        calcForward -= strafe;
                        calcStrafe -= strafe;
                        break;
                    }

                    case 6: {
                        calcForward = -strafe;
                        calcStrafe = forward;
                        break;
                    }

                    case 7: {
                        calcForward += forward;
                        calcStrafe += forward;
                        calcForward -= strafe;
                        calcStrafe += strafe;
                        break;
                    }
                }

                if (calcForward > 1.0F || (calcForward < 0.9F && calcForward > 0.3F) || calcForward < -1.0F || (calcForward > -0.9F && calcForward < -0.3F)) {
                    calcForward *= 0.5F;
                }

                if (calcStrafe > 1.0F || (calcStrafe < 0.9F && calcStrafe > 0.3F) || calcStrafe < -1.0F || (calcStrafe > -0.9F && calcStrafe < -0.3F)) {
                    calcStrafe *= 0.5F;
                }

                float d;
                if ((d = calcStrafe * calcStrafe + calcForward * calcForward) >= 1.0E-4F) {
                    if ((d = MathHelper.sqrt_float(d)) < 1.0F) {
                        d = 1.0F;
                    }
                    d = friction / d;
                    final float yawSin = MathHelper.sin((float) (this.getYaw() * Math.PI / 180.0));
                    final float yawCos = MathHelper.cos((float) (this.getYaw() * Math.PI / 180.0));
                    Statics.getPlayer().motionX += (calcStrafe *= d) * yawCos - (calcForward *= d) * yawSin;
                    Statics.getPlayer().motionZ += calcForward * yawCos + calcStrafe * yawSin;
                }
                eventStrafe.setCancelled(true);
                break;
            case STRICT:
                eventStrafe.setYaw(this.getYaw());
                break;
        }
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

    public enum StrafeMode implements ModeSetting.ModeTemplate {
        OFF("Off"),
        STRICT("Strict"),
        SILENT("Silent");

        private final String name;

        StrafeMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
