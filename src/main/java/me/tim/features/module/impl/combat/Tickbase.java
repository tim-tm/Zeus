package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventTick;
import me.tim.features.event.EventUpdate;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class Tickbase extends Module {
    private NumberSetting rangeSetting;

    private int skippedTicks, preTick;
    private KillAura killAura;
    private boolean ignored;

    public Tickbase() {
        super("Tickbase", "Tick-manipulation lets you teleport!", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.rangeSetting = new NumberSetting("Range", "Teleport-Range", 1, 8, 3));
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        this.killAura = (KillAura) Statics.getZeus().moduleManager.getModuleByClass(KillAura.class);
        if (this.killAura == null) return;

        if ((Statics.getPlayer().moveStrafing == 0 && Statics.getPlayer().moveForward == 0) || this.killAura.getCurrTarget() == null) {
            Statics.getTimer().timerSpeed = 1;
        }
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (this.ignored || this.killAura == null) return;

        if (this.killAura.getCurrTarget() == null) {
            this.sleep();
        } else {
            if (this.shouldChoke()) {
                this.ignored = true;
                try {
                    Statics.getMinecraft().runTick();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.ignored = false;
            } else {
                this.sleep();
            }
        }
    }

    private void sleep() {
        if (this.skippedTicks > 0) {
            try {
                Thread.sleep(2L * this.skippedTicks);
                this.skippedTicks = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Statics.getTimer().timerSpeed = 0.054f + this.skippedTicks;
        }
    }

    public boolean shouldChoke() {
        if (this.killAura.getCurrTarget() == null || this.skippedTicks > 5) return false;
        final double dx = Statics.getPlayer().posX - this.killAura.getCurrTarget().posX, dz = Statics.getPlayer().posZ - this.killAura.getCurrTarget().posZ;
        if (MathHelper.sqrt_double(dx * dx + dz * dz) > this.rangeSetting.getValue()) {
            this.preTick = (int) (2 * (MathHelper.sqrt_double(dx * dx + dz * dz) - this.rangeSetting.getValue()));
            this.skippedTicks += preTick;
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.skippedTicks = 0;
        this.ignored = false;
        this.preTick = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
