package me.tim.features.module.impl.player;

import me.tim.Statics;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cleaner extends Module {
    private NumberSetting delaySetting, startDelaySetting;
    private BooleanSetting onlyInvSetting;

    private int[] bestArmorLessDamage;
    private int[] bestArmorSlots;
    private float bestSwordDamage;
    private int bestSwordSlot;
    private final ArrayList<Integer> trash;

    private final Timer startTimer, timer;

    public Cleaner() {
        super("Cleaner", "Cleans your inventory!", Keyboard.KEY_NONE, Category.PLAYER);
        this.trash = new ArrayList<>();

        this.startTimer = new Timer();
        this.timer = new Timer();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.delaySetting = new NumberSetting("Delay", "Delay between inventory actions!", 0, 500, 125));
        this.settings.add(this.startDelaySetting = new NumberSetting("Start-Delay", "Delay between stealing and opening your inventory!", 0, 500, 125));

        this.settings.add(this.onlyInvSetting = new BooleanSetting("OnlyInv", "Only sort if your inventory is opened!", true));
    }

    @EventTarget
    private void onUpdate(EventPreMotion event) {
        boolean passed =
                Statics.getPlayer().moveForward == 0 && Statics.getPlayer().moveStrafing == 0
                        && Statics.getPlayer().onGround && Statics.getMinecraft().currentScreen == null
                        && !Statics.getPlayer().isUsingItem()
                        && !Statics.getPlayer().isSneaking()
                        && !Statics.getPlayer().isSwingInProgress
                        && !this.onlyInvSetting.getValue();

        if (((Statics.getMinecraft().currentScreen != null && Statics.getMinecraft().currentScreen instanceof GuiInventory) || passed) && this.startTimer.elapsed((long) this.startDelaySetting.getValue())) {
            if (passed) {
                Statics.sendPacket(new C0BPacketEntityAction(Statics.getPlayer(), C0BPacketEntityAction.Action.OPEN_INVENTORY));
            }
            this.searchItems();

            for (int i = 0; i < 4; i++) {
                if (this.bestArmorSlots[i] != -1) {
                    int bestSlot = this.bestArmorSlots[i];

                    ItemStack oldArmor = Statics.getPlayer().inventory.armorItemInSlot(i);
                    if (oldArmor != null && oldArmor.getItem() != null) {
                        Statics.getPlayerController().windowClick(Statics.getPlayer().inventoryContainer.windowId, 8 - i, 0, 1, Statics.getPlayer());
                        if (!this.hasDelayReached()) return;
                    }
                    Statics.getPlayerController().windowClick(Statics.getPlayer().inventoryContainer.windowId, bestSlot < 9 ? bestSlot + 36 : bestSlot, 0, 1, Statics.getPlayer());
                    if (!this.hasDelayReached()) return;
                }
            }

            if (this.bestSwordSlot != -1 && this.bestSwordDamage != -1) {
                Statics.getPlayerController().windowClick(Statics.getPlayer().inventoryContainer.windowId, this.bestSwordSlot < 9 ? this.bestSwordSlot + 36 : this.bestSwordSlot, 0, 2, Statics.getPlayer());
                if (!this.hasDelayReached()) return;
            }

            this.searchTrash();

            for (Integer i : this.trash) {
                Statics.getPlayerController().windowClick(Statics.getPlayer().inventoryContainer.windowId, i < 9 ? i + 36 : i, 0, 4, Statics.getPlayer());
                if (!this.hasDelayReached()) return;
            }

            if (passed) {
                Statics.sendPacket(new C0DPacketCloseWindow(0));
            }
            this.startTimer.reset();
        }
    }

    private boolean hasDelayReached() {
        if (this.timer.elapsed((long) this.delaySetting.getValue())) {
            this.timer.reset();
            return true;
        }
        return false;
    }

    private void searchTrash() {
        this.trash.clear();
        this.bestArmorLessDamage = new int[4];
        this.bestArmorSlots = new int[4];
        this.bestSwordDamage = -1;
        this.bestSwordSlot = -1;

        Arrays.fill(bestArmorLessDamage, -1);
        Arrays.fill(bestArmorSlots, -1);

        List<Integer>[] allItems = new List[4];
        List<Integer> allSwords = new ArrayList<>();

        for (int i = 0; i < bestArmorSlots.length; i++) {
            ItemStack stack = Statics.getPlayer().inventory.armorItemInSlot(i);
            allItems[i] = new ArrayList<>();

            if (stack == null || stack.getItem() == null) continue;

            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                this.bestArmorLessDamage[i] = armor.damageReduceAmount;
                this.bestArmorSlots[i] = 8 + i;
            }
        }

        for (int i = 0; i < 9 * 4; i++) {
            ItemStack stack = Statics.getPlayer().inventory.getStackInSlot(i);
            if (stack == null || stack.getItem() == null) continue;

            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                int aType = 3 - armor.armorType;
                allItems[aType].add(i);

                if (this.bestArmorLessDamage[aType] < armor.damageReduceAmount) {
                    this.bestArmorLessDamage[aType] = armor.damageReduceAmount;
                    this.bestArmorSlots[aType] = i;
                }
            }

            if (stack.getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) stack.getItem();

                allSwords.add(i);
                if (this.bestSwordDamage < sword.getDamageVsEntity()) {
                    this.bestSwordDamage = sword.getDamageVsEntity();
                    this.bestSwordSlot = i;
                }
            }

            if (stack.getItem() instanceof ItemTool) {
                ItemTool sword = (ItemTool) stack.getItem();

                float damage = sword.getToolMaterial().getDamageVsEntity();

                damage -= 1;

                if (this.bestSwordDamage == damage) {
                    this.trash.add(i);
                }

                if (this.bestSwordDamage < damage) {
                    this.bestSwordDamage = damage;
                    this.bestSwordSlot = i;
                }
            }
        }

        for (int i = 0; i < allItems.length; i++) {
            List<Integer> item = allItems[i];
            int finalI = i;
            item.stream().filter(slot -> slot != this.bestArmorSlots[finalI]).forEach(trash::add);
        }
        allSwords.stream().filter(slot -> slot != this.bestSwordSlot).forEach(trash::add);
    }

    private void searchItems() {
        this.bestArmorLessDamage = new int[4];
        this.bestArmorSlots = new int[4];
        this.bestSwordDamage = -1;
        this.bestSwordSlot = -1;

        Arrays.fill(bestArmorLessDamage, -1);
        Arrays.fill(bestArmorSlots, -1);

        for (int i = 0; i < bestArmorSlots.length; i++) {
            ItemStack stack = Statics.getPlayer().inventory.armorItemInSlot(i);
            if (stack == null || stack.getItem() == null) continue;

            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                this.bestArmorLessDamage[i] = armor.damageReduceAmount;
            }
        }

        for (int i = 0; i < 9 * 4; i++) {
            ItemStack stack = Statics.getPlayer().inventory.getStackInSlot(i);
            if (stack == null || stack.getItem() == null) continue;

            if (stack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                int aType = 3 - armor.armorType;

                if (this.bestArmorLessDamage[aType] < armor.damageReduceAmount) {
                    this.bestArmorLessDamage[aType] = armor.damageReduceAmount;
                    this.bestArmorSlots[aType] = i;
                }
            }

            if (stack.getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) stack.getItem();

                if (this.bestSwordDamage < sword.getDamageVsEntity()) {
                    this.bestSwordDamage = sword.getDamageVsEntity();
                    this.bestSwordSlot = i;
                }
            }

            if (stack.getItem() instanceof ItemTool) {
                ItemTool sword = (ItemTool) stack.getItem();

                float damage = sword.getToolMaterial().getDamageVsEntity();

                damage -= 1;

                if (this.bestSwordDamage < damage) {
                    this.bestSwordDamage = damage;
                    this.bestSwordSlot = i;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.startTimer.reset();
        this.timer.reset();
        this.trash.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
