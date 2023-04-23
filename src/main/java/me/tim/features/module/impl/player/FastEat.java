package me.tim.features.module.impl.player;

import me.tim.Statics;
import me.tim.features.event.EventUpdate;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

public class FastEat extends Module {
    public FastEat() {
        super("FastEat", "Eat faster!", Keyboard.KEY_NONE, Category.PLAYER);
    }

    @Override
    protected void setupSettings() { }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (Statics.getPlayer().isEating() && Statics.getPlayer().ticksExisted % 2 == 0) {
            Statics.sendPacket(new C03PacketPlayer(true));
        }
    }
}
