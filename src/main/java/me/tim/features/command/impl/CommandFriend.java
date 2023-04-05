package me.tim.features.command.impl;

import me.tim.Statics;
import me.tim.features.command.Command;
import me.tim.features.config.impl.LocalConfig;
import me.tim.features.config.impl.OnlineConfig;
import me.tim.features.friend.Friend;
import me.tim.features.module.Module;

public class CommandFriend extends Command {
    public CommandFriend() {
        super("Manage friends!", "<add/remove/list> <friendName> <alias>", new String[]{"friend"});
    }

    @Override
    public void run(String[] args) {
        if (args.length <= 1 || args[1].isEmpty()) {
            this.fail();
            return;
        }

        if (args[1].equalsIgnoreCase("list")) {
            Statics.addChatMessage("Friends: ");
            for (Friend friend : Statics.getZeus().friendManager.getFriends()) {
                Statics.addChatMessage(String.format("%s, alias: %s", friend.getName(), friend.getAlias()));
            }
        } else if (args.length > 2 && !args[2].isEmpty()) {
            String alias = args[2];
            if (args.length > 3 && !args[3].isEmpty()) {
                alias = args[3];
            }

            String type = args[1].toLowerCase();
            switch (type) {
                case "add":
                    Statics.getZeus().friendManager.addFriend(new Friend(args[2], alias));
                    Statics.addChatMessage("Friend: " + args[2] + "|" + alias + " added!");
                    break;
                case "remove":
                    Statics.getZeus().friendManager.removeFriend(args[2]);
                    Statics.addChatMessage("Friend: " + args[2] + " removed!");
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
