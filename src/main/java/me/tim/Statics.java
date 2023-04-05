package me.tim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;

public class Statics {
    private static double speed = 0.265f;

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP getPlayer() {
        return getMinecraft().thePlayer;
    }

    public static PlayerControllerMP getPlayerController() {
        return getMinecraft().playerController;
    }

    public static WorldClient getWorld() {
        return getMinecraft().theWorld;
    }

    public static Timer getTimer() {
        return getMinecraft().timer;
    }

    public static Session getSession() {
        return getMinecraft().session;
    }

    public static void setSession(Session session) {
        getMinecraft().session = session;
    }

    public static FontRenderer getFontRenderer() {
        return getMinecraft().fontRendererObj;
    }

    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

    public static Zeus getZeus() {
        return getMinecraft().zeusClient;
    }

    public static void speed(float d) {
        float yaw = EntityPlayer.movementYaw != null ? EntityPlayer.movementYaw : getPlayer().rotationYaw;
        yaw = (float) Math.toRadians(yaw);
        Statics.speed = d;
        getPlayer().motionX = -MathHelper.sin(yaw) * d;
        getPlayer().motionZ = MathHelper.cos(yaw) * d;
    }

    public static void sendPacket(Packet<?> packet) {
        Statics.getPlayer().sendQueue.addToSendQueue(packet);
    }

    public static void multMotion(float multiplier) {
        getPlayer().motionX *= multiplier;
        getPlayer().motionZ *= multiplier;
    }

    public static void addChatMessage(String msg) {
        addChatMessageRaw("§7[§cZeus§7]: " + msg);
    }

    public static void addChatMessageRaw(String msg) {
        getPlayer().addChatMessage(new ChatComponentText(msg));
    }
}
