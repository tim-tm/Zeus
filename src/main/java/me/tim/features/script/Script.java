package me.tim.features.script;

import me.tim.util.common.FileUtil;
import net.minecraft.util.ResourceLocation;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Script {
    private final File file;
    private final ScriptEngineManager manager;
    private final ScriptEngine engine;
    private final Invocable invocable;

    private String name, description, author;

    public Script(ResourceLocation path) {
        this.manager = new ScriptEngineManager();
        this.engine = this.manager.getEngineByName("JavaScript");
        this.invocable = (Invocable) this.engine;
        this.file = new File(FileUtil.getRealPath(path));

        try {
            this.engine.eval(new BufferedReader(new FileReader(this.file)));
            Object result = this.invocable.invokeFunction("bootstrap");
            String[] arr = (String[]) this.invocable.invokeMethod(this.engine.get("Java"), "to", result, "java.lang.String[]");
            this.name = arr[0];
            this.description = arr[1];
            this.author = arr[2];
        } catch (ScriptException | FileNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ScriptEngineManager getManager() {
        return manager;
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public Invocable getInvocable() {
        return invocable;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
