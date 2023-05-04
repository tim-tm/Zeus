package me.tim.util.render;

import me.tim.Statics;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;

import static org.lwjgl.opengl.GL11.*;

public class StencilUtil {
    public static void dispose() {
        glDisable(GL_STENCIL_TEST);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    public static void init() {
        Statics.getMinecraft().getFramebuffer().bindFramebuffer(false);
        StencilUtil.checkSetupFBO(Statics.getMinecraft().getFramebuffer());
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);

        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glColorMask(false, false, false, false);
    }

    public static void read() {
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, 1, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public static void unInit() {
        glDisable(GL_STENCIL_TEST);
    }

    public static void erase(boolean invert) {
        glStencilFunc(invert ? GL_EQUAL : GL_NOTEQUAL, 1, 65535);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        glAlphaFunc(GL_GREATER, 0.0f);
    }

    public static void write(boolean renderClipLayer) {
        StencilUtil.checkSetupFBO();
        glClearStencil(0);
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 65535);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        if (!renderClipLayer) {
            GlStateManager.colorMask(false, false, false, false);
        }
    }

    public static void write(boolean renderClipLayer, Framebuffer fb) {
        StencilUtil.checkSetupFBO(fb);
        glClearStencil(0);
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 65535);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        if (!renderClipLayer) {
            GlStateManager.colorMask(false, false, false, false);
        }
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = Statics.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            StencilUtil.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    public static void checkSetupFBO(Framebuffer fbo) {
        if (fbo != null && fbo.depthBuffer > -1) {
            StencilUtil.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    public static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Statics.getMinecraft().displayWidth, Statics.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
    }
}
