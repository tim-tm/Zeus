package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.event.EventRender3D;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.features.module.impl.player.Teams;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.common.EnumUtil;
import me.tim.util.common.MathUtil;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ESP extends Module {
    private ModeSetting modeSetting;
    
    private Teams teamsModule;
    private Framebuffer entityBuffer = new Framebuffer(1, 1, false);

    public ESP() {
        super("ESP", "See people through walls!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.modeSetting = new ModeSetting("ESP Mode", "Render mode!", ESPMode.values(), ESPMode.TWOD));
    }

    @EventTarget
    private void onRender3D(EventRender3D eventRender3D) {
        ESPMode mode = (ESPMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), ESPMode.values());
        if (mode == null) return;
        
        for (Entity entity : Statics.getWorld().loadedEntityList) {
            if (!(entity instanceof EntityPlayer) || entity.isInvisible()) continue;
            if (entity == Statics.getPlayer() && Statics.getGameSettings().showDebugInfo == 0) continue;
            if (this.teamsModule.isEnabled() && this.teamsModule.getTeammates().contains(entity.getName())) continue;
            if (Statics.getZeus().friendManager.contains(entity.getName())) continue;

            EntityPlayer player = (EntityPlayer) entity;
            double x = MathUtil.interpolate(entity.lastTickPosX, entity.posX, eventRender3D.getPartialTicks()) - Statics.getMinecraft().getRenderManager().renderPosX;
            double y = MathUtil.interpolate(entity.lastTickPosY, entity.posY, eventRender3D.getPartialTicks()) - Statics.getMinecraft().getRenderManager().renderPosY;
            double z = MathUtil.interpolate(entity.lastTickPosZ, entity.posZ, eventRender3D.getPartialTicks()) - Statics.getMinecraft().getRenderManager().renderPosZ;

            switch (mode) {
                case TWOD:
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y - 0.2D, z);
                    GL11.glScalef(0.03f, 0.03f, 0.03f);
                    GL11.glRotated(-Statics.getMinecraft().getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    int lineW = 2;
                    float healthFactor = MathUtil.percentage(player.getHealth(), player.getMaxHealth());
                    healthFactor = MathHelper.clamp_float(healthFactor, 0, 1);

                    int colorHealth = (int) (255 * healthFactor);
                    int color = new Color(255 - colorHealth, colorHealth, 0).getRGB();
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.disableDepth();

                    // Health-Bar
                    Gui.drawRect(21, 0, 21 + lineW, (int) (70 * healthFactor), color);

                    // ESP
                    RenderUtil.drawEntityBox2d(player, lineW, new Color(205, 45, 205));

                    GlStateManager.enableDepth();
                    GL11.glPopMatrix();
                    break;
                case SHADER:
                    GlStateManager.pushMatrix();
                    entityBuffer = RenderUtil.createFrameBuffer(entityBuffer);
                    entityBuffer.framebufferClear();
                    entityBuffer.bindFramebuffer(true);
                    Statics.getMinecraft().getRenderManager().renderEntitySimple(player, eventRender3D.getPartialTicks());
                    entityBuffer.unbindFramebuffer();
                    RenderUtil.drawBloom(entityBuffer.framebufferTexture, 15, 2, new Color(205, 45, 205), true);
                    GlStateManager.popMatrix();
                    break;
            }
        }

        for (TileEntity tileEntity : Statics.getWorld().loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
                RenderUtil.drawBlockOutline(tileEntity.getPos(), new Color(205, 45, 205));
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.teamsModule = (Teams) Statics.getZeus().moduleManager.getModuleByClass(Teams.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private enum ESPMode implements ModeSetting.ModeTemplate {
        TWOD("TWOD"),
        SHADER("Shader");

        private final String name;

        ESPMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
