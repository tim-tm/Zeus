package me.tim.ui.click.settings;

import javax.vecmath.Vector2f;

public abstract class Setting {
    private String name, description;
    private boolean visible;

    public Setting(String name, String description) {
        this(name, description, true);
    }

    public Setting(String name, String description, boolean visible) {
        this.name = name;
        this.description = description;
        this.visible = visible;
    }

    public abstract float draw(Vector2f position, Vector2f size, float offset, int mouseX, int mouseY);

    protected boolean isHovered(Vector2f position, Vector2f size, float mouseX, float mouseY) {
        return mouseX >= position.x && mouseX <= position.x + size.x && mouseY >= position.y && mouseY <= position.y + size.y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
