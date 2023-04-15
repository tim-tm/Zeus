package me.tim.ui.click.component;

import me.tim.Statics;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.util.render.shader.RenderUtil;
import org.apache.commons.lang3.StringUtils;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Panel implements CUIComponent {
    private final Category category;
    private ArrayList<ModuleButton> moduleButtons;
    private Vector2f position, dragPosition;
    private Vector2f size;
    private boolean dragging, extended;

    public Panel(Category category, Vector2f position, Vector2f size) {
        this.category = category;
        this.position = position;
        this.dragPosition = new Vector2f();
        this.size = size;
        this.moduleButtons = new ArrayList<>();
        this.extended = false;
        this.dragging = false;
        this.setup();
    }

    private void setup() {
        int index = 1;
        for (Module module : Statics.getZeus().moduleManager.getModules()) {
            if (module.getCategory().equals(category)) {
                this.moduleButtons.add(new ModuleButton(module, new Vector2f(this.position.x, this.position.y + (index*this.size.y)), new Vector2f(this.size.x, this.size.y)));
                index++;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.position.x, this.position.y, this.position.x + this.size.x, this.position.y + this.size.y, new Color(20, 20, 20, 255));
        String pnlName = StringUtils.capitalize(this.category.name().toLowerCase());
        Statics.getFontRenderer().drawString(pnlName, (int) (this.position.x + this.size.x / 2 - Statics.getFontRenderer().getStringWidth(pnlName) / 2), (int) (this.position.y + this.size.y / 2 - Statics.getFontRenderer().FONT_HEIGHT / 2), -1);

        if (this.dragging) {
            this.position.x = this.dragPosition.x + mouseX;
            this.position.y = this.dragPosition.y + mouseY;
        }

        if (!this.extended) return;
        int index = 1;
        float offset = 0;
        for (ModuleButton module : this.moduleButtons) {
            module.setPosition(new Vector2f(this.position.x, this.position.y + (index*this.size.y + offset)));
            module.drawScreen(mouseX, mouseY, partialTicks);
            offset += module.offset;
            index++;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.isHovered(this.position, this.size, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    this.dragPosition.x = this.position.x - mouseX;
                    this.dragPosition.y = this.position.y - mouseY;
                    this.dragging = true;
                    break;
                case 1:
                    this.extended = !this.extended;
                    break;
            }
        }

        if (!this.extended) return;
        for (ModuleButton module : this.moduleButtons) {
            module.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) this.dragging = false;

        if (!this.extended) return;
        for (ModuleButton module : this.moduleButtons) {
            module.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!this.extended) return;
        for (ModuleButton module : this.moduleButtons) {
            module.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.extended) return;
        for (ModuleButton module : this.moduleButtons) {
            module.keyTyped(typedChar, keyCode);
        }
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<ModuleButton> getModuleButtons() {
        return moduleButtons;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }
}
