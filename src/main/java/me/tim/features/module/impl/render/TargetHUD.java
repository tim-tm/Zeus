package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.event.EventBloom;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.EventRender3D;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.features.module.impl.combat.KillAura;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.util.common.MathUtil;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TargetHUD extends Module {
    private BooleanSetting projectedSetting;

    private KillAura killAuraModule;
    private float x, y, width, height;

    public TargetHUD() {
        super("TargetHUD", "See target information!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.projectedSetting = new BooleanSetting("Projected", "TargetHUD appears Projected!", false));
    }

    @EventTarget
    private void onRender2d(EventRender2D eventRender2D) {
        if (!this.killAuraModule.isEnabled() || this.killAuraModule.getCurrTarget() == null || this.projectedSetting.getValue()) return;
        this.x = eventRender2D.getWidth() / 2f + 20;
        this.y = eventRender2D.getHeight() / 2f + 20;
        this.width = 125;
        this.height = 60;
        this.drawTargetHUD();
    }

    @EventTarget
    private void onBloom(EventBloom eventBloom) {
        if (!this.killAuraModule.isEnabled() || this.killAuraModule.getCurrTarget() == null || this.projectedSetting.getValue()) return;

        RenderUtil.drawRoundedRect(this.x, this.y, this.x + this.width, this.y + this.height, 3f, new Color(255, 255, 255));
    }

    @EventTarget
    private void onRender3d(EventRender3D eventRender3D) {
        if (!this.killAuraModule.isEnabled() || this.killAuraModule.getCurrTarget() == null || !this.projectedSetting.getValue()) return;
        double rawX = this.killAuraModule.getCurrTarget().posX, rawY = this.killAuraModule.getCurrTarget().posY + 1.5, rawZ = this.killAuraModule.getCurrTarget().posZ;
        double lastRawX = this.killAuraModule.getCurrTarget().lastTickPosX, lastRawY = this.killAuraModule.getCurrTarget().lastTickPosY + 1.5, lastRawZ = this.killAuraModule.getCurrTarget().lastTickPosZ;
        double x = MathUtil.interpolate(lastRawX, rawX, eventRender3D.getPartialTicks()) - Statics.getMinecraft().getRenderManager().renderPosX;
        double y = MathUtil.interpolate(lastRawY, rawY, eventRender3D.getPartialTicks()) - Statics.getMinecraft().getRenderManager().renderPosY;
        double z = MathUtil.interpolate(lastRawZ, rawZ, eventRender3D.getPartialTicks()) - Statics.getMinecraft().getRenderManager().renderPosZ;
        this.x = 21;
        this.y = 21;
        this.width = 60;
        this.height = 20;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(0.03f, 0.03f, 0.03f);
        GlStateManager.rotate(-Statics.getMinecraft().getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();
        this.drawTargetHUD();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();

    }

    private void drawTargetHUD() {
        float healthPerc = MathUtil.percentage(this.killAuraModule.getCurrTarget().getHealth(), this.killAuraModule.getCurrTarget().getMaxHealth());
        healthPerc = MathHelper.clamp_float(healthPerc, 0, 1);
        int col = (int) (255 * healthPerc);

        RenderUtil.drawRoundedRect(this.x, this.y, this.x + this.width * healthPerc, this.y + this.height, 3f, new Color(255 - col, col, 0, 100));
        Statics.getFontRenderer().drawString(this.killAuraModule.getCurrTarget().getName(), (int) (this.x + 10), (int) (this.y + 10), -1);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.killAuraModule = (KillAura) Statics.getZeus().moduleManager.getModuleByClass(KillAura.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
