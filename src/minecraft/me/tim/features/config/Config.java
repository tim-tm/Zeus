package me.tim.features.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.tim.Statics;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.Setting;
import me.tim.ui.click.settings.impl.BooleanSetting;
import me.tim.ui.click.settings.impl.ColorSetting;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.ui.click.settings.impl.NumberSetting;
import me.tim.util.common.FileUtil;

import java.awt.*;
import java.io.*;

public class Config {
    private String name;
    private File file;
    private final Gson gson;

    public Config(String additionalPath, String name) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        this.name = name + ".json";
        this.file = new File(FileUtil.getDataDir().getAbsolutePath() + additionalPath, this.name);
    }

    public void save() {
        if (!this.file.getParentFile().exists() && !this.file.getParentFile().mkdirs()) return;

        try (FileWriter writer = new FileWriter(this.file)){
            JsonObject finalObj = new JsonObject();
            for (Module module : Statics.getZeus().moduleManager.getModules()) {
                JsonArray array = new JsonArray();
                JsonObject modObj = new JsonObject();
                modObj.addProperty("key", module.getKey());
                modObj.addProperty("state", module.isEnabled());

                JsonObject settingsObj = new JsonObject();
                for (Setting setting : module.getSettings()) {
                    JsonArray value = new JsonArray();
                    if (setting instanceof BooleanSetting) {
                        BooleanSetting booleanSetting = (BooleanSetting) setting;
                        JsonObject object = new JsonObject();
                        object.addProperty("state", booleanSetting.getValue());
                        value.add(object);
                    }

                    if (setting instanceof ColorSetting) {
                        ColorSetting colorSetting = (ColorSetting) setting;
                        JsonObject object = new JsonObject();
                        object.addProperty("red", colorSetting.getColor().getRed());
                        object.addProperty("green", colorSetting.getColor().getGreen());
                        object.addProperty("blue", colorSetting.getColor().getBlue());
                        value.add(object);
                    }

                    if (setting instanceof ModeSetting) {
                        ModeSetting modeSetting = (ModeSetting) setting;
                        JsonObject object = new JsonObject();
                        object.addProperty("mode", modeSetting.getCurrentMode().getName());
                        value.add(object);
                    }

                    if (setting instanceof NumberSetting) {
                        NumberSetting numberSetting = (NumberSetting) setting;
                        JsonObject object = new JsonObject();
                        object.addProperty("value", numberSetting.getValue());
                        value.add(object);
                    }
                    settingsObj.add(setting.getName(), value);
                }

                array.add(modObj);
                array.add(settingsObj);

                finalObj.add(module.getName(), array);
            }

            this.gson.toJson(finalObj, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.printf("Failed to save config: %s | %s%n", this.name, this.file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    public void load() {
        if (!this.file.exists()) return;

        try (JsonReader reader = new JsonReader(new FileReader(this.file))) {
            JsonObject object = this.gson.fromJson(reader, JsonObject.class);
            for (Module module : Statics.getZeus().moduleManager.getModules()) {
                if (object.has(module.getName())) {
                    JsonArray modObj = object.getAsJsonArray(module.getName());
                    JsonObject moduleInfo = modObj.get(0).getAsJsonObject();
                    if (moduleInfo.has("key") && moduleInfo.has("state")) {
                        module.setKey(moduleInfo.get("key").getAsInt());
                        module.setEnabled(moduleInfo.get("state").getAsBoolean());
                    }

                    JsonObject settings = modObj.get(1).getAsJsonObject();
                    for (int i = 0; i < module.getSettings().size(); i++) {
                        if (settings.has(module.getSettings().get(i).getName())) {
                            JsonArray array = settings.get(module.getSettings().get(i).getName()).getAsJsonArray();
                            JsonObject setting = array.get(0).getAsJsonObject();

                            if (setting.has("mode")) {
                                ModeSetting modeSetting = (ModeSetting) module.getSettings().get(i);
                                modeSetting.setCurrentMode(setting.get("mode").getAsString());
                            }

                            if (setting.has("state")) {
                                BooleanSetting booleanSetting = (BooleanSetting) module.getSettings().get(i);
                                booleanSetting.setValue(setting.get("state").getAsBoolean());
                            }

                            if (setting.has("value")) {
                                NumberSetting numberSetting = (NumberSetting) module.getSettings().get(i);
                                numberSetting.setValue(setting.get("value").getAsFloat());
                            }

                            if (setting.has("red") && setting.has("green") && setting.has("blue")) {
                                ColorSetting colorSetting = (ColorSetting) module.getSettings().get(i);
                                colorSetting.setColor(new Color(setting.get("red").getAsInt(), setting.get("green").getAsInt(), setting.get("blue").getAsInt()));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.printf("Failed to load config: %s | %s%n", this.name, this.file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    public boolean delete() {
        if (this.file.exists()) {
            return this.file.delete();
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public void setName(String name) {
        this.name = name;
        this.file = new File(FileUtil.getDataDir().getAbsolutePath() + "\\", name);
    }
}
