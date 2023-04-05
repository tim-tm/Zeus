package me.tim.features.friend;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.tim.util.common.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class FriendManager {
    private final File friendsFile;
    private final Gson gson;

    private final ArrayList<Friend> friends;

    public FriendManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.friends = new ArrayList<>();

        this.friendsFile = new File(FileUtil.getDataDir().getAbsolutePath(), "friends.json");
        if (!this.friendsFile.exists()) {
            try {
                this.friendsFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.load();
        }
    }

    private void load() {
        if (!this.friendsFile.exists()) return;

        try (JsonReader reader = new JsonReader(new FileReader(this.friendsFile))) {
            JsonObject object = this.gson.fromJson(reader, JsonObject.class);
            if (object == null || object.entrySet().isEmpty()) return;

            for (Map.Entry<String, JsonElement> stringJsonElementEntry : object.entrySet()) {
                if (!this.contains(stringJsonElementEntry.getKey())) {
                    this.addFriend(new Friend(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString()));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load fiends-list!");
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            if (!this.friendsFile.exists()) {
                this.friendsFile.createNewFile();
            }

            FileWriter writer = new FileWriter(this.friendsFile);
            JsonObject jsonObject = new JsonObject();
            for (Friend friend : this.friends) {
                jsonObject.addProperty(friend.getName(), friend.getAlias());
            }
            this.gson.toJson(jsonObject, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to save friends-list!");
            e.printStackTrace();
        }
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
        this.save();
    }

    public void removeFriend(String name) {
        this.friends.removeIf(friend -> friend.getName().equalsIgnoreCase(name));
        this.save();
    }

    public void removeFriendB(String alias) {
        this.friends.removeIf(friend -> friend.getAlias().equalsIgnoreCase(alias));
        this.save();
    }

    public String getAlias(String name) {
        for (Friend friend : this.friends) {
            if (friend.getName().equals(name)) return friend.getAlias();
        }
        return name;
    }

    public boolean contains(String name) {
        for (Friend friend : this.friends) {
            if (friend.getName().equals(name)) return true;
        }
        return false;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }
}
