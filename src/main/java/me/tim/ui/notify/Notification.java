package me.tim.ui.notify;

import me.tim.util.Timer;
import me.tim.util.render.animation.Animation;
import me.tim.util.render.animation.AnimationType;

import java.awt.*;

public class Notification {
    private final String title, description;
    private final NotificationType type;
    private final long duration;
    private final Timer timer;
    private final Animation animation;

    public Notification(String title, String description, NotificationType type) {
        this(title, description, type, 2000);
    }

    public Notification(String title, String description, NotificationType type, long duration) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.duration = duration;
        this.timer = new Timer();
        this.animation = new Animation(400, AnimationType.QUINT, Animation.AnimationState.IN, false);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public NotificationType getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public Timer getTimer() {
        return timer;
    }

    public Animation getAnimation() {
        return animation;
    }

    public enum NotificationType {
        SUCCESS(new Color(4, 128, 57)),
        FAILURE(new Color(210, 20, 20)),
        WARNING(new Color(255, 132, 0));

        private final Color color;
        NotificationType(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }
}
