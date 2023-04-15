package me.tim.util.render.shader;

import me.tim.Statics;
import me.tim.util.common.MathUtil;
import me.tim.util.render.shader.impl.BloomShader;
import me.tim.util.render.shader.impl.CircleShader;
import me.tim.util.render.shader.impl.RoundedRectShader;
import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.optifine.model.BlockModelUtils;
import net.optifine.shaders.Shaders;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.nio.FloatBuffer;

public class RenderUtil {
    private static final RoundedRectShader ROUNDED_RECT_SHADER = new RoundedRectShader();
    private static final BloomShader BLOOM_SHADER = new BloomShader();
    private static final CircleShader CIRCLE_SHADER = new CircleShader();

    private static Framebuffer BLOOM_FRAMEBUFFER = new Framebuffer(1, 1, false);

    static {
        ROUNDED_RECT_SHADER.setup();
        BLOOM_SHADER.setup();
        CIRCLE_SHADER.setup();
    }

    public static void drawRoundedRect(float x, float y, float x2, float y2, float radius, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        ROUNDED_RECT_SHADER.setSize(new Vector2f(x2 - x, y2 - y));
        ROUNDED_RECT_SHADER.setRadius(radius);
        ROUNDED_RECT_SHADER.setColor(color);
        ROUNDED_RECT_SHADER.use();
        ROUNDED_RECT_SHADER.drawQuads(x - 1, y - 1, x2 - x + 2, y2 - y + 2);
        ROUNDED_RECT_SHADER.stop();
        GlStateManager.disableBlend();
    }

    public static void drawCircle(float x, float y, float x2, float y2, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        CIRCLE_SHADER.setSize(new Vector2f(x2 - x, y2 - y));
        CIRCLE_SHADER.setColor(color);
        CIRCLE_SHADER.use();
        CIRCLE_SHADER.drawQuads(x - 1, y - 1, x2 - x + 2, y2 - y + 2);
        CIRCLE_SHADER.stop();
        GlStateManager.disableBlend();
    }
    
