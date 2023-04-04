package me.tim.util.player;

import me.tim.Statics;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityChest;

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
}
