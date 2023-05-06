package me.tim.util.render.shader.impl;

import me.tim.util.render.shader.Shader;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector2f;
import java.nio.FloatBuffer;

public class BlurShader extends Shader {
    private Vector2f direction;
    private float radius;
    private FloatBuffer weights;

    public BlurShader() {
        super(new ResourceLocation("zeus/shader/blur.fsh"));
    }

    @Override
    public void setupUniforms() {
        this.setUniformi("textureIn", 0);
        this.setUniformf("texelSize", 1.0F / this.resolution.getScaledWidth(), 1.0F / this.resolution.getScaledHeight());
        this.setUniformf("direction", this.direction.getX(), this.direction.getY());
        this.setUniformf("radius", this.radius);
        GL20.glUniform1(this.getUniform("weights"), this.weights);
    }

    public Vector2f getDirection() {
        return direction;
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public FloatBuffer getWeights() {
        return weights;
    }

    public void setWeights(FloatBuffer weights) {
        this.weights = weights;
    }
}
