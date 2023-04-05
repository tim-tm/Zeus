package me.tim.features.command;

import me.tim.Statics;
import net.minecraft.util.ChatComponentText;

public abstract class Command {
    private final String description, usage;
    private final String[] names;

    public Command(String description, String usage, String[] names) {
        this.description = description;
        this.usage = usage;
        this.names = names;
    }

    public abstract void run(String[] args);

    public String getFailMessage() {
        StringBuilder builder = new StringBuilder();
        for (String name : this.names) {
            builder.append(".").append(name).append(" ");
        }
        return builder.append(usage).append(" | ").append(description).toString();
    }

    protected void fail() {
        Statics.addChatMessage(this.getFailMessage());
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getNames() {
        return names;
    }
}
