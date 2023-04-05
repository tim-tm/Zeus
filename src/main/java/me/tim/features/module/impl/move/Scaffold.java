package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.*;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import me.tim.util.player.BlockUtil;
import me.tim.util.player.rotation.Rotation;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Scaffold extends Module {
    private NumberSetting delaySetting;
    private BooleanSetting safeWalkSetting;

    private final BlockUtil blockUtil;
    private final Rotation rotation;

    private final Timer timer;

    public Scaffold() {
        super("Scaffold", "Bridges for you!", Keyboard.KEY_G, Category.MOVEMENT);
        this.blockUtil = new BlockUtil();
        this.rotation = new Rotation();
        this.timer = new Timer();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.delaySetting = new NumberSetting("Place-Delay", "Delay between block placements!", 0, 500, 125));

        this.settings.add(this.safeWalkSetting = new BooleanSetting("SafeWalk", "Stop on block edges!", true));
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        BlockPos pos = new BlockPos(Statics.getPlayer().posX, Statics.getPlayer().posY - 0.5, Statics.getPlayer().posZ);
        this.blockUtil.findPos(pos);

        if (this.blockUtil.getFacing() == null || this.blockUtil.getPos() == null) return;
        this.rotation.apply(this.blockUtil.getFacing());
    }

    @EventTarget
    private void onRender3d(EventRender3D eventRender3D) {
        if (this.blockUtil == null || this.blockUtil.getPos() == null) return;

        RenderUtil.drawBlockOutline(this.blockUtil.getPos(), new Color(255, 35, 255));
    }

    @EventTarget
    private void onPre(EventPreMotion event) {
        if (this.rotation == null) return;

        event.setYaw(this.rotation.getYaw());
        event.setPitch(this.rotation.getPitch());
        Statics.getPlayer().renderYawOffset = this.rotation.getYaw();
        Statics.getPlayer().rotationYawHead = this.rotation.getYaw();
        Statics.getPlayer().rotationPitchHead = this.rotation.getPitch();

        if (Statics.getPlayer().isSprinting()) {
            Statics.getPlayer().setSprinting(false);
        }
    }

    @EventTarget
    private void onTick(EventTick event) {
        if (this.blockUtil == null || this.blockUtil.getPos() == null || this.blockUtil.getFacing() == null || this.rotation == null) return;

        MovingObjectPosition rayTrace = Statics.getPlayer().rayTrace(Statics.getPlayerController().getBlockReachDistance(), this.rotation.getYaw(), this.rotation.getPitch());
        if (rayTrace == null) return;

        boolean check = rayTrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.blockUtil.getPos().equals(rayTrace.getBlockPos()) && rayTrace.hitVec != null && rayTrace.sideHit != null;
        if (!check) return;

        if (this.timer.elapsed((long) this.delaySetting.getValue()) && Statics.getPlayerController().onPlayerRightClick(Statics.getPlayer(), Statics.getWorld(), Statics.getPlayer().getCurrentEquippedItem(), this.blockUtil.getPos(), rayTrace.sideHit, rayTrace.hitVec)) {
            Statics.getPlayer().swingItem();
            this.timer.reset();
        }
    }

    @EventTarget
    private void onStrafe(EventStrafe eventStrafe) {
        if (this.rotation == null) return;

        final int dif = (int) ((MathHelper.wrapAngleTo180_float(Statics.getPlayer().rotationYaw - this.rotation.getYaw() - 23.5F - 135.0F) + 180.0F) / 45.0F);
        final float strafe = eventStrafe.getStrafe();
        final float forward = eventStrafe.getForward();
        final float friction = eventStrafe.getFriction();
        
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

        if (calcForward > 1.0F || (calcForward < 0.9F && calcForward > 0.3F) || calcForward < -1.0F || (calcForward > -0.9F && calcForward < -0.3F))
            calcForward *= 0.5F;

        if (calcStrafe > 1.0F || (calcStrafe < 0.9F && calcStrafe > 0.3F) || calcStrafe < -1.0F || (calcStrafe > -0.9F && calcStrafe < -0.3F))
            calcStrafe *= 0.5F;

        float d;
        if ((d = calcStrafe * calcStrafe + calcForward * calcForward) >= 1.0E-4F) {
            if ((d = MathHelper.sqrt_float(d)) < 1.0F) {
                d = 1.0F;
            }
            d = friction / d;
            final float yawSin = MathHelper.sin((float) (this.rotation.getYaw() * Math.PI / 180.0));
            final float yawCos = MathHelper.cos((float) (this.rotation.getYaw() * Math.PI / 180.0));
            Statics.getPlayer().motionX += (calcStrafe *= d) * yawCos - (calcForward *= d) * yawSin;
            Statics.getPlayer().motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
        eventStrafe.setCancelled(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.blockUtil.reset();
        this.timer.reset();
    }

    public boolean isSafeWalkEnabled() {
        return this.safeWalkSetting.getValue();
    }
}
