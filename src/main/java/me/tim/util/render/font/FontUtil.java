package me.tim.util.render.font;

import java.awt.*;

public class FontUtil {
    public static CFontRenderer normal = new CFontRenderer("normal.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer other = new CFontRenderer("other.ttf", Font.PLAIN, 16, 7, false);

    // Helvetica
    public static CFontRenderer helvetica = new CFontRenderer("/helvetica/Helvetica.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer helvetica_bold = new CFontRenderer("/helvetica/Helvetica-Bold.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer helvetica_light = new CFontRenderer("/helvetica/helvetica-light.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer helvetica_oblique = new CFontRenderer("/helvetica/Helvetica-Oblique.ttf", Font.PLAIN, 16, 7, false);

    public static CFontRenderer makeFont(String name, int size) {
        return new CFontRenderer(name, Font.PLAIN, size, 7, false);
    }
}
