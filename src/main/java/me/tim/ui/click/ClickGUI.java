package me.tim.ui.click;

import me.tim.features.module.Category;
import me.tim.ui.click.component.CUIComponent;
import me.tim.ui.click.component.Panel;
import me.tim.util.render.animation.Animation;
import me.tim.util.render.animation.AnimationType;
import me.tim.util.render.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {
    private static final float PANEL_WIDTH = 100, PANEL_HEIGHT = 25;
    public static Framebuffer BLOOM_BUFFER;

    private final ArrayList<CUIComponent> components;
    private boolean initialized = false;
    private final Animation inAnimation;

    public ClickGUI() {
        this.components = new ArrayList<>();
        this.inAnimation = new Animation(450, AnimationType.EXPO, Animation.AnimationState.IN, false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        RenderUtil.scale(width / 2f, height / 2f, this.inAnimation.animate());
        BLOOM_BUFFER.framebufferClear();
        BLOOM_BUFFER.bindFramebuffer(true);
        for (CUIComponent component : this.components) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
        BLOOM_BUFFER.unbindFramebuffer();
        RenderUtil.drawBloom(BLOOM_BUFFER.framebufferTexture, 25, 2, new Color(190, 35, 190), false);

        for (CUIComponent component : this.components) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (CUIComponent component : this.components) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (CUIComponent component : this.components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for (CUIComponent component : this.components) {
            component.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (CUIComponent component : this.components) {
            component.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
        if (!this.initialized) {
            int index = 0;
            for (Category category : Category.values()) {
                Panel panel = new Panel(category, new Vector2f(100 + ((PANEL_WIDTH + 30) * index), 200), new Vector2f(PANEL_WIDTH, PANEL_HEIGHT));
                this.components.add(panel);
                index++;
            }
            this.initialized = true;
        }
        this.inAnimation.reset();
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }
}
