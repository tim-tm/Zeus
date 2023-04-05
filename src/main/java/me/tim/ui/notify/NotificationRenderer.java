package me.tim.ui.notify;

import me.tim.Statics;
import me.tim.features.event.EventRender2D;
import me.tim.features.event.api.EventManager;
import me.tim.features.event.api.EventTarget;
import me.tim.util.render.shader.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class NotificationRenderer {
    private ArrayList<Notification> queue;

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
                continue;
            }
            this.drawNotification(event, notification, i);
        }
    }

    private void drawNotification(EventRender2D event, Notification notification, int index) {
        int x = event.getWidth() - 200, y = event.getHeight() - 100 - 70 * index, x2 = event.getWidth() - 10, y2 = event.getHeight() - 50 - 70 * index;
        RenderUtil.drawRoundedRect(x, y, x2, y2, 5f, new Color(0, 0, 0, 70));
        RenderUtil.drawRoundedRect(x, y, x + (int) ((x2 - x) * notification.getTimer().getElapsedTime() / notification.getDuration()), y2, 5f, new Color(notification.getType().getColor().getRed(), notification.getType().getColor().getGreen(), notification.getType().getColor().getBlue(), 70));
        Statics.getFontRenderer().drawString(notification.getTitle(), x + 10, y + 10, new Color(245, 245, 245).getRGB());
        Statics.getFontRenderer().drawString(notification.getDescription(), x + 10, y + 30, new Color(245, 245, 245).getRGB());
    }

    public void sendNotification(Notification notification) {
        this.queue.add(notification);
    }
}
