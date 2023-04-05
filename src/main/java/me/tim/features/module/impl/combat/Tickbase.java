package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventTick;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.input.Keyboard;

public class Tickbase extends Module {
    private NumberSetting maxTicksSetting;

    private int packets, ticks;
    private Timer timer;

    public Tickbase() {
        super("Tickbase", "Tick-manipulation lets you teleport!", Keyboard.KEY_NONE, Category.COMBAT);
        this.timer = new Timer();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.maxTicksSetting = new NumberSetting("Max-Ticks", "Maximum Ticks", 5, 30, 10));
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (this.ticks < this.maxTicksSetting.getValue() && this.packets > 0) {
            Statics.getTimer().timerSpeed = 15f + (this.packets % 2);
            this.ticks++;
        } else if (this.timer.elapsed(10000)) {
            this.ticks = 0;
            this.packets = 0;
            this.timer.reset();
        } else {
            Statics.getTimer().timerSpeed = 1;
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) eventPacket.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
            this.packets++;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.packets = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
