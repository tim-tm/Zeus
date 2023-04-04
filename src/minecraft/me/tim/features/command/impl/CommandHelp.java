package me.tim.features.command.impl;

import me.tim.Statics;
import me.tim.features.command.Command;

public class CommandHelp extends Command {
    public CommandHelp() {
        super("See command help!", "", new String[]{"help"});
    }

    @Override
    public void run(String[] args) {
        for (Command command : Statics.getZeus().commandManager.getCommands()) {
            Statics.addChatMessage(command.getFailMessage());
        }
    }
}
