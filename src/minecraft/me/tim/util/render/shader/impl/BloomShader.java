package me.tim.util.render.shader.impl;

import me.tim.Statics;
import me.tim.util.render.shader.Shader;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector2f;
import java.nio.FloatBuffer;

public class BloomShader extends Shader {
    private float radius;
    private Vector2f direction;
    private FloatBuffer weights;

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
}
