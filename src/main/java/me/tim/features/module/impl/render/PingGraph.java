package me.tim.features.module.impl.render;

import me.tim.Statics;
import me.tim.features.event.EventBloom;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.EventTick;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class PingGraph extends Module {
    private final int width = 100, height = 40;
    private final ArrayList<Integer> pings;
    
    public PingGraph() {
        super("PingGraph", "Show recent pings!", Keyboard.KEY_NONE, Category.RENDER);
        this.pings = new ArrayList<>();
    }

    @Override
    protected void setupSettings() { }
    
    @EventTarget
    private void onTick(EventTick eventTick) {
        if (Statics.getWorld() == null) return;
        if (this.pings.size() > this.width) {
            this.pings.remove(0);
        }
        this.pings.add(Statics.getPing());
    }

    @EventTarget
    private void onRender2d(EventRender2D eventRender2D) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.glBegin(GL11.GL_LINES);
        double yPos = (eventRender2D.getHeight() / 2f) + this.height;
        double len = this.width / (this.pings.size() - 1D);
        for (int i = 0; i < this.pings.size() - 1; i++) {
            GlStateManager.color(1.0f, 0.3f, 1.0f, 1.0f);
            double y = this.pings.get(i) * 0.5d;
            double y2 = this.pings.get(i + 1) * 0.5d;

            GL11.glVertex2d(5 + (i * len), yPos + height - Math.min(y, height));
            GL11.glVertex2d(5 + ((i + 1) * len), yPos + height - Math.min(y2, height));
        }
        GlStateManager.glEnd();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if (this.pings.size() > 0) {
            double recentY = this.pings.get(this.pings.size() - 1) * 0.5d;
            Statics.getFontRenderer().drawString(Statics.getPing() + " ms", 10 + width, (int) (yPos + height - Math.min(recentY, height) - Statics.getFontRenderer().FONT_HEIGHT / 2), -1);
        }
    }

    @EventTarget
    private void onBloom(EventBloom eventBloom) {
        Gui.drawRect(5, (eventBloom.getHeight() / 2) + height, 5 + width, (eventBloom.getHeight() / 2) + (height * 2), -1);
    }
}
