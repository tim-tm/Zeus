package me.tim.util.render.shader.impl;

import me.tim.util.render.shader.Shader;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector2f;
import java.awt.*;

public class CircleShader extends Shader {
    private Color color;
    private Vector2f size;

    public CircleShader() {
        super(new ResourceLocation("zeus/shader/circle.fsh"));
    }

    @Override
    public void setupUniforms() {
        setUniformf("size", this.size.x * this.resolution.getScaleFactor(), this.size.y * this.resolution.getScaleFactor());
        setUniformf("color", this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f, this.color.getAlpha() / 255f);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }
}
