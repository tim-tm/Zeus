package me.tim.util.player;

import me.tim.Statics;
import net.minecraft.block.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;

public class InventoryUtil {
    public static boolean isInventoryFull() {
        for (int i = 9; i <= 44; i++) {
            ItemStack stack = Statics.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTrash(ItemStack stack) {
        return !(stack.getItem() instanceof ItemBlock)
                && !(stack.getItem() instanceof ItemArmor)
                && !(stack.getItem() instanceof ItemFood)
                && !(stack.getItem() instanceof ItemSword)
                && !(stack.getItem() instanceof ItemTool)
                && !(stack.getItem() instanceof ItemFishingRod)
                && !(stack.getItem() instanceof ItemFireball)
                && !(stack.getItem() instanceof ItemPotion)
                && !(stack.getItem() instanceof ItemBucket)
                && !(stack.getItem() instanceof ItemBow)
                && stack.getItem() != Items.arrow
                && stack.getItem() != Items.ender_eye;
    }

    public static boolean inventoryContainsOrBetter(ItemStack stack) {
        for (int i = 9; i <= 44; i++) {
            ItemStack invStack = Statics.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (invStack == null) continue;

            if (stack.getItem() instanceof ItemTool && invStack.getItem() instanceof ItemTool) {
                ItemTool tool = (ItemTool) stack.getItem();
                ItemTool invTool = (ItemTool) invStack.getItem();

                return invTool.damageVsEntity > tool.damageVsEntity;
            }

            if (stack.getItem() instanceof ItemSword && invStack.getItem() instanceof ItemSword) {
                ItemSword sword = (ItemSword) stack.getItem();
                ItemSword invSword = (ItemSword) invStack.getItem();

                return invSword.getDamageVsEntity() > sword.getDamageVsEntity();
            }

            if (stack.getItem() instanceof ItemArmor && invStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) stack.getItem();
                ItemArmor invArmor = (ItemArmor) invStack.getItem();

                if (armor.armorType == invArmor.armorType) {
                    return invArmor.damageReduceAmount > armor.damageReduceAmount;
                }
            }

            if ((!(invStack.getItem() instanceof ItemBlock) && !(invStack.getItem() instanceof ItemFood)) && invStack.equals(stack)) return true;
        }
        return false;
    }

    public static boolean isChestEmpty(ContainerChest chest) {
        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(i);
            if (stack != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnnoyable(ItemStack itemStack) {
        return itemStack != null
                && (itemStack.getItem() instanceof ItemFishingRod || itemStack.getItem() instanceof ItemSnowball || itemStack.getItem() instanceof ItemEgg);
    }

    public static boolean isBlock(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemBlock)) return false;
        ItemBlock block = (ItemBlock) stack.getItem();
        if (block.getBlock() == null) return false;

        boolean condition = block.getBlock() instanceof BlockChest || block.getBlock() instanceof BlockEnderChest || block.getBlock() instanceof BlockSlime;
        return !condition;
    }

    public static ItemInformation searchAnnoyable() {
        for (int i = 36; i <= 44; i++) {
            ItemStack itemStack = Statics.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (InventoryUtil.isAnnoyable(itemStack)) {
                return new ItemInformation(itemStack, i - 36);
            }
        }
        return null;
    }

    public static ItemInformation searchBlocksHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = Statics.getPlayer().inventory.getStackInSlot(i);
            if (itemStack == null) continue;

            if (InventoryUtil.isBlock(itemStack)) {
                return new ItemInformation(itemStack, i);
            }
        }
        return null;
    }

    public static ItemInformation searchBlocks() {
        for (int i = 9; i < Statics.getPlayer().inventory.getSizeInventory(); i++) {
            ItemStack stack = Statics.getPlayer().inventory.getStackInSlot(i);
            if (stack == null) continue;

            if (InventoryUtil.isBlock(stack)) {
                return new ItemInformation(stack, i);
            }
        }
        return null;
    }

    public static final class ItemInformation {
        private ItemStack itemStack;
        private int id;

        public ItemInformation(ItemStack itemStack, int id) {
            this.itemStack = itemStack;
            this.id = id;
        }

        public ItemInformation() {
            this(null, -1);
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
