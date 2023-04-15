package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventTick;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class Tickbase extends Module {
    private NumberSetting maxTicksSetting;

    private int ticks, cPackets;
    private boolean ignored;

    public Tickbase() {
        super("Tickbase", "Tick-manipulation lets you teleport!", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.maxTicksSetting = new NumberSetting("Max-Ticks", "Max-Ticks", 1, 10, 3));
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) eventPacket.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
            this.cPackets++;
        }
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (this.ignored) return;

        if (this.ticks <= this.maxTicksSetting.getValue() && this.cPackets > 0) {
            this.ignored = true;
            try {
                float speed = 1f / (this.ticks / 2f);
                speed = MathHelper.clamp_float(speed, 0.05f, 1);

                Statics.getTimer().timerSpeed = this.ticks > 1 ?  speed : 1;
                Statics.getMinecraft().runTick();
                this.ticks++;
            } catch (IOException ignored) { }
            this.ignored = false;
        } else {
            this.ticks = 0;
            this.ignored = false;
            this.cPackets = 0;
            Statics.getTimer().timerSpeed = 1;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.ignored = false;
        this.ticks = 0;

        if (Statics.getTimer() != null)
            Statics.getTimer().timerSpeed = 1;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
