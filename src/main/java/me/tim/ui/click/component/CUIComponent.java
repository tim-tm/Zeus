package me.tim.ui.click.component;

import javax.vecmath.Vector2f;
import java.io.IOException;

public interface CUIComponent {
    void drawScreen(int mouseX, int mouseY, float partialTicks);

    void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException;
    void mouseReleased(int mouseX, int mouseY, int state);
    void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick);

    void keyTyped(char typedChar, int keyCode) throws IOException;

    default boolean isHovered(Vector2f position, Vector2f size, float mouseX, float mouseY) {
        return mouseX >= position.x && mouseX <= position.x + size.x && mouseY >= position.y && mouseY <= position.y + size.y;
    }
}