    public static void drawRect(float x, float y, float x2, float y2, Color color) {
        if (x < x2)
        {
            float i = x;
            x = x2;
            x2 = i;
        }

        if (y < y2)
        {
            float j = y;
            y = y2;
            y2 = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getRed() / 255.f, color.getGreen() / 255.f, color.getBlue() / 255.f, color.getAlpha() / 255.f);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y, 0.0D).endVertex();
        worldrenderer.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != Statics.getMinecraft().displayWidth || framebuffer.framebufferHeight != Statics.getMinecraft().displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(Statics.getMinecraft().displayWidth, Statics.getMinecraft().displayHeight, true);
        }
        return framebuffer;
    }

    public static void drawBloom(int sourceTexture, int radius, int offset) {
        BLOOM_FRAMEBUFFER = RenderUtil.createFrameBuffer(BLOOM_FRAMEBUFFER);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        FloatBuffer weights = BufferUtils.createFloatBuffer(256);
        for (int i = 0; i <= radius; i++) {
            weights.put(MathUtil.calculateGaussianValue(i, radius).floatValue());
        }
        weights.rewind();

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);

        BLOOM_FRAMEBUFFER.framebufferClear();
        BLOOM_FRAMEBUFFER.bindFramebuffer(true);

        BLOOM_SHADER.setRadius(radius);
        BLOOM_SHADER.setDirection(new Vector2f(offset, 0));
        BLOOM_SHADER.setWeights(weights);
        BLOOM_SHADER.use();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture);
        BLOOM_SHADER.drawQuads();
        BLOOM_SHADER.stop();

        BLOOM_FRAMEBUFFER.unbindFramebuffer();

        Statics.getMinecraft().getFramebuffer().bindFramebuffer(true);

        BLOOM_SHADER.setDirection(new Vector2f(0, offset));
        BLOOM_SHADER.use();
        GL13.glActiveTexture(GL13.GL_TEXTURE16);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, BLOOM_FRAMEBUFFER.framebufferTexture);
        BLOOM_SHADER.drawQuads();
        BLOOM_SHADER.stop();

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();

        GlStateManager.bindTexture(0);
    }

    public static void drawBlockOutline(BlockPos pos, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();

        if (Config.isShaders()) {
            Shaders.disableTexture2D();
        }

        GlStateManager.disableDepth();
        Block block = Statics.getWorld().getBlockState(pos).getBlock();

        double d0 = Statics.getPlayer().lastTickPosX + (Statics.getPlayer().posX - Statics.getPlayer().lastTickPosX) * Statics.getTimer().renderPartialTicks;
        double d1 = Statics.getPlayer().lastTickPosY + (Statics.getPlayer().posY - Statics.getPlayer().lastTickPosY) * Statics.getTimer().renderPartialTicks;
        double d2 = Statics.getPlayer().lastTickPosZ + (Statics.getPlayer().posZ - Statics.getPlayer().lastTickPosZ) * Statics.getTimer().renderPartialTicks;
        AxisAlignedBB axisalignedbb = block.getSelectedBoundingBox(Statics.getWorld(), pos);
        Block.EnumOffsetType block$enumoffsettype = block.getOffsetType();

        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            axisalignedbb = BlockModelUtils.getOffsetBoundingBox(axisalignedbb, block$enumoffsettype, pos);
        }

        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb.expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2), color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();

        if (Config.isShaders()) {
            Shaders.enableTexture2D();
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawEntityBox(Entity entity, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();

        Vec3 pos = RenderUtil.getInterpolatedPosition(entity);
        double x = pos.xCoord - Statics.getMinecraft().getRenderManager().renderPosX;
        double y = pos.yCoord - Statics.getMinecraft().getRenderManager().renderPosY;
        double z = pos.zCoord - Statics.getMinecraft().getRenderManager().renderPosZ;

        float f = entity.width / 2.0F;
        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entity.posX + x, axisalignedbb.minY - entity.posY + y, axisalignedbb.minZ - entity.posZ + z, axisalignedbb.maxX - entity.posX + x, axisalignedbb.maxY - entity.posY + y, axisalignedbb.maxZ - entity.posZ + z);
        RenderGlobal.drawOutlinedBoundingBox(axisalignedbb1, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawEntityBox2d(Entity entity, float lineWidth, Color color) {
        GlStateManager.resetColor();
        Gui.drawRect(18, 0, (int) (18 + lineWidth), 70, color.getRGB());
        Gui.drawRect(-18, 0, (int) (-18 - lineWidth), 70, color.getRGB());

        Gui.drawRect(18, 0, -18, (int) lineWidth, color.getRGB());
        Gui.drawRect(18, 70, -18, (int) (70 - lineWidth), color.getRGB());
    }

    public static boolean drawEntityStatic(Entity entity, Vec3 realPos, float alpha) {
        float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * Statics.getTimer().renderPartialTicks;
        int i = entity.getBrightnessForRender(Statics.getTimer().renderPartialTicks);

        if (entity.isBurning())
        {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        return Statics.getMinecraft().getRenderManager().doRenderEntity(entity, realPos.xCoord - Statics.getMinecraft().getRenderManager().renderPosX, realPos.yCoord - Statics.getMinecraft().getRenderManager().renderPosY, realPos.zCoord - Statics.getMinecraft().getRenderManager().renderPosZ, f, Statics.getTimer().renderPartialTicks, true);
    }

    public static Vec3 getInterpolatedPosition(Entity entity) {
        return new Vec3(
                MathUtil.interpolate(entity.lastTickPosX, entity.posX, Statics.getTimer().renderPartialTicks),
                MathUtil.interpolate(entity.lastTickPosY, entity.posY, Statics.getTimer().renderPartialTicks),
                MathUtil.interpolate(entity.lastTickPosZ, entity.posZ, Statics.getTimer().renderPartialTicks));
    }

    public static void drawHead(ResourceLocation skin, int x, int y, int width, int height) {
        GlStateManager.color(1, 1, 1, 1);
        Statics.getMinecraft().getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
        Gui.drawScaledCustomSizeModalRect(x, y, 40.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
    }
}
