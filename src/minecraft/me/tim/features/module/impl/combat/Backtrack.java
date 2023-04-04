package me.tim.features.module.impl.combat;

import me.tim.Statics;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventRender3D;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.Timer;
import me.tim.util.common.MathUtil;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.src.Config;
import net.minecraft.util.Vec3;
import net.optifine.shaders.Shaders;
import org.lwjgl.input.Keyboard;

import javax.vecmath.Vector2f;

public class Backtrack extends Module {
    private NumberSetting delaySetting;

    private final Timer timer;
    private final TrackingInformation entityInformation;

    public Backtrack() {
        super("Backtrack", "Hit last entity positions!", Keyboard.KEY_NONE, Category.COMBAT);
        this.timer = new Timer();
        this.entityInformation = new TrackingInformation();
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.delaySetting = new NumberSetting("Delay", "Backtrack-Delay", 50, 800, 250));
    }

    @EventTarget
    private void onRender3d(EventRender3D eventRender3D) {
        if (this.entityInformation == null || this.entityInformation.entity == null || this.entityInformation.realPosition == null || !this.entityInformation.hit) return;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        RenderUtil.drawEntityStatic(this.entityInformation.entity, this.entityInformation.realPosition);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        if (Config.isShaders())
        {
            Shaders.disableLightmap();
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        switch (eventPacket.getState()) {
            case SEND:
                if (eventPacket.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) eventPacket.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                    this.entityInformation.hit = true;
                    this.entityInformation.entity = ((C02PacketUseEntity) eventPacket.getPacket()).getEntityFromWorld(Statics.getWorld());
                }
                break;
            case RECEIVE:
                if (this.entityInformation.hit && (eventPacket.getPacket() instanceof S12PacketEntityVelocity || eventPacket.getPacket() instanceof S14PacketEntity)) {
                    boolean passed = false;
                    if (eventPacket.getPacket() instanceof S14PacketEntity) {
                        S14PacketEntity packetEntity = (S14PacketEntity) eventPacket.getPacket();
                        passed = packetEntity.getEntity(Statics.getWorld()).equals(this.entityInformation.entity);

                        if (passed) {
                            double decodedX = packetEntity.getX() / 32.0D;
                            double decodedY = packetEntity.getY() / 32.0D;
                            double decodedZ = packetEntity.getZ() / 32.0D;
                            this.entityInformation.realPosition = new Vec3(decodedX, decodedY, decodedZ);
                        }
                    }

                    if (eventPacket.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity packetVelocity = (S12PacketEntityVelocity) eventPacket.getPacket();
                        passed = passed || (this.entityInformation.entity != null && packetVelocity.getEntityID() == this.entityInformation.entity.getEntityId());

                        if (passed) {
                            double newX = MathUtil.interpolate(this.entityInformation.entity.lastTickPosX, this.entityInformation.entity.posX, packetVelocity.getMotionX() / 8000.0D);
                            double newY = MathUtil.interpolate(this.entityInformation.entity.lastTickPosY, this.entityInformation.entity.posY, packetVelocity.getMotionY() / 8000.0D);
                            double newZ = MathUtil.interpolate(this.entityInformation.entity.lastTickPosZ, this.entityInformation.entity.posZ, packetVelocity.getMotionZ() / 8000.0D);
                            this.entityInformation.realPosition = new Vec3(newX, newY, newZ);
                        }
                    }

                    if (passed) {
                        if (!this.timer.elapsed((long) this.delaySetting.getValue())) {
                            eventPacket.setCancelled(true);
                        } else {
                            this.timer.reset();
                            this.entityInformation.hit = false;
                        }
                    }
                }
                break;
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
            this(null, null, false);
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
