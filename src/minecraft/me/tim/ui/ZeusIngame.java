package me.tim.ui;

import me.tim.Statics;
import me.tim.features.event.EventBloom;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.api.EventManager;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Module;
import me.tim.util.render.shader.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;
import java.util.ArrayList;

public class ZeusIngame {
    public ZeusIngame() {
        EventManager.register(this);
    }

    @EventTarget
    private void onRender2d(EventRender2D event) {
        int index = 0;
        for (Module module : this.getSortedModules()) {
            if (!module.isEnabled()) continue;

            String suffix = (module.getSuffix().isEmpty() ? "" : " " + module.getSuffix());
            int height = Statics.getFontRenderer().FONT_HEIGHT;
            int x = 5, y = 5 + (index * height), width = Statics.getFontRenderer().getStringWidth(module.getName() + suffix);
            Statics.getFontRenderer().drawString(module.getName() + " " + module.getSuffix(), x, y, -1);
            index++;
        }

        Statics.getFontRenderer().drawString("FPS: ", 10, event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT * 2 - 6, new Color(205, 75, 205).getRGB());
        Statics.getFontRenderer().drawString(String.valueOf(Minecraft.getDebugFPS()), 12 + Statics.getFontRenderer().getStringWidth("FPS: "), event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT * 2 - 6, -1);

        Statics.getFontRenderer().drawString("Ping: ", 10, event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT - 3, new Color(205, 75, 205).getRGB());
        Statics.getFontRenderer().drawString(String.valueOf(Statics.getMinecraft().getCurrentServerData().pingToServer), 12 + Statics.getFontRenderer().getStringWidth("Ping: "), event.getHeight() - Statics.getFontRenderer().FONT_HEIGHT - 3, -1);
    }

    @EventTarget
    private void onBloom(EventBloom bloomEvent) {
        int index = 0;
        for (Module module : this.getSortedModules()) {
            if (!module.isEnabled()) continue;

            String suffix = (module.getSuffix().isEmpty() ? "" : " " + module.getSuffix());
            int height = Statics.getFontRenderer().FONT_HEIGHT;
            int x = 5, y = 5 + (index * height), width = Statics.getFontRenderer().getStringWidth(module.getName() + suffix);
            Gui.drawRect(x - 2, y - 2, x + width, y + height, new Color(255, 255, 255).getRGB());
            index++;
        }
    }

    private ArrayList<Module> getSortedModules() {
        ArrayList<Module> sorted = Statics.getZeus().moduleManager.getVisibleModules();
        sorted.sort((mod1, mod2) -> Float.compare(Statics.getFontRenderer().getStringWidth(mod2.getName() + (mod2.getSuffix().isEmpty() ? "" : " " + mod2.getSuffix())), Statics.getFontRenderer().getStringWidth(mod1.getName() + (mod1.getSuffix().isEmpty() ? "" : " " + mod1.getSuffix()))));
        return sorted;
    }
}
