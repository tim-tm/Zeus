package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.event.EventBloom;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.features.module.impl.combat.KillAura;
import me.tim.util.common.MathUtil;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TargetHUD extends Module {
    private KillAura killAuraModule;
    private float x, y, width, height;

    public TargetHUD() {
        super("TargetHUD", "See target information!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() { }

    @EventTarget
    private void onRender2d(EventRender2D eventRender2D) {
        if (!this.killAuraModule.isEnabled() || this.killAuraModule.getCurrTarget() == null) return;
        this.x = eventRender2D.getWidth() / 2f + 20;
        this.y = eventRender2D.getHeight() / 2f + 20;
        this.width = 125;
        this.height = 60;

        float healthPerc = MathUtil.percentage(this.killAuraModule.getCurrTarget().getHealth(), this.killAuraModule.getCurrTarget().getMaxHealth());
        healthPerc = MathHelper.clamp_float(healthPerc, 0, 1);
        int col = (int) (255 * healthPerc);
        RenderUtil.drawRoundedRect(this.x, this.y, this.x + this.width * healthPerc, this.y + this.height, 10, new Color(255 - col, col, 0, 100));

        Statics.getFontRenderer().drawString(this.killAuraModule.getCurrTarget().getName(), (int) (this.x + 10), (int) (this.y + 10), -1);
    }

    @EventTarget
    private void onBloom(EventBloom eventBloom) {
        if (!this.killAuraModule.isEnabled() || this.killAuraModule.getCurrTarget() == null) return;

        RenderUtil.drawRoundedRect(this.x, this.y, this.x + this.width, this.y + this.height, 10, new Color(255, 255, 255));
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
