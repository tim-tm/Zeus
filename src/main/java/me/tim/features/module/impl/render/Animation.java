package me.tim.features.module.impl.render;

import me.tim.features.event.EventRenderItem;
import me.tim.features.event.EventTick;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

public class Animation extends Module {
    private ModeSetting modeSetting;
    private BlockMode blockMode;

    public Animation() {
        super("Animation", "Different blocking animations!", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Override
    protected void setupSettings() {
        this.settings.add(this.modeSetting = new ModeSetting("Mode", "Mode of blocking!", BlockMode.values(), BlockMode.VANILLA));
    }

    @EventTarget
    private void onTick(EventTick eventTick) {
        this.blockMode = (BlockMode) EnumUtil.fromName(this.modeSetting.getCurrentMode().getName(), BlockMode.values());
    }

    @EventTarget
    private void onRenderItem(EventRenderItem eventRenderItem) {
        if (this.blockMode == null) return;

        switch (this.blockMode) {
            case DEFAULT:
                eventRenderItem.setCancelled(true);
                this.transformFirstPersonItem(eventRenderItem.getF(), eventRenderItem.getF1());
                this.doBlockTransformations();
                break;
            case JELLO:
                eventRenderItem.setCancelled(true);
                GlStateManager.translate(0.5f, 0.0f, -0.5f);
                this.transformFirstPersonItem(eventRenderItem.getF(), eventRenderItem.getF1());
                this.doBlockTransformations();
                break;
        }
    }

    /**
     * @see net.minecraft.client.renderer.ItemRenderer
     */
    private void doBlockTransformations() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * @see net.minecraft.client.renderer.ItemRenderer
     * @param equipProgress
     * @param swingProgress
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private enum BlockMode implements ModeSetting.ModeTemplate {
        VANILLA("Vanilla"),
        DEFAULT("Default"),
        JELLO("Jello");

        private final String name;

        BlockMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
