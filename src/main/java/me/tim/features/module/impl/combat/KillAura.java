package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.EventStrafe;
import me.tim.features.event.EventTick;
import me.tim.features.event.EventUpdate;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.features.module.impl.player.Teams;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import me.tim.util.common.EnumUtil;
import me.tim.util.common.MathUtil;
import me.tim.util.player.InventoryUtil;
import me.tim.util.player.rotation.Rotation;
import me.tim.util.player.rotation.RotationUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import viamcp.ViaMCP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class KillAura extends Module {
    private ModeSetting switchModeSetting, autoBlockModeSetting, strafeModeSetting;
    private NumberSetting aRangeSetting, apsSetting, dRangeSetting, twRangeSetting, switchDelaySetting, maxHurtTimeSetting, particleMultiplierSetting, reduceSetting;
    private BooleanSetting keepSprintSetting, newHitDelaySetting, autoAnnoySetting, searchSetting;

    private final Rotation rotation;
    private final Timer attackTimer, switchTimer, annoyTimer;
    private EntityLivingBase currTarget;

    private BlockMode blockMode;
    private final ArrayList<EntityLivingBase> switchEntities;
    private Teams teamsModule;

    public KillAura() {
        super("KillAura", "Auto-kill everybody!", Keyboard.KEY_R, Category.COMBAT);
        this.attackTimer = new Timer();
        this.switchTimer = new Timer();
        this.annoyTimer = new Timer();
        this.rotation = new Rotation();
        this.switchEntities = new ArrayList<>();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.switchModeSetting = new ModeSetting("Switch Mode", "How should the target be detected?", SwitchMode.values(), SwitchMode.RANGE));
        this.settings.add(this.autoBlockModeSetting = new ModeSetting("AutoBlock Mode", "The way, the KillAura is going to block for you!", BlockMode.values(), BlockMode.OFF));
        this.settings.add(this.strafeModeSetting = new ModeSetting("Strafe Mode", "Move like you're legit!", Rotation.StrafeMode.values(), Rotation.StrafeMode.OFF));

        this.settings.add(this.apsSetting = new NumberSetting("APS", "How often do you want to attack in a second?", 1f, 40f, 10f));
        this.settings.add(this.aRangeSetting = new NumberSetting("Attack-Range", "The Range in which you are going to attack!", 3f, 8f, 4f));
        this.settings.add(this.twRangeSetting = new NumberSetting("Walls-Range", "The Range in which you are going to attack targets through walls!", 0, 8f, 4f));
        this.settings.add(this.dRangeSetting = new NumberSetting("Detection-Range", "The Range in which you are going to detect targets!", 3f, 15f, 5f));
        this.settings.add(this.switchDelaySetting = new NumberSetting("Switch Delay", "Delay between switching targets!", 0, 500, 125));
        this.settings.add(this.maxHurtTimeSetting = new NumberSetting("Max-HurtTime", "Maximum HurtTime of Target!", 1, 10, 10));
        this.settings.add(this.particleMultiplierSetting = new NumberSetting("ParticleMultiplier", "See more particles!", 1, 10, 2));
        this.settings.add(this.reduceSetting = new NumberSetting("Reduce-Amount", "Amount of reduction!", 1, 10, 2));

        this.settings.add(this.autoAnnoySetting = new BooleanSetting("Auto-Annoy", "Auto-Annoy by using Rods, Eggs or Snowballs!", false));
        this.settings.add(this.keepSprintSetting = new BooleanSetting("KeepSprint", "Prevent sprinting while attacking!", false));
        this.settings.add(this.searchSetting = new BooleanSetting("Search", "Search for the best rotation!", false));
        this.settings.add(this.newHitDelaySetting = new BooleanSetting("1.9 Hit Delay", "Hit Delayed due to how newer MC Versions work!", false));
    }

    private EntityLivingBase getTarget() {
        SwitchMode switchMode = (SwitchMode) EnumUtil.fromName(this.switchModeSetting.getCurrentMode().getName(), SwitchMode.values());
        if (switchMode == null) return null;

        for (Entity entity : Statics.getWorld().loadedEntityList) {
            if (entity instanceof EntityLivingBase) this.switchEntities.add((EntityLivingBase) entity);
        }

        this.switchEntities.removeIf(target ->
                this.switchEntities.size() > 0 && (
                        target == Statics.getPlayer()
                                || target.getHealth() <= 0
                                || target.isDead
                                || target.getDistanceToEntity(Statics.getPlayer()) > this.dRangeSetting.getValue()
                                || target instanceof EntityArmorStand
                                || (!Statics.getPlayer().canEntityBeSeen(target) && !this.searchSetting.getValue() && target.getDistanceToEntity(Statics.getPlayer()) > this.twRangeSetting.getValue()))
                        || (this.teamsModule.isEnabled() && this.teamsModule.getTeammates().contains(target.getName()))
                        || target.isInvisible()
                        || target.getUniqueID().equals(Statics.getPlayer().getUniqueID())
                        || Statics.getZeus().friendManager.contains(target.getName()));

        switch (switchMode) {
            case RANGE:
                this.switchEntities.sort((target1, target2) -> Float.compare(target1.getDistanceToEntity(Statics.getPlayer()), target2.getDistanceToEntity(Statics.getPlayer())));
                break;
            case HEALTH:
                this.switchEntities.sort((target1, target2) -> Float.compare(target1.getHealth(), target2.getHealth()));
                break;
            case ARMOR:
                this.switchEntities.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
                break;
        }
        return this.switchEntities.size() > 0 ? this.switchEntities.get(0) : null;
    }

    @EventTarget
    private void onPre(EventPreMotion event) {
        event.setYaw(rotation.getYaw());
        event.setPitch(rotation.getPitch());

        float renderYaw = MathUtil.interpolate(rotation.getLastYaw(), rotation.getYaw(), Statics.getTimer().renderPartialTicks).floatValue();
        float renderPitch = MathUtil.interpolate(rotation.getLastPitch(), rotation.getPitch(), Statics.getTimer().renderPartialTicks).floatValue();
        Statics.getPlayer().rotationYawHead = renderYaw;
        Statics.getPlayer().renderYawOffset = renderYaw;
        Statics.getPlayer().rotationPitchHead = renderPitch;

        this.handleAutoAnnoy();
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        this.blockMode = (BlockMode) EnumUtil.fromName(this.autoBlockModeSetting.getCurrentMode().getName(), BlockMode.values());

        if (this.switchTimer.elapsed((long) this.switchDelaySetting.getValue())) {
            this.currTarget = this.getTarget();
            this.switchTimer.reset();
        } else {
            this.currTarget = null;
        }

        if (this.currTarget == null || this.currTarget.getHealth() <= 0 || Statics.getPlayer().getDistanceToEntity(this.currTarget) > this.dRangeSetting.getValue()) {
            this.rotation.reset();
            this.setSuffix("");
            return;
        }

        this.setSuffix(this.currTarget.getName());

        if (this.searchSetting.getValue()) {
            AxisAlignedBB bb = this.currTarget.getEntityBoundingBox();
            double bestDistance = Double.MAX_VALUE;
            Vec3 bestVec = new Vec3(0, 0, 0);
            for (double x = bb.minX; x <= bb.maxX; x++) {
                for (double y = bb.minY; y <= bb.maxY; y++) {
                    for (double z = bb.minZ; z <= bb.maxZ; z++) {
                        double dist = Statics.getPlayer().getDistance(x, y, z);
                        if (dist < bestDistance) {
                            bestDistance = dist;
                            bestVec = new Vec3(x, y, z);
                        }
                    }
                }
            }
            this.rotation.apply(bestVec);
        } else {
            this.rotation.apply(this.currTarget);
        }
        Statics.movementYaw = this.rotation.getYaw();
    }

    @EventTarget
    private void onStrafe(EventStrafe eventStrafe) {
        Rotation.StrafeMode mode = (Rotation.StrafeMode) EnumUtil.fromName(this.strafeModeSetting.getCurrentMode().getName(), Rotation.StrafeMode.values());
        if (mode == null) return;

        this.rotation.strafe(eventStrafe, mode, this.keepSprintSetting.getValue());
    }

    @EventTarget
    private void onTick(EventTick event) {
        if (Statics.getPlayer() == null || this.currTarget == null || Statics.getMinecraft().currentScreen instanceof GuiInventory) return;

        if (this.currTarget.hurtTime > this.maxHurtTimeSetting.getValue()) return;

        this.handleAutoBlock(BlockState.PRE);
        if (this.currTarget.getDistanceToEntity(Statics.getPlayer()) <= this.aRangeSetting.getValue()) {
            long aps = MathUtil.random(this.apsSetting.getValue() - this.apsSetting.getValue() / 4f, this.apsSetting.getValue()).longValue();
            if (this.newHitDelaySetting.getValue()) {
                aps = 1;
            }

            if (RotationUtil.rayCast(this.aRangeSetting.getValue(), this.rotation) == currTarget && this.attackTimer.elapsed(1000 / aps)) {
                for (int i = 0; i < this.particleMultiplierSetting.getValue(); i++) {
                    Statics.getPlayer().onEnchantmentCritical(this.currTarget);
                }

                if (ViaMCP.getInstance().getVersion() <= 47) {
                    Statics.getPlayer().swingItem();
                    Statics.sendPacket(new C02PacketUseEntity(this.currTarget, C02PacketUseEntity.Action.ATTACK));
                } else {
                    Statics.sendPacket(new C02PacketUseEntity(this.currTarget, C02PacketUseEntity.Action.ATTACK));
                    Statics.getPlayer().swingItem();
                }

                if (!this.keepSprintSetting.getValue() && Statics.getPlayer().isSprinting()) {
                    this.currTarget.addVelocity(-MathHelper.sin((float) Math.toRadians(this.rotation.getYaw())) * this.reduceSetting.getValue() * 0.5F, 0.1D, MathHelper.cos((float) Math.toRadians(this.rotation.getYaw())) * this.reduceSetting.getValue() * 0.5F);
                    Statics.getPlayer().motionZ *= 0.6f;
                    Statics.getPlayer().motionX *= 0.6f;
                    Statics.getPlayer().setSprinting(false);
                }

                this.attackTimer.reset();
            }
        }
        this.handleAutoBlock(BlockState.POST);
    }

    private void handleAutoBlock(BlockState state) {
        if (this.blockMode == null || Statics.getPlayer().getCurrentEquippedItem() == null || !(Statics.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemSword)) return;
        if (Statics.getPlayer().getDistanceToEntity(this.currTarget) > this.dRangeSetting.getValue()) return;

        switch (state) {
            case PRE:
                switch (this.blockMode) {
                    case DEFAULT:
                        Statics.getGameSettings().keyBindDrop.pressed = false;
                        break;
                    case VANILLA:
                        Statics.getPlayerController().sendUseItem(Statics.getPlayer(), Statics.getWorld(), Statics.getPlayer().getCurrentEquippedItem());
                        break;
                }
                break;
            case POST:
                if (Objects.requireNonNull(this.blockMode) == BlockMode.DEFAULT) {
                    Statics.getGameSettings().keyBindDrop.pressed = true;
                }
                break;
        }
    }
    
    private void handleAutoAnnoy() {
        if (!this.autoAnnoySetting.getValue() || this.currTarget == null || !Statics.getPlayer().canEntityBeSeen(this.currTarget)) return;

        InventoryUtil.ItemInformation itemStack = InventoryUtil.searchAnnoyable();
        if (itemStack == null || itemStack.getId() == -1) return;

        long delay = 150;
        if (itemStack.getItemStack().getItem() instanceof ItemFishingRod) {
            delay += 1000;
        }

        int currentPlayerItem = Statics.getPlayer().inventory.currentItem;
        double dist = Statics.getPlayer().getDistanceToEntity(this.currTarget);
        if (dist <= this.dRangeSetting.getValue() && dist > this.aRangeSetting.getValue()) {
            if (this.annoyTimer.elapsed(delay)) {
                Statics.sendPacket(new C09PacketHeldItemChange(itemStack.getId()));
                Statics.sendPacket(new C08PacketPlayerBlockPlacement(itemStack.getItemStack()));
                itemStack.getItemStack().useItemRightClick(Statics.getWorld(), Statics.getPlayer());
                Statics.sendPacket(new C09PacketHeldItemChange(currentPlayerItem));
                this.annoyTimer.reset();
            }
        }
    }

    public BlockMode getBlockMode() {
        return blockMode;
    }

    public EntityLivingBase getCurrTarget() {
        return currTarget;
    }

    public boolean keepSprintEnabled() {
        return this.keepSprintSetting != null && this.keepSprintSetting.getValue();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.switchTimer.reset();
        this.teamsModule = (Teams) Statics.getZeus().moduleManager.getModuleByClass(Teams.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.currTarget = null;
        Statics.getGameSettings().keyBindDrop.pressed = false;
        Statics.movementYaw = null;

        if (Statics.getPlayer() != null)
            this.rotation.reset();
    }

    private enum SwitchMode implements ModeSetting.ModeTemplate {
        RANGE("Range"),
        HEALTH("Health"),
        ARMOR("Armor");

        private final String name;

        SwitchMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum BlockMode implements ModeSetting.ModeTemplate {
        OFF("Off"),
        DEFAULT("Default"),
        VANILLA("Vanilla"),
        FAKE("Fake");

        private final String name;

        BlockMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private enum BlockState { PRE, POST }
}
