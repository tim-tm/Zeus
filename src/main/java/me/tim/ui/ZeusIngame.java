package me.tim.ui;

import me.tim.Statics;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.EventResize;
import me.tim.features.event.EventShader;
import me.tim.features.event.api.EventManager;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Module;
import me.tim.util.common.MathUtil;
import me.tim.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;

public class ZeusIngame {
    public ZeusIngame() {
        EventManager.register(this);
    }

    @EventTarget
    private void onRender2d(EventRender2D event) {
        ArrayList<Module> mods = this.getSortedModules();
        this.draw(mods);

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        Statics.getFontRenderer().drawString("FPS: ", 10, event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT * 2 - 6, new Color(205, 75, 205).getRGB());
        Statics.getFontRenderer().drawString(String.valueOf(Minecraft.getDebugFPS()), 12 + Statics.getFontRenderer().getStringWidth("FPS: "), event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT * 2 - 6, -1);

        Statics.getFontRenderer().drawString("Ping: ", 10, event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT - 3, new Color(205, 75, 205).getRGB());
        Statics.getFontRenderer().drawString(String.valueOf(Statics.getPing()), 12 + Statics.getFontRenderer().getStringWidth("Ping: "), event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT - 3, -1);
        GlStateManager.popMatrix();

        int slot = Statics.getPlayer().inventory.currentItem * 20;
        int k = event.getWidth() / 2 - 90 + slot + 2;
        int l = event.getHeight() - 16 - 5;
        RenderUtil.drawRoundedRect(k, l, k + 17, l + 2, 1f, new Color(235, 35, 235, 130));
    }

    @EventTarget
    private void onShader(EventShader eventShader) {
        int i = eventShader.getWidth() / 2;
        RenderUtil.drawRoundedRect(i - 91, eventShader.getHeight() - 22, i + 91, eventShader.getHeight(), 4f, new Color(0, 0, 0));
    }

    /**
     * Optimizations
     *
     * @param eventResize
     */
    @EventTarget
    private void onResize(EventResize eventResize) {
        RenderUtil.BLUR_FRAMEBUFFER = RenderUtil.createFrameBuffer(RenderUtil.BLUR_FRAMEBUFFER);
        RenderUtil.BLOOM_FRAMEBUFFER = RenderUtil.createFrameBuffer(RenderUtil.BLOOM_FRAMEBUFFER);
        GuiIngame.BLOOM_BUFFER = RenderUtil.createFrameBuffer(GuiIngame.BLOOM_BUFFER);
    }

    private void draw(ArrayList<Module> mods) {
        int index = 0;
        for (Module module : mods) {
            if (!module.isEnabled()) continue;

            final float height = Statics.getFontRenderer().FONT_HEIGHT;
            float x = 5, y = 5 + (index * height), width = Statics.getFontRenderer().getStringWidth(module.getName());
            final Color c = this.calcColor(index, mods.size());
            if (!module.getSuffix().isEmpty()) {
                width += Statics.getFontRenderer().getStringWidth(" " + module.getSuffix());
            }

            GlStateManager.pushMatrix();
            RenderUtil.scale(x + width / 2f, y + height / 2f, module.getAnimation().animate());
            Statics.getFontRenderer().drawString(module.getName(), x, y, c.getRGB());
            if (!module.getSuffix().isEmpty()) {
                Statics.getFontRenderer().drawString(" " + module.getSuffix(), x + Statics.getFontRenderer().getStringWidth(module.getName()), y, new Color(170, 170, 170).getRGB());
            }
            GlStateManager.popMatrix();
            index++;
        }
    }

    private Color calcColor(int index, int max) {
        Color to = new Color(245, 35, 245);
        Color base = new Color(100, 14, 100);
        long min = (System.currentTimeMillis() / 45 + index) % max;
        float val = MathUtil.percentage(min, max);

        return new Color(
                MathUtil.interpolate(to.getRed(), base.getRed(), val).intValue(),
                MathUtil.interpolate(to.getGreen(), base.getGreen(), val).intValue(),
                MathUtil.interpolate(to.getBlue(), base.getBlue(), val).intValue());
    }

    private ArrayList<Module> getSortedModules() {
        ArrayList<Module> sorted = Statics.getZeus().moduleManager.getVisibleModules();
        sorted.sort((mod1, mod2) -> Float.compare(Statics.getFontRenderer().getStringWidth(mod2.getName() + (mod2.getSuffix().isEmpty() ? "" : " " + mod2.getSuffix())), Statics.getFontRenderer().getStringWidth(mod1.getName() + (mod1.getSuffix().isEmpty() ? "" : " " + mod1.getSuffix()))));
        return sorted;
    }
}
