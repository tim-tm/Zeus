package me.tim.features.module.impl.player;

import me.tim.Statics;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.player.InventoryUtil;
import me.tim.util.common.MathUtil;
import me.tim.util.Timer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

public class Stealer extends Module {
    private NumberSetting delaySetting, startDelaySetting;
    private BooleanSetting onlyChestTitleSetting, randomizeSetting, smartSetting;
    private final Timer timer, startTimer;

    public Stealer() {
        super("Stealer", "Steal chest automatically!", Keyboard.KEY_NONE, Category.PLAYER);
        this.timer = new Timer();
        this.startTimer = new Timer();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.delaySetting = new NumberSetting("Steal-Delay", "Time between every pick!", 0, 500, 125));
        this.settings.add(this.startDelaySetting = new NumberSetting("Start-Delay", "Time waiting before starting to steal!", 0, 1000, 125));
        this.settings.add(this.randomizeSetting = new BooleanSetting("Randomize Delay", "Make the delay a little random!", true));
        this.settings.add(this.onlyChestTitleSetting = new BooleanSetting("Only Chest-Title", "Only steal chests!", true));
        this.settings.add(this.smartSetting = new BooleanSetting("Smart", "Only steal useful items!", true));
    }

    @EventTarget
    private void onUpdate(EventPreMotion event) {
        if (Statics.getPlayer().openContainer instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) Statics.getPlayer().openContainer;

            if (InventoryUtil.isChestEmpty(chest) || InventoryUtil.isInventoryFull()) {
                Statics.getPlayer().closeScreen();
            }

            if (this.onlyChestTitleSetting.getValue() && !chest.getLowerChestInventory().getName().equalsIgnoreCase(I18n.format("chest"))) return;
            if (this.startDelaySetting.getValue() > 0 && !this.startTimer.elapsed((long) this.startDelaySetting.getValue())) return;

            for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);
                if (stack == null) continue;
                if (this.smartSetting.getValue() && (InventoryUtil.isTrash(stack) || InventoryUtil.inventoryContainsOrBetter(stack))) continue;

                long random = this.randomizeSetting.getValue() ? MathUtil.random(0, 30).longValue() : 0L;
                if (this.timer.elapsed((long) this.delaySetting.getValue() + random)) {
                    Statics.getPlayerController().windowClick(chest.windowId, i, 0, 1, Statics.getPlayer());
                    this.timer.reset();
                }
            }
        } else {
            this.startTimer.reset();
        }
    }
}
