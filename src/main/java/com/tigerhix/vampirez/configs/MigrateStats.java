package com.tigerhix.vampirez.configs;

import com.tigerhix.vampirez.Main;

public class MigrateStats {
    private final Main plugin;
    public MigrateStats(Main main) {
        this.plugin = main;
        migrate(main);
    }

    private void migrate(Main plugin) {
        if (plugin.getConfig().get("players") != null) {
            this.plugin.statsConfig.set("players", plugin.getConfig().get("players"));
            plugin.getConfig().set("players", null);
            plugin.saveConfig();
        }
    }
}
