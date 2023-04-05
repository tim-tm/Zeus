package me.tim.util.common;

import me.tim.ui.click.settings.impl.ModeSetting;

public class EnumUtil {

    public static ModeSetting.ModeTemplate fromName(String name, ModeSetting.ModeTemplate[] valueSet) {
        for (ModeSetting.ModeTemplate anEnum : valueSet) {
            if (name.equals(anEnum.getName())) return anEnum;
        }
        return null;
    }
}
