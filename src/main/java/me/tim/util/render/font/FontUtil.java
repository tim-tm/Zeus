package me.tim.util.render.font;

import java.awt.*;

@SuppressWarnings("unused")
public class FontUtil {
    public static CFontRenderer normal = new CFontRenderer("normal.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer other = new CFontRenderer("other.ttf", Font.PLAIN, 16, 7, false);

    // Helvetica
    public static CFontRenderer helvetica = new CFontRenderer("/helvetica/Helvetica.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer helvetica_bold = new CFontRenderer("/helvetica/Helvetica-Bold.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer helvetica_light = new CFontRenderer("/helvetica/helvetica-light.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer helvetica_oblique = new CFontRenderer("/helvetica/Helvetica-Oblique.ttf", Font.PLAIN, 16, 7, false);

    // Poppins
    public static CFontRenderer poppins = new CFontRenderer("/poppins/Poppins-Regular.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer poppins_bold = new CFontRenderer("/poppins/Poppins-Bold.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer poppins_light = new CFontRenderer("/poppins/Poppins-Light.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer poppins_medium = new CFontRenderer("/poppins/Poppins-Medium.ttf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer poppins_semibold = new CFontRenderer("/poppins/Poppins-SemiBold.ttf", Font.PLAIN, 16, 7, false);

    public static CFontRenderer volte = new CFontRenderer("/volte/volte-medium.otf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer volte_bold = new CFontRenderer("/volte/volte-bold.otf", Font.PLAIN, 16, 7, false);
    public static CFontRenderer volte_semibold = new CFontRenderer("/volte/volte-semibold.ttf", Font.PLAIN, 16, 7, false);


    public static CFontRenderer makeFont(String name, int size) {
        return new CFontRenderer(name, Font.PLAIN, size, 7, false);
    }
}
