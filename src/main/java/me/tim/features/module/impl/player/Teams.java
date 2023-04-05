package me.tim.features.module.impl.player;

import me.tim.Statics;
import me.tim.features.event.EventJoin;
import me.tim.features.event.EventPacket;
import me.tim.features.event.EventPreMotion;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.ui.click.settings.impl.ModeSetting;
import me.tim.util.common.EnumUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.server.S3EPacketTeams;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Teams extends Module {
    private final ArrayList<String> teammates;

    public Teams() {
        super("Teams", "Prevents the KillAura from attacking teammates!", Keyboard.KEY_NONE, Category.PLAYER);
        this.teammates = new ArrayList<>();
    }

    @Override
    protected void setupSettings() {
    }

    @EventTarget
    private void onJoin(EventJoin event) {
        this.teammates.clear();
        Statics.addChatMessage("Cleared teammates!");
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (event.getState().equals(EventPacket.State.SEND)) return;

        if (event.getPacket() instanceof S3EPacketTeams) {
            int playerCol = -1;
            for (String player : ((S3EPacketTeams) event.getPacket()).getPlayers()) {
                if (player.equals(Statics.getPlayer().getName())) {
                    playerCol = ((S3EPacketTeams) event.getPacket()).getColor();
                } else if (playerCol == ((S3EPacketTeams) event.getPacket()).getColor()) {
                    this.teammates.add(player);
                }
            }
        }
    }

    public ArrayList<String> getTeammates() {
        return teammates;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.teammates.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
