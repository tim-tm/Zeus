package me.tim.features.script;

import me.tim.util.common.FileUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

public class ScriptManager {
    private final ArrayList<Script> scripts;

    public ScriptManager() {
        this.scripts = new ArrayList<>();
        this.readScripts();
    }

    public void readScripts() {
        File dataDir = new File(FileUtil.getRealPath(new ResourceLocation("zeus/script")));
        if (!dataDir.exists() || !dataDir.isDirectory() || dataDir.listFiles() == null) return;

        for (File file : dataDir.listFiles()) {
            if (FilenameUtils.getExtension(file.getName()).equals("js")) {
                Script newS = new Script(new ResourceLocation("zeus/script/" + file.getName()));
                this.scripts.add(newS);
                System.out.printf("Detected local script: %s, details: %s | %s by %s%n", newS.getFile().getName(), newS.getName(), newS.getDescription(), newS.getAuthor());
            }
        }
    }

    public ArrayList<Script> getScripts() {
        return scripts;
    }
}
