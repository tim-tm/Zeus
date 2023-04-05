package me.tim.features.command.impl;

import me.tim.Statics;
import me.tim.features.command.Command;
import me.tim.features.module.Module;
import org.lwjgl.input.Keyboard;

public class CommandBind extends Command {
    public CommandBind() {
        super("Set keybinds!", "<ModuleName> <KeyBind>", new String[]{"bind"});
    }

    @Override
    public void run(String[] args) {
        if (args.length > 2 && !args[1].isEmpty() && !args[2].isEmpty()) {
            for (Module module : Statics.getZeus().moduleManager.getModules()) {
                if (args[1].equalsIgnoreCase(module.getName())) {
                    module.setKey(Keyboard.getKeyIndex(args[2].toUpperCase()));
                    Statics.addChatMessage(String.format("Bound %s to %s!", module.getName(), Keyboard.getKeyName(module.getKey())));
                }
            }
        } else {
            this.fail();
        }
    }
}
