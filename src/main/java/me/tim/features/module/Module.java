package me.tim.features.module;

import me.tim.Statics;
import me.tim.features.event.api.EventManager;
import me.tim.ui.click.settings.Setting;
import me.tim.ui.notify.Notification;
import me.tim.util.render.animation.Animation;
import me.tim.util.render.animation.AnimationType;

import java.util.ArrayList;

public abstract class Module {
    private final String name;
    private String suffix;
    private int key;
    private final String description;
    private final Category category;
    private final boolean visible;
    private boolean enabled = false;
    protected final ArrayList<Setting> settings;
    private final Animation animation;

    public Module(String name, String description, int key, Category category) {
        this(name, description, key, category, true);
    }

    public Module(String name, String description, int key, Category category, boolean visible) {
        this.name = name;
        this.description = description;
        this.suffix = "";
        this.key = key;
        this.category = category;
        this.visible = visible;
        this.settings = new ArrayList<>();
        this.setupSettings();
        this.animation = new Animation(450, AnimationType.CIRC, Animation.AnimationState.IN, false);
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.onEnable();
            this.enabled = true;
        } else {
            this.onDisable();
            this.enabled = false;
        }
    }

    public void toggle() {
        this.animation.reset();
        this.onToggle();
        this.setEnabled(!this.isEnabled());
    }

    public void onToggle() { }

    public void onEnable() {
        EventManager.register(this);
        Statics.getZeus().notificationRenderer.sendNotification(new Notification(this.getName(), this.getDescription(), Notification.NotificationType.SUCCESS));
    }

    public void onDisable() {
        EventManager.unregister(this);
        Statics.getZeus().notificationRenderer.sendNotification(new Notification(this.getName(), this.getDescription(), Notification.NotificationType.FAILURE));
    }

    protected abstract void setupSettings();
}
