package com.tigerhix.vampirez;


import com.google.common.io.Closer;
import com.tigerhix.vampirez.configs.Config;
import com.tigerhix.vampirez.configs.MessagesConfig;
import com.tigerhix.vampirez.configs.MigrateStats;
import com.tigerhix.vampirez.configs.StatsConfig;
import com.tigerhix.vampirez.lib.ItemMessage;
import com.tigerhix.vampirez.lib.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin
{
    public ScoreboardManager sm;
    public ItemMessage messenger;
    public HashMap<String, Arena> arenas;
    public HashMap<String, Gamer> gamers;
    public HashMap<Location, Sign> signs;
    public HashMap<String, ItemStack[]> inventories;
    public MessagesConfig message;
    public StatsConfig statsConfig;

    private Main main;
 
    
    public Main() {
        this.arenas = new HashMap<String, Arena>();
        this.gamers = new HashMap<String, Gamer>();
        this.signs = new HashMap<Location, Sign>();
        this.inventories = new HashMap<String, ItemStack[]>();
    }

    public static boolean isCurrentlyReloading() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String clazz = element.getClassName();
            if (clazz.startsWith("org.bukkit.craftbukkit.") && clazz.endsWith(".CraftServer") && element.getMethodName().equals("reload")) {
                return true;
            }
        }
        return false;
    }

    public MessagesConfig getMessagesConfig() {
        return message;
    }
    public StatsConfig getStatsConfig() {
        return statsConfig;
    }
    public String getSpigotVersion() throws IOException {
        Closer closer = Closer.create();
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=105477").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            InputStreamReader isr = (InputStreamReader) closer.register(new InputStreamReader(con.getInputStream()));
            BufferedReader br = (BufferedReader) closer.register(new BufferedReader(isr));
            String readLine = br.readLine();
            if (closer != null) {
                closer.close();
            }
            return readLine;
        } catch (Throwable th) {
            if (closer != null) {
                try {
                    closer.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
    public static boolean isVersionNewer(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        for (int i = 0; i < parts1.length; i++) {
            int v1 = Integer.parseInt(parts1[i]);
            int v2 = Integer.parseInt(parts2[i]);

            if (v1 < v2) {
                return false; // Version 1 is not newer
            } else if (v1 > v2) {
                return true; // Version 1 is newer
            }
            // If they are equal, continue to the next component
        }

        // If all components are equal, the versions are the same
        return false;
    }

    public void onEnable() {
    	
    	Bukkit.getConsoleSender().sendMessage("§c========================================");
    	Bukkit.getConsoleSender().sendMessage("§c			Enabling VAMPIREZ			");
    	Bukkit.getConsoleSender().sendMessage("§c========================================");
        PluginDescriptionFile file = this.getDescription();
        try {
            String version1 = file.getVersion().replace("-RELEASE", "");
            String version2 = getSpigotVersion().replace("-RELEASE", "");

            boolean isNewer = isVersionNewer(version2, version1);
//            Bukkit.getConsoleSender().sendMessage("§c" + version2);
//            Bukkit.getConsoleSender().sendMessage("§c" + version1);

            if (isNewer) {
                Bukkit.getConsoleSender().sendMessage("§c========================================");
                Bukkit.getConsoleSender().sendMessage("§c			VAMPIREZ UPDATE FOUND:       ");
                Bukkit.getConsoleSender().sendMessage("§c			NEW VERSION: "+version2+"                ");
                Bukkit.getConsoleSender().sendMessage("§c========================================");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.sm = this.getServer().getScoreboardManager();
        this.messenger = new ItemMessage(this);
        this.message = new MessagesConfig();
        message.setup(this);
        this.statsConfig = new StatsConfig();
        this.statsConfig.setup(this);

//        Migrating stats to file stats.yml
        new MigrateStats(this);

        int pluginId = 16858;
        try {
            Metrics metrics = new Metrics(this, pluginId);

            metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> {
                return Bukkit.getOnlinePlayers().size();
            }));

        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("bStats failed, maybe it disabled.");
        }


        String version_server = Utils.getServerVersion();
        Bukkit.getConsoleSender().sendMessage("§cDetected server version: " + version_server);
//        Check if messages.yml is empty

        if(!message.disabled) {
            Bukkit.getConsoleSender().sendMessage("Minimum seconds: " + this.getConfig().getInt("general.requirements.min"));
            Bukkit.getConsoleSender().sendMessage("Maximum seconds: " + this.getConfig().getInt("general.requirements.max"));
            message.get();
            message.reloadconf();
            message.save();


            this.getCommand("vampire").setExecutor(new Commands(this));
            this.getCommand("vampire").setTabCompleter(new CommandsTabCompleter(this));

            new Config(this);
            new Game(this);
            new Listeners(this);
            new Utils(this);
            new ItemTemplate();
            for (final Player player : this.getServer().getOnlinePlayers()) {
                this.gamers.put(player.getName(), new Gamer(this, player.getName()));
            }
            Bukkit.getConsoleSender().sendMessage("§cEnabled arenas:");
            int i = 1;
            for (final String name : this.getConfig().getStringList("arenas.enabled-arenas")) {
                Arena arena = new Arena(this, name);
                this.arenas.put(name, arena);
                Bukkit.getConsoleSender().sendMessage("§c" + i+". " +name+ "");
                i++;
            }

            for (final String str : this.getConfig().getStringList("signs")) {
                final Sign sign = new Sign(this, this.arenas.get(str.split("@")[1]), Utils.stringToLocation(str.split("@")[0]).getBlock());
                sign.startTimer();
                this.signs.put(Utils.stringToLocation(str.split("@")[0]), sign);
            }
            Game.lobby = Utils.stringToLocation(this.getConfig().getString("general.lobby"));
        }


    }
    public void onDisable() {
    	this.reloadConfig();
        Bukkit.getConsoleSender().sendMessage("§c========================================");
        Bukkit.getConsoleSender().sendMessage("§c			Disabling VAMPIREZ			");
        Bukkit.getConsoleSender().sendMessage("§c========================================");
        if (isCurrentlyReloading()) {
            Bukkit.getConsoleSender().sendMessage("§c========================================");
            Bukkit.getConsoleSender().sendMessage("§c	   DON`T USE /RELOAD COMMAND!        ");
            Bukkit.getConsoleSender().sendMessage("§c	   YOU CAN GET UNEXPECTED ERRORS.    ");
            Bukkit.getConsoleSender().sendMessage("§c========================================");
        }
    }
}
