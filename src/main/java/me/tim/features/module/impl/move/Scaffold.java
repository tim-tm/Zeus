package me.tim.features.module.impl.move;

import me.tim.Statics;
import me.tim.features.event.*;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import me.tim.util.common.EnumUtil;
import me.tim.util.player.BlockUtil;
import me.tim.util.player.InventoryUtil;
import me.tim.util.player.rotation.Rotation;
import me.tim.util.render.RenderUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Objects;

public class Scaffold extends Module {
    private ModeSetting rotationModeSetting, towerModeSetting;
    private NumberSetting delaySetting;
    private BooleanSetting safeWalkSetting, silentSetting, sameYSetting, keepSprintSetting, swingSetting;

    private final BlockUtil blockUtil;
    private final Rotation rotation;
    private final Timer timer;

    private RotationMode rotationMode;
    private int silentSlot, lastSlotSent;
    private double startY;

    public Scaffold() {
        super("Scaffold", "Bridges for you!", Keyboard.KEY_G, Category.MOVEMENT);
        this.blockUtil = new BlockUtil();
        this.rotation = new Rotation();
        this.timer = new Timer();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.rotationModeSetting = new ModeSetting("Rotation Mode", "How should be towered?", RotationMode.values(), RotationMode.DEFAULT));
        this.settings.add(this.towerModeSetting = new ModeSetting("Tower Mode", "How should be towered?", TowerMode.values(), TowerMode.VANILLA));

        this.settings.add(this.delaySetting = new NumberSetting("Place-Delay", "Delay between block placements!", 0, 500, 125));

        this.settings.add(this.safeWalkSetting = new BooleanSetting("SafeWalk", "Stop on block edges!", true));
        this.settings.add(this.silentSetting = new BooleanSetting("Silent", "Place blocks while holding your sword!", false));
        this.settings.add(this.sameYSetting = new BooleanSetting("SameY", "Jump-Scaffold!", false));
        this.settings.add(this.keepSprintSetting = new BooleanSetting("KeepSprint", "Keep sprinting!", false));
        this.settings.add(this.swingSetting = new BooleanSetting("Swing", "Should swings be visual?", false));
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        TowerMode towerMode = (TowerMode) EnumUtil.fromName(this.towerModeSetting.getCurrentMode().getName(), TowerMode.values());
        this.rotationMode = (RotationMode) EnumUtil.fromName(this.rotationModeSetting.getCurrentMode().getName(), RotationMode.values());

        BlockPos pos = new BlockPos(Statics.getPlayer().getPositionVector().addVector(0, -1, 0));
        if (this.sameYSetting.getValue()) {
            pos = new BlockPos(Statics.getPlayer().posX, this.startY, Statics.getPlayer().posZ);
        }

        if (Statics.getWorld().getBlockState(pos).getBlock() instanceof BlockAir)
            this.blockUtil.find(pos);

        if (this.blockUtil.getEnumFacing() == null || this.blockUtil.getPos() == null) return;
        this.rotation.apply(this.blockUtil);

        if (this.silentSetting.getValue()) {
            InventoryUtil.ItemInformation itemInformation = InventoryUtil.searchBlocksHotbar();
            if (itemInformation != null) {
                this.silentSlot = itemInformation.getId();
            }
        }

        Statics.movementYaw = this.rotation.getYaw();

