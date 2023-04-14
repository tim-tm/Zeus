package me.tim.features.module;

import me.tim.features.module.impl.combat.Backtrack;
import me.tim.features.module.impl.combat.KillAura;
import me.tim.features.module.impl.combat.Tickbase;
import me.tim.features.module.impl.combat.Velocity;
import me.tim.features.module.impl.exploit.Blink;
import me.tim.features.module.impl.exploit.FastPlace;
import me.tim.features.module.impl.exploit.Phase;
import me.tim.features.module.impl.move.*;
import me.tim.features.module.impl.player.*;
import me.tim.features.module.impl.render.*;

import java.util.ArrayList;

public class ModuleManager {
    private final ArrayList<Module> modules;

    public ModuleManager() {
        this.modules = new ArrayList<>();
        this.modules.add(new Backtrack());
        this.modules.add(new KillAura());
        this.modules.add(new Velocity());
        this.modules.add(new Tickbase());

        this.modules.add(new Fly());
        this.modules.add(new NoSlow());
        this.modules.add(new Scaffold());
        this.modules.add(new Step());
        this.modules.add(new Speed());

        this.modules.add(new Cleaner());
        this.modules.add(new FastBridge());
        this.modules.add(new Stealer());
        this.modules.add(new Teams());

        this.modules.add(new Blink());
        this.modules.add(new FastPlace());
        this.modules.add(new Phase());

        this.modules.add(new Animation());
        this.modules.add(new ClickGUI());
        this.modules.add(new ESP());
        this.modules.add(new Fullbright());
        this.modules.add(new MotionGraph());
        this.modules.add(new NoCameraClip());
        this.modules.add(new PingGraph());
        this.modules.add(new Radar());
        this.modules.add(new TargetHUD());
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public ArrayList<Module> getVisibleModules() {
        ArrayList<Module> vis = modules;
        vis.removeIf(vi -> !vi.isVisible());
        return vis;
    }

    public Module getModuleByClass(Class<? extends Module> clazz) {
        for (Module module : this.modules) {
            if (module.getClass().equals(clazz)) return module;
        }
        return null;
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (module.getName().equals(name)) return module;
        }
        return null;
    }

    public void handleKey(int keyCode) {
        for (Module module : this.modules) {
            if (module.getKey() == keyCode) module.toggle();
        }
    }
}
