package me.tim;

import me.tim.features.command.CommandManager;
import me.tim.features.config.ConfigManager;
import me.tim.features.config.impl.DefaultConfig;
import me.tim.features.event.EventJoin;
import me.tim.features.event.EventPacket;
import me.tim.features.event.api.EventManager;
import me.tim.features.event.api.EventTarget;
import me.tim.features.friend.FriendManager;
import me.tim.features.module.ModuleManager;
import me.tim.features.script.ScriptManager;
import me.tim.ui.ZeusIngame;
import me.tim.ui.click.ClickGUI;
import me.tim.ui.notify.Notification;
import me.tim.ui.notify.NotificationRenderer;
import me.tim.util.common.FileUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.util.ResourceLocation;
import viamcp.ViaMCP;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Zeus {
    public static final String VERSION = "v0.4";

    public ModuleManager moduleManager;
    public NotificationRenderer notificationRenderer;
    public ZeusIngame zeusIngame;
    public ClickGUI clickGUI;
    public CommandManager commandManager;
    public ConfigManager configManager;
    public FriendManager friendManager;
    public ScriptManager scriptManager;

    private final IntegrationManager integrationManager;
    private final ExploitPrevention exploitPrevention;

    public Zeus() {
        this.moduleManager = new ModuleManager();
        this.notificationRenderer = new NotificationRenderer();
        this.zeusIngame = new ZeusIngame();
        this.clickGUI = new ClickGUI();
        this.commandManager = new CommandManager();
        this.configManager = new ConfigManager();
        this.friendManager = new FriendManager();
        this.scriptManager = new ScriptManager();

        this.integrationManager = new IntegrationManager();
        this.exploitPrevention = new ExploitPrevention();

        ViaMCP.getInstance().start();
        ViaMCP.getInstance().initAsyncSlider();
    }

    public void shutdown() {
        this.configManager.save(DefaultConfig.class);
        this.integrationManager.shutdown();
        this.exploitPrevention.shutdown();
    }

    private static final class IntegrationManager {
        private final ArrayList<Integration> integrations;

        public IntegrationManager() {
            this.integrations = new ArrayList<>();
            this.integrations.add(new DiscordIntegration());
        }

        public ArrayList<Integration> getIntegrations() {
            return integrations;
        }

        public void shutdown() {
            for (Integration integration : this.integrations) {
                integration.shutdown();
            }
        }

        private interface Integration {
            void shutdown();
        }

        private static final class DiscordIntegration implements Integration {
            private static String APPLICATION_ID;

            public DiscordIntegration() {
                File tokenFile = new File(FileUtil.getRealPath(new ResourceLocation("zeus/.tokens")));
                if (tokenFile.exists()) {
                    try (Scanner scanner = new Scanner(tokenFile)) {
                        String line;
                        while (scanner.hasNextLine()) {
                            line = scanner.nextLine();
                            String[] split = line.split(":");
                            if (split.length > 1 && split[0].equals("DISCORD_APP")) {
                                APPLICATION_ID = split[1];
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
                    System.out.printf("Discord RPC successfully setup. User: %s:%s%n", user.username, user.discriminator);
                }).build();
                DiscordRPC.discordInitialize(APPLICATION_ID, handlers, true);

                this.update("", "Idling");
                EventManager.register(this);
            }

            @EventTarget
            private void onJoin(EventJoin eventJoin) {
                this.update(Statics.getMinecraft().getCurrentServerData().serverIP, "Cheatin");
            }

            public void update(String state, String details) {
                DiscordRichPresence richPresence = new DiscordRichPresence
                        .Builder(state)
                        .setDetails(details)
                        .setBigImage("default", String.format("Zeus %s", Zeus.VERSION))
                        .setStartTimestamps(System.currentTimeMillis())
                        .build();
                DiscordRPC.discordUpdatePresence(richPresence);
            }

            public void shutdown() {
                DiscordRPC.discordShutdown();
            }
        }
    }

    private static final class ExploitPrevention {
        public ExploitPrevention() {
            EventManager.register(this);
        }

        @EventTarget
        private void onPacket(EventPacket eventPacket) {
            if (eventPacket.getPacket() instanceof S48PacketResourcePackSend) {
                S48PacketResourcePackSend packet = (S48PacketResourcePackSend) eventPacket.getPacket();
                try {
                    String url = URLDecoder.decode(packet.getURL().substring("level://".length()), StandardCharsets.UTF_8.toString());
                    if (new URI(packet.getURL()).getScheme().equals("level") && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                        if (Statics.getPlayer() != null) {
                            Statics.addChatMessage("Blocked file access!");
                            Statics.addChatMessage("Directory: " + url.substring(3));
                            Statics.getZeus().notificationRenderer.sendNotification(new Notification("Exploit Patch", "Blocked malicious file access from server!", Notification.NotificationType.SUCCESS));
                        }
                        eventPacket.setCancelled(true);
                    }
                } catch (UnsupportedEncodingException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void shutdown() {
            EventManager.unregister(this);
        }
    }
}