        if (towerMode == null || !Statics.getGameSettings().keyBindSneak.pressed) return;
        switch (towerMode) {
            case NCP:
                Statics.getPlayer().motionX = 0;
                Statics.getPlayer().motionZ = 0;
                if (Statics.getPlayer().motionY >= 0.1) {
                    Statics.getPlayer().setPosition(Statics.getPlayer().posX, MathHelper.ceiling_double_int(Statics.getPlayer().posY), Statics.getPlayer().posZ);
                }
                break;
            case FORWARD:
                Statics.getGameSettings().keyBindLeft.pressed = Statics.getPlayer().ticksExisted % 2 == 0;
                break;
        }
    }

    @EventTarget
    private void onRender3d(EventRender3D eventRender3D) {
        if (this.blockUtil == null || this.blockUtil.getPos() == null) return;

        RenderUtil.drawBlockOutline(this.blockUtil.getPos(), new Color(255, 35, 255));
    }

    @EventTarget
    private void onPre(EventPreMotion event) {
        if (this.rotation == null) return;
        if (rotationMode == null) return;

        float yaw = this.rotation.getYaw();
        float pitch = this.rotation.getPitch();
        if (Objects.requireNonNull(rotationMode) == RotationMode.OFF) {
            yaw = event.getYaw();
            pitch = event.getPitch();
        }

        event.setYaw(yaw);
        event.setPitch(pitch);
        Statics.getPlayer().renderYawOffset = yaw;
        Statics.getPlayer().rotationYawHead = yaw;
        Statics.getPlayer().rotationPitchHead = pitch;

        if (!(Statics.getWorld().getBlockState(new BlockPos(Statics.getPlayer().getPositionVector().addVector(0, -1, 0))).getBlock() instanceof BlockAir)) {
            switch (this.rotationMode) {
                case STATIC:
                    this.blockUtil.reset();
                    break;
                case UNLEGIT:
                    this.blockUtil.reset();
                    this.rotation.reset();
                    break;
            }
        }
    }

    @EventTarget
    private void onTick(EventTick event) {
        if (this.blockUtil == null || this.blockUtil.getPos() == null || this.rotation == null) return;

        int slot = this.silentSetting.getValue() ? this.silentSlot : Statics.getPlayer().inventory.currentItem;
        ItemStack item = Statics.getPlayer().inventory.getStackInSlot(slot);
        boolean check = item != null
                && item.getItem() instanceof ItemBlock
                && !(item.getItem() instanceof ItemSlab);
        if (!check) return;

        if (this.silentSetting.getValue() && ((this.lastSlotSent == Statics.getPlayer().inventory.currentItem && this.silentSlot != Statics.getPlayer().inventory.currentItem) || this.silentSlot != this.lastSlotSent)) {
            Statics.sendPacket(new C09PacketHeldItemChange(this.silentSlot));
        }

        if (this.timer.elapsed((long) this.delaySetting.getValue()) && Statics.getPlayerController().onPlayerRightClick(Statics.getPlayer(), Statics.getWorld(), slot, this.blockUtil.getPos(), this.blockUtil.getEnumFacing())) {
            if (this.swingSetting.getValue()) {
                Statics.getPlayer().swingItem();
            } else {
                Statics.sendPacket(new C0APacketAnimation());
            }
        }
    }

    @EventTarget
    private void onStrafe(EventStrafe eventStrafe) {
        this.rotation.strafe(eventStrafe, Rotation.StrafeMode.SILENT, this.keepSprintSetting.getValue());
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.getPacket() instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange heldItemChange = (C09PacketHeldItemChange) eventPacket.getPacket();
            this.lastSlotSent = heldItemChange.getSlotId();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (Statics.getPlayer() != null) {
            this.startY = Statics.getPlayer().posY - 1;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.blockUtil.reset();
        this.timer.reset();

        if (Statics.getPlayer() != null) {
            Statics.getTimer().timerSpeed = 1;
            this.syncCurrentPlayItem();
        }
        Statics.movementYaw = null;
    }

    private void syncCurrentPlayItem() {
        if (Statics.getPlayer().inventory.currentItem != this.silentSlot) {
            Statics.sendPacket(new C09PacketHeldItemChange(Statics.getPlayer().inventory.currentItem));
            this.silentSlot = Statics.getPlayer().inventory.currentItem;
        }
    }

    public boolean isSafeWalkEnabled() {
        return this.safeWalkSetting.getValue();
    }

    private enum TowerMode implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla"),
        NCP("NCP"),
        FORWARD("Forward");

        private final String name;

        TowerMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private enum RotationMode implements ModeSetting.ModeTemplate {
        OFF("Off"),
        DEFAULT("Default"),
        STATIC("Static"),
        UNLEGIT("Unlegit");

        private final String name;

        RotationMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
