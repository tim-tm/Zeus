package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.event.EventShader;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.features.module.impl.player.Teams;
import me.tim.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Radar extends Module {
    private final int size = 80;
    private Teams teamsModule;

    public Radar() {
        super("Radar", "See nearby Players!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() { }

    @EventTarget
    private void onRender2d(EventRender2D eventRender2D) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 0.3f, 1.0f, 1.0f);
        RenderUtil.drawCircle(eventRender2D.getWidth() - 16 - size / 2f, eventRender2D.getHeight() - 16 - size / 2f, eventRender2D.getWidth() - 14 - size / 2f, eventRender2D.getHeight() - 14 - size / 2f, new Color(235, 45, 235));
        for (EntityPlayer playerEntity : Statics.getWorld().playerEntities) {
            if (playerEntity == null || playerEntity.getHealth() <= 0 || playerEntity == Statics.getPlayer()) continue;
            float posX = (float) (playerEntity.posX + (playerEntity.posX - playerEntity.lastTickPosX) * eventRender2D.getPartialTicks() - Statics.getPlayer().posX);
            float posZ = (float) (playerEntity.posZ + (playerEntity.posZ - playerEntity.lastTickPosZ) * eventRender2D.getPartialTicks() - Statics.getPlayer().posZ);

            float f = MathHelper.cos((float) Math.toRadians(Statics.getPlayer().rotationYaw));
            float f1 = MathHelper.sin((float) Math.toRadians(Statics.getPlayer().rotationYaw));
            float rotY = -posZ * f - posX * f1;
            float rotX = -posX * f + posZ * f1;
            rotY = MathHelper.clamp_float(rotY, -size / 2f, size / 2f);
            rotX = MathHelper.clamp_float(rotX, -size / 2f, size / 2f);

            Color color = new Color(215, 35, 45);
            if ((this.teamsModule.isEnabled() && this.teamsModule.getTeammates().contains(playerEntity.getName())) || Statics.getZeus().friendManager.contains(playerEntity.getName())) {
                color = new Color(45, 35, 215);
            }
            RenderUtil.drawCircle(eventRender2D.getWidth() - 16 - (size / 2f + rotX), eventRender2D.getHeight() - 16 - (size / 2f + rotY), eventRender2D.getWidth() - 14 - (size / 2f + rotX), eventRender2D.getHeight() - 14 - (size / 2f + rotY), color);
        }
        GlStateManager.popMatrix();
    }

    @EventTarget
    private void onShader(EventShader eventShader) {
        Gui.drawRect(eventShader.getWidth() - size - 15, eventShader.getHeight() - size - 15, eventShader.getWidth() - 15, eventShader.getHeight() - 15, -1);
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
}
