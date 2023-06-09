package me.tim;

import me.tim.features.event.EventMove;
import me.tim.util.render.font.CFontRenderer;
import me.tim.util.render.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.util.*;

import java.util.UUID;

public class Statics {
    private static double speed = 0.265f;
    public static Float movementYaw;

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

    public static CFontRenderer getFontRenderer() {
        return FontUtil.volte_semibold;
    }

    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

    public static Zeus getZeus() {
        return getMinecraft().zeusClient;
    }

    public static MovementInput getMovementInput() {
        return getPlayer().movementInput;
    }

    public static void speed(float d) {
        double yaw = Statics.movementYaw != null ? Statics.movementYaw : getPlayer().rotationYaw;
        boolean moving = !(getPlayer().moveForward == 0.0F && getPlayer().moveStrafing == 0.0F);
        boolean movingForward = getPlayer().moveForward > 0.0F;
        boolean movingBackward = getPlayer().moveForward < 0.0F;
        boolean movingRight = getPlayer().moveStrafing > 0.0F;
        boolean movingLeft = getPlayer().moveStrafing < 0.0F;
        boolean movingSideways = !(!movingLeft && !movingRight);
        boolean movingStraight = !(!movingForward && !movingBackward);
        if (moving) {
            if (movingForward && !movingSideways) {
                yaw += 0.0D;
            } else if (movingBackward && !movingSideways) {
                yaw += 180.0D;
            } else if (movingForward && movingLeft) {
                yaw += 45.0D;
            } else if (movingForward) {
                yaw -= 45.0D;
            } else if (!movingStraight && movingLeft) {
                yaw += 90.0D;
            } else if (!movingStraight && movingRight) {
                yaw -= 90.0D;
            } else if (movingBackward && movingLeft) {
                yaw += 135.0D;
            } else if (movingBackward) {
                yaw -= 135.0D;
            }

            yaw = Math.toRadians(yaw);
            Statics.speed = d;
            getPlayer().motionX = -MathHelper.sin((float) yaw) * d;
            getPlayer().motionZ = MathHelper.cos((float) yaw) * d;
        }
    }

    public static void motionFly(EventMove event, boolean yChange, double speed) {
        if (yChange) {
            if (Statics.getMovementInput().jump) {
                event.setY(speed);
            } else if (Statics.getMovementInput().sneak) {
                event.setY(-speed);
            } else {
                event.setY(0.0);
            }
        } else {
            event.setY(0);
        }

        if (Statics.getMovementInput().moveForward > 0 || Statics.getMovementInput().moveStrafe > 0) {
            Statics.setMoveSpeed(event, speed);
        } else {
            Statics.setMoveSpeed(event, 0);
        }
    }

    public static void setMoveSpeed(EventMove event, final double speed) {
        double forward = getMovementInput().moveForward;
        double strafe = getMovementInput().moveStrafe;
        float yaw = Statics.movementYaw != null ? Statics.movementYaw : getPlayer().rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            Statics.speed = speed;
            event.setX(forward * speed * MathHelper.cos((float) Math.toRadians(yaw + 90.0f)) + strafe * speed * MathHelper.sin((float) Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * MathHelper.sin((float) Math.toRadians(yaw + 90.0f)) - strafe * speed * MathHelper.cos((float) Math.toRadians(yaw + 90.0f)));
        }
    }

    public static double getSpeed() {
        return Math.hypot(Statics.getPlayer().motionX, Statics.getPlayer().motionZ);
    }

    public static double getBPS() {
        double delta = MathHelper.sqrt_double(getPlayer().motionX * getPlayer().motionX + getPlayer().motionZ * getPlayer().motionZ);
        return delta * 20 * getTimer().timerSpeed;
    }

    public static int getPing(UUID player) {
        if (getMinecraft() == null) return 0;
        if (getMinecraft().isIntegratedServerRunning()) return 0;
        if (getMinecraft().getNetHandler() == null || getMinecraft().getNetHandler().getPlayerInfo(player) == null) return 0;
        return getMinecraft().getNetHandler().getPlayerInfo(player).getResponseTime();
    }

    public static double getCacheSpeed() {
        return speed;
    }

    public static int getPing() {
        return getPing(getPlayer().getUniqueID());
    }

    public static void sendPacket(Packet<?> packet) {
        Statics.getPlayer().sendQueue.addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        Statics.getMinecraft().getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
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
