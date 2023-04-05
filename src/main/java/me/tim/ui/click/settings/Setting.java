package me.tim.ui.click.settings;

public class Setting {
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
