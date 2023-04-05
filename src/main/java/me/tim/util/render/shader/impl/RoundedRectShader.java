package me.tim.util.render.shader.impl;

import me.tim.Statics;
import me.tim.util.render.shader.Shader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector2f;
import java.awt.*;

public class RoundedRectShader extends Shader {
    private Color color;
    private Vector2f size;
    private float radius;

    public RoundedRectShader() {
        super(new ResourceLocation("zeus/shader/roundedrect.fsh"));
    }

    @Override
    public void setupUniforms() {
        ScaledResolution sr = new ScaledResolution(Statics.getMinecraft());
        setUniformf("size", this.size.x * sr.getScaleFactor(), this.size.y * sr.getScaleFactor());
        setUniformf("radius", this.radius * sr.getScaleFactor());
        setUniformf("color", this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f, this.color.getAlpha() / 255f);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }
}
