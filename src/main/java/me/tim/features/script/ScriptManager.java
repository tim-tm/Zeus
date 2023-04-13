package me.tim.features.script;

import me.tim.Statics;
import me.tim.features.event.EventJoin;
import me.tim.features.event.EventPacket;
import me.tim.features.event.api.EventManager;
import me.tim.features.event.api.EventTarget;
import me.tim.features.module.Category;
import me.tim.features.module.Module;
import me.tim.util.common.FileUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.input.Keyboard;

import javax.script.ScriptException;
import java.io.File;
import java.util.ArrayList;

public class ScriptManager {
    private final ArrayList<Script> scripts;
    private boolean modsSet;

    public ScriptManager() {
        this.scripts = new ArrayList<>();
        this.modsSet = false;
        this.readScripts();
        EventManager.register(this);
    }

    public void readScripts() {
        File dataDir = new File(FileUtil.getRealPath(new ResourceLocation("zeus/script")));
        if (!dataDir.exists() || !dataDir.isDirectory() || dataDir.listFiles() == null) {
            System.out.println("No local scripts found!");
            return;
        }

        for (File file : dataDir.listFiles()) {
            if (FilenameUtils.getExtension(file.getName()).equals("js")) {
                Script newS = new Script(new ResourceLocation("zeus/script/" + file.getName()));
                this.scripts.add(newS);
                System.out.printf("Detected local script: %s, details: %s | %s by %s%n", newS.getFile().getName(), newS.getName(), newS.getDescription(), newS.getAuthor());
            }
        }
    }

    @EventTarget
    private void onTick(EventJoin eventJoin) {
        if (!this.modsSet) {
            for (Script script : this.scripts) {
                if (Statics.getZeus().moduleManager.getModuleByName(script.getName()) == null) {
                    Statics.getZeus().moduleManager.getModules().add(new Module(script.getName(), script.getDescription(), Keyboard.KEY_NONE, Category.SCRIPT) {
                        @Override
                        protected void setupSettings() { }
                    });
                }
            }
            this.modsSet = true;
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        for (Script script : this.scripts) {
            Module mod = Statics.getZeus().moduleManager.getModuleByName(script.getName());
            if (mod == null || !mod.isEnabled()) continue;

            try {
                Boolean obj = (Boolean) script.getInvocable().invokeFunction("onPacket", eventPacket.getPacket().getClass().getName(), eventPacket.getState().name().toUpperCase());
                if (obj != null) {
                    eventPacket.setCancelled(obj);
                    if (eventPacket.isCancelled()) {
                        System.out.printf("DEBUG: Cancelled onPacket in script: %s%n", script.getName());
                        System.out.printf("DEBUG: PacketEvent Info: Packet - %s, State - %s, Cancelled - %s%n", eventPacket.getPacket().getClass().getName(), eventPacket.getState().name().toUpperCase(), eventPacket.isCancelled());
                    }
                }
            } catch (ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Script> getScripts() {
        return scripts;
    }
}
