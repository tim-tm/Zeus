package me.tim.features.command.impl;

import me.tim.Statics;
import me.tim.features.command.Command;
import me.tim.features.module.Module;

public class CommandToggle extends Command {
    public CommandToggle() {
        super("Toggle modules!", "<ModuleName>", new String[]{"t", "toggle"});
    }

    @Override
    public void run(String[] args) {
        if (args.length > 1 && !args[1].isEmpty()) {
            for (Module module : Statics.getZeus().moduleManager.getModules()) {
                if (args[1].equalsIgnoreCase(module.getName())) {
                    module.toggle();
                    Statics.addChatMessage(String.format("Toggled Module: %s", module.getName()));
                }
            }
        } else {
            this.fail();
        }
    }
}
