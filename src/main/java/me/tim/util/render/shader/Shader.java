package me.tim.util.render.shader;

import me.tim.Statics;
import me.tim.util.common.FileUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public abstract class Shader {
    private final ResourceLocation fragmentLoc, vertexLoc;
    private int programId;
    protected ScaledResolution resolution;

    public Shader(ResourceLocation fragmentLoc, ResourceLocation vertexLoc) {
        this.fragmentLoc = fragmentLoc;
        this.vertexLoc = vertexLoc;
        this.resolution = new ScaledResolution(Statics.getMinecraft());
    }

    public Shader(ResourceLocation fragmentLoc) {
        this(fragmentLoc, new ResourceLocation("zeus/shader/default.vsh"));
    }

    private int compile(ResourceLocation location, int shaderType) {
        String fileContent = FileUtil.readFile(location);
        int shaderId = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shaderId, fileContent);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("Shader: " + location.getResourcePath() + " failed to compile!");
            System.err.println(GL20.glGetShaderInfoLog(shaderId, 4096));
            return -6969;
        }
        return shaderId;
    }

    public void setup() {
        int vertId = this.compile(this.vertexLoc, GL20.GL_VERTEX_SHADER);
        int fragId = this.compile(this.fragmentLoc, GL20.GL_FRAGMENT_SHADER);
        if (fragId == -6969 || vertId == -6969) return;

        this.programId = GL20.glCreateProgram();
        GL20.glAttachShader(this.programId, vertId);
        GL20.glAttachShader(this.programId, fragId);

        GL20.glDeleteShader(vertId);
        GL20.glDeleteShader(fragId);

        GL20.glLinkProgram(this.programId);

        if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == 0) {
            System.err.println("Failed to link program!");
            System.err.println("Fragment Shader: " + this.fragmentLoc);
            System.err.println("Vertex Shader: " + this.vertexLoc);
            System.err.println(GL20.glGetProgramInfoLog(this.programId, 4096));
        }
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void use() {
        GL20.glUseProgram(this.programId);
        this.setupUniforms();
    }

    public void drawQuads(float x, float y, float width, float height) {
        if (Statics.getMinecraft().gameSettings.ofFastRender) return;
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }

    public void drawQuads() {
        if (Statics.getMinecraft().gameSettings.ofFastRender) return;
        float width = (float) this.resolution.getScaledWidth_double();
        float height = (float) this.resolution.getScaledHeight_double();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(width, height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(width, 0);
        GL11.glEnd();
    }

    protected void setUniformf(String name, float... args) {
        int loc = GL20.glGetUniformLocation(this.programId, name);
        switch (args.length) {
            case 1:
                GL20.glUniform1f(loc, args[0]);
                break;
            case 2:
                GL20.glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                GL20.glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                GL20.glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    protected void setUniformi(String name, int... args) {
        int loc = GL20.glGetUniformLocation(this.programId, name);
        if (args.length > 1) GL20.glUniform2i(loc, args[0], args[1]);
        else GL20.glUniform1i(loc, args[0]);
    }

    protected int getUniform(String name) {
        return GL20.glGetUniformLocation(this.programId, name);
    }

    public abstract void setupUniforms();

    public ResourceLocation getFragmentLoc() {
        return fragmentLoc;
    }

    public ResourceLocation getVertexLoc() {
        return vertexLoc;
    }

    public int getProgramId() {
        return programId;
    }

    public void setResolution(ScaledResolution resolution) {
        this.resolution = resolution;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }
}
