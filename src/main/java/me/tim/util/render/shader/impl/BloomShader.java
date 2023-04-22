package me.tim.util.render.shader.impl;

import me.tim.Statics;
import me.tim.util.render.shader.Shader;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.nio.FloatBuffer;

public class BloomShader extends Shader {
    private float radius;
    private Vector2f direction;
    private FloatBuffer weights;
    private Color color;

    public BloomShader() {
        super(new ResourceLocation("zeus/shader/bloom.fsh"));
    }

    @Override
    public void setupUniforms() {
        this.setUniformi("inTexture", 0);
        this.setUniformi("textureToCheck", 16);
        this.setUniformf("radius", this.radius);
        this.setUniformf("texelSize", 1.0f / Statics.getMinecraft().displayWidth, 1.0f / Statics.getMinecraft().displayHeight);
        this.setUniformf("direction", this.direction.x, this.direction.y);
        GL20.glUniform1(this.getUniform("weights"), this.weights);
        this.setUniformf("color", this.color.getRed() / 255f, this.color.getGreen() / 255f, this.color.getBlue() / 255f);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getDirection() {
        return direction;
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
    }

    public FloatBuffer getWeights() {
        return weights;
    }

    public void setWeights(FloatBuffer weights) {
        this.weights = weights;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
