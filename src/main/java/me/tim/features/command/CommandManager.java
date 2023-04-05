package me.tim.features.command;

import me.tim.features.command.impl.*;

import java.util.ArrayList;

public class CommandManager {
    private final ArrayList<Command> commands;

    public CommandManager() {
        this.commands = new ArrayList<>();
        this.commands.add(new CommandToggle());
        this.commands.add(new CommandHelp());
        this.commands.add(new CommandBind());
        this.commands.add(new CommandConfig());
        this.commands.add(new CommandFriend());
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
