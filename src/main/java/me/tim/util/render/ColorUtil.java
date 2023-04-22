package me.tim.util.render;

import java.awt.*;

public class ColorUtil {
    public static Color step(Color from, Color to, float amount) {
        int[] diff = {
                (int) (Math.abs(to.getRed() - from.getRed()) * amount),
                (int) (Math.abs(to.getGreen() - from.getGreen()) * amount),
                (int) (Math.abs(to.getBlue() - from.getBlue()) * amount),
                (int) (Math.abs(to.getAlpha() - from.getAlpha()) * amount)
        };
        return new Color(from.getRed() + diff[0], from.getGreen() + diff[1], from.getBlue() + diff[2], from.getAlpha() + diff[3]);
    }

    public static Color rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        Color color = new Color(Color.HSBtoRGB(hue, saturation, brightness));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }
}
