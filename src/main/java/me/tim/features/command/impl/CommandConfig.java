package me.tim.features.command.impl;

import me.tim.Statics;
import me.tim.features.command.Command;
import me.tim.features.config.impl.LocalConfig;
import me.tim.features.config.impl.OnlineConfig;

public class CommandConfig extends Command {
    public CommandConfig() {
        super("Create or remove configs!", "<load/save/remove/list> <configName>", new String[]{"config", "cfg"});
    }

    @Override
    public void run(String[] args) {
        if (args.length <= 1 || args[1].isEmpty()) {
            this.fail();
            return;
        }

        if (args[1].equalsIgnoreCase("list")) {
            Statics.addChatMessage("Local Configs: ");
            for (LocalConfig localConfig : Statics.getZeus().configManager.getLocalConfigs()) {
                Statics.addChatMessage(localConfig.getName());
            }

            Statics.addChatMessageRaw("");
            Statics.addChatMessage("Online Configs: ");
            for (OnlineConfig onlineConfig : Statics.getZeus().configManager.getOnlineConfigs()) {
                Statics.addChatMessageRaw(onlineConfig.getName());
            }
        } else if (args.length > 2 && !args[2].isEmpty()) {
            String type = args[1].toLowerCase();
            switch (type) {
                case "load":
                    Statics.getZeus().configManager.load(args[2]);
                    Statics.addChatMessage("Config: " + args[2] + " loaded!");
                    break;
                case "save":
                    Statics.getZeus().configManager.addConfig(new LocalConfig(args[2]));
                    Statics.addChatMessage("Config: " + args[2] + " saved!");
                    break;
                case "remove":
                    Statics.getZeus().configManager.remove(args[2]);
                    Statics.addChatMessage("Config: " + args[2] + " removed!");
                    break;
                default:
                    this.fail();
                    break;
            }
        } else {
            this.fail();
        }
    }
}
