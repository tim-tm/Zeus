package me.tim.ui.notify;

import me.tim.Statics;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.EventShader;
import me.tim.features.event.api.EventManager;
import me.tim.features.event.api.EventTarget;
import me.tim.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;

public class NotificationRenderer {
    private final ArrayList<Notification> queue;

    public NotificationRenderer() {
        EventManager.register(this);
        this.queue = new ArrayList<>();
    }

    @EventTarget
    public void onRender(EventRender2D event) {
        for (int i = 0; i < this.queue.size(); i++) {
            Notification notification = this.queue.get(i);
            if (notification.getTimer().elapsed(notification.getDuration())) {
                this.queue.remove(notification);
                notification.getTimer().reset();
                notification.getAnimation().reset();
                continue;
            }
            this.drawNotification(event.getWidth(), event.getHeight(), notification, i, false);
        }
    }

    @EventTarget
    private void onShader(EventShader eventShader) {
        for (int i = 0; i < this.queue.size(); i++) {
            this.drawNotification(eventShader.getWidth(), eventShader.getHeight(), this.queue.get(i), i, true);
        }
    }

    private void drawNotification(int width, int height, Notification notification, int index, boolean back) {
        int x = width / 2 - 70 / 2, y = 5 + 25 * index, x2 = width / 2 + 70 / 2, y2 = 25 + 25 * index;

        GlStateManager.pushMatrix();
        RenderUtil.scale(x + (x2-x) / 2f, y + (y2-y) / 2f, notification.getAnimation().animate());
        if (back) {
            Gui.drawRect(x, y, x2, y2, new Color(0, 0, 0, 70).getRGB());
        } else {
            Gui.drawRect(x, y, x + (int) ((x2 - x) * notification.getTimer().getElapsedTime() / notification.getDuration()), y2, new Color(notification.getType().getColor().getRed(), notification.getType().getColor().getGreen(), notification.getType().getColor().getBlue(), 70).getRGB());
            Statics.getFontRenderer().drawString(notification.getTitle(), width / 2 - Statics.getFontRenderer().getStringWidth(notification.getTitle()) / 2, (int) (y + (y2 - y) / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2), new Color(245, 245, 245).getRGB());
        }
        GlStateManager.popMatrix();
    }

    public void sendNotification(Notification notification) {
        this.queue.add(notification);
    }
}
