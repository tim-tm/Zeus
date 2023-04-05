package me.tim.features.config;

import me.tim.Statics;
import me.tim.features.config.impl.DefaultConfig;
import me.tim.features.config.impl.LocalConfig;
import me.tim.features.config.impl.OnlineConfig;
import me.tim.util.common.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigManager {
    private final ArrayList<LocalConfig> localConfigs;
    private final ArrayList<OnlineConfig> onlineConfigs;

    public ConfigManager() {
        this.onlineConfigs = new ArrayList<>();

        this.localConfigs = new ArrayList<>();
        this.localConfigs.add(new DefaultConfig());

        this.detectLocalConfigs();
    }

    public boolean load(Class<? extends Config> clazz) {
        for (OnlineConfig onlineConfig : this.onlineConfigs) {
            if (onlineConfig.getClass().equals(clazz)) {
                onlineConfig.load();
                return true;
            }
        }

        for (LocalConfig localConfig : this.localConfigs) {
            if (localConfig.getClass().equals(clazz)) {
                localConfig.load();
                return true;
            }
        }
        return false;
    }

    public boolean load(String name) {
        for (OnlineConfig onlineConfig : this.onlineConfigs) {
            if (onlineConfig.getName().split("\\.")[0].equalsIgnoreCase(name)) {
                onlineConfig.load();
                return true;
            }
        }

        for (LocalConfig localConfig : this.localConfigs) {
            if (localConfig.getName().split("\\.")[0].equalsIgnoreCase(name)) {
                localConfig.load();
                return true;
            }
        }
        return false;
    }

    public void remove(Class<? extends Config> clazz) {
        for (LocalConfig localConfig : this.localConfigs) {
            if (localConfig.getClass().equals(clazz)) {
                if (localConfig.delete()) {
                    //this.localConfigs.remove(localConfig);
                } else {
                    Statics.addChatMessage("Failed to delete Config: " + localConfig.getName());
                }
            }
        }
    }

    public void remove(String name) {
        for (LocalConfig localConfig : this.localConfigs) {
            if (localConfig.getName().split("\\.")[0].equalsIgnoreCase(name)) {
                if (localConfig.delete()) {
                    //this.localConfigs.remove(localConfig);
                } else {
                    Statics.addChatMessage("Failed to delete Config: " + localConfig.getName());
                }
            }
        }
    }

    public void addConfig(LocalConfig config) {
        this.localConfigs.add(config);
        config.save();

        if (!config.getFile().exists()) {
            Statics.addChatMessage("Failed to create Config: " + config.getName());
        } else {
            Statics.addChatMessage("Saved config: " + config.getName());
        }
    }

    public void save() {
        for (OnlineConfig onlineConfig : this.onlineConfigs) {
            onlineConfig.save();
        }

        for (LocalConfig localConfig : localConfigs) {
            localConfig.save();
        }
    }

    public void save(Class<? extends Config> clazz) {
        for (OnlineConfig onlineConfig : this.onlineConfigs) {
            if (onlineConfig.getClass().equals(clazz)) onlineConfig.save();;
        }

        for (LocalConfig localConfig : localConfigs) {
            if (localConfig.getClass().equals(clazz)) localConfig.save();
        }
    }

    private void detectLocalConfigs() {
        File localConfigDir = new File(FileUtil.getDataDir().getAbsolutePath() + "/local/");
        if (localConfigDir.exists()) {
            for (File file : Objects.requireNonNull(localConfigDir.listFiles())) {
                if (file.getName().split("\\.").length > 1 && file.getName().split("\\.")[1].equals("json")) {
                    LocalConfig cfg = new LocalConfig(file.getName().split("\\.")[0]);
                    if (!this.localConfigs.contains(cfg))
                        this.localConfigs.add(cfg);
                }
            }
        }
    }

    public ArrayList<LocalConfig> getLocalConfigs() {
        return localConfigs;
    }

    public ArrayList<OnlineConfig> getOnlineConfigs() {
        return onlineConfigs;
    }
}
