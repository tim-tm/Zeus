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

public class MotionGraph extends Module {
    private final int width = 100, height = 40;
    private final ArrayList<Double> speeds;

    public MotionGraph() {
        super("MotionGraph", "See your recent motion.", Keyboard.KEY_NONE, Category.RENDER);
        this.speeds = new ArrayList<>();
    }

    @Override
    protected void setupSettings() { }

    @EventTarget
    private void onTick(EventTick eventTick) {
        if (Statics.getWorld() == null) return;
        if (this.speeds.size() > this.width) {
            this.speeds.remove(0);
        }
        this.speeds.add(Statics.getSpeed() * Statics.getTimer().timerSpeed);
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
        for (int i = 0; i < this.speeds.size() - 1; i++) {
            GlStateManager.color(1.0f, 0.3f, 1.0f, 1.0f);
            double y = this.speeds.get(i) * 10 * this.height;
            double y2 = this.speeds.get(i + 1) * 10 * this.height;
            double len = this.width / (this.speeds.size() - 1D);
            double yPos = (eventRender2D.getHeight() / 2f) - (this.height / 2f);

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
    }

    @EventTarget
    private void onBloom(EventBloom eventBloom) {
        Gui.drawRect(5, (eventBloom.getHeight() / 2) - (height / 2), 5 + width, (eventBloom.getHeight() / 2) + (height / 2), -1);
    }
}
