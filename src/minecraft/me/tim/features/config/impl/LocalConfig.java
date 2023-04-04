package me.tim.features.config.impl;

import me.tim.features.config.Config;

public class LocalConfig extends Config {
    public LocalConfig(String name) {
        super("/local/", name);
    }
}
