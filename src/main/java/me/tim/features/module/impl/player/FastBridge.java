package me.tim.features.module.impl.player;

import me.tim.Statics;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class FastBridge extends Module {
    public FastBridge() {
        super("FastBridge", "Sneak on the edge of blocks!", Keyboard.KEY_NONE, Category.PLAYER);
    }

    @Override
    protected void setupSettings() { }

    @EventTarget
    private void onUpdate(EventPreMotion event) {
        Statics.getGameSettings().keyBindSprint.pressed = Statics.getWorld().getBlockState(new BlockPos(Statics.getPlayer().posX, Statics.getPlayer().posY - 1, Statics.getPlayer().posZ)).getBlock() instanceof BlockAir;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Statics.getGameSettings().keyBindSprint.pressed = false;
    }
}
