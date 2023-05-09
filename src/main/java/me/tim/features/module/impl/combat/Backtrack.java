package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventJoin;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.EventRender3D;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import me.tim.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.src.Config;
import net.minecraft.util.Vec3;
import net.optifine.shaders.Shaders;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;

public class Backtrack extends Module {
    private NumberSetting delaySetting;

    private final Timer timer;
    private final TrackingInformation entityInformation;
    private final LinkedList<Packet<?>> packets;

    public Backtrack() {
        super("Backtrack", "Hit last entity positions!", Keyboard.KEY_NONE, Category.COMBAT);
        this.timer = new Timer();
        this.entityInformation = new TrackingInformation();
        this.packets = new LinkedList<>();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.delaySetting = new NumberSetting("Delay", "Backtrack-Delay", 50, 800, 250));
    }

    @EventTarget
    private void onJoin(EventJoin eventJoin) {
        this.entityInformation.reset();
        this.packets.clear();
    }

    @EventTarget
    private void onPre(EventPreMotion eventPreMotion) {
        this.setSuffix(String.valueOf(this.delaySetting.getValue()));
        if (this.entityInformation == null) return;

        if ((this.entityInformation.entity == null || this.entityInformation.entity.isDead) && (this.entityInformation.realPosition == null || !this.entityInformation.realPosition.equals(new Vec3(0, 0, 0)))) {
            this.entityInformation.realPosition = new Vec3(0, 0, 0);
        }
    }

    @EventTarget
    private void onRender3d(EventRender3D eventRender3D) {
        if (this.entityInformation == null || this.entityInformation.entity == null || this.entityInformation.realPosition == null)
            return;

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();

        RenderUtil.drawEntityStatic(this.entityInformation.entity, this.entityInformation.realPosition, 0.35f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        if (Config.isShaders()) {
            Shaders.disableLightmap();
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (Statics.getWorld() == null || this.entityInformation == null) return;

        switch (eventPacket.getState()) {
            case SEND:
                if (eventPacket.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) eventPacket.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                    this.entityInformation.hit = true;
                    this.entityInformation.entity = ((C02PacketUseEntity) eventPacket.getPacket()).getEntityFromWorld(Statics.getWorld());
                    this.entityInformation.realPosition = this.entityInformation.entity.getPositionVector();
                }

                if (this.entityInformation.hit && this.timer.getElapsedTime() > 0 && this.timer.getElapsedTime() <= this.delaySetting.getValue()) {
                    if (eventPacket.getPacket() instanceof C00PacketKeepAlive) {
                        this.packets.add(eventPacket.getPacket());
                        eventPacket.setCancelled(true);
                    }
                } else {
                    if (this.packets.size() > 0) {
                        Statics.sendPacketNoEvent(this.packets.poll());
                    }
                }
                break;
            case RECEIVE:
                if (eventPacket.getPacket() instanceof S14PacketEntity) {
                    S14PacketEntity packetEntity = (S14PacketEntity) eventPacket.getPacket();
                    if (packetEntity == null) return;

                    Entity entity = packetEntity.getEntity(Statics.getWorld());
                    if (this.entityInformation.hit && entity != null && entity.equals(this.entityInformation.entity)) {
                        if (!this.timer.elapsed((long) this.delaySetting.getValue())) {
                            eventPacket.setCancelled(true);

                            double posX = packetEntity.getX() / 32.0D;
                            double posY = packetEntity.getX() / 32.0D;
                            double posZ = packetEntity.getX() / 32.0D;
                            this.entityInformation.realPosition.addVector(posX, posY, posZ);
                        } else {
                            this.timer.reset();
                            this.entityInformation.hit = false;
                            this.entityInformation.realPosition = new Vec3(0, 0, 0);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.entityInformation.reset();
        if (Statics.getPlayer() != null) {
            while (this.packets.size() > 0) {
                Statics.sendPacketNoEvent(this.packets.poll());
            }
        }
    }

    private static final class TrackingInformation {
        private Entity entity;
        private Vec3 realPosition;
        private boolean hit;

        public TrackingInformation(Entity entity, Vec3 realPosition, boolean hit) {
            this.entity = entity;
            this.realPosition = realPosition;
            this.hit = hit;
        }

        public TrackingInformation() {
            this(null, new Vec3(0, 0, 0), false);
        }

        public void reset() {
            this.entity = null;
            this.realPosition = new Vec3(0, 0, 0);
            this.hit = false;
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public Vec3 getRealPosition() {
            return realPosition;
        }

        public void setRealPosition(Vec3 realPosition) {
            this.realPosition = realPosition;
        }

        public boolean isHit() {
            return hit;
        }

        public void setHit(boolean hit) {
            this.hit = hit;
        }
    }
}
