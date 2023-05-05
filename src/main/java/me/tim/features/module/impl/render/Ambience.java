package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventTick;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import org.lwjgl.input.Keyboard;

public class Ambience extends Module {
    private NumberSetting worldTimeSetting;

    public Ambience() {
        super("Ambience", "Change the world time!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.worldTimeSetting = new NumberSetting("Time", "Time to be set!", 0, 24, 23));
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.getState().equals(EventPacket.State.SEND)) return;
        if (eventPacket.getPacket() instanceof S03PacketTimeUpdate) {
            eventPacket.setCancelled(true);
        }
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (Statics.getWorld() == null || Statics.getPlayer() == null) return;
        Statics.getWorld().setWorldTime((long) (this.worldTimeSetting.getValue() * 1000));
    }
}
