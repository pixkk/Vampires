package com.tigerhix.vampirez;


import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.*;
import org.bukkit.scoreboard.*;
import com.tigerhix.vampirez.lib.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

public class Main extends JavaPlugin
{
    public ScoreboardManager sm;
    public ItemMessage messenger;
    public HashMap<String, Arena> arenas;
    public HashMap<String, Gamer> gamers;
    public HashMap<Location, Sign> signs;
    public HashMap<String, ItemStack[]> inventories;
    public MessagesConfig message;

    private Main main;
 
    
    public Main() {
        this.arenas = new HashMap<String, Arena>();
        this.gamers = new HashMap<String, Gamer>();
        this.signs = new HashMap<Location, Sign>();
        this.inventories = new HashMap<String, ItemStack[]>();
    }

    public void onEnable() {
    	
    	Bukkit.getConsoleSender().sendMessage("§c========================================\n");
    	Bukkit.getConsoleSender().sendMessage("§c			Enabling VAMPIREZ			\n");
    	Bukkit.getConsoleSender().sendMessage("§c========================================\n");
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.sm = this.getServer().getScoreboardManager();
        this.messenger = new ItemMessage(this);
        this.message = new MessagesConfig();
        message.setup(this);

        int pluginId = 16858;
        try {
            Metrics metrics = new Metrics(this, pluginId);

            metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> {
                return Bukkit.getOnlinePlayers().size();
            }));

        } catch (Exception ignored) {

        }


        String version_server = Utils.getServerVersion();
        Bukkit.getConsoleSender().sendMessage("§cDetected server version: " + version_server);
//        Check if messages.yml is empty

        if(!message.disabled) {
            Bukkit.getConsoleSender().sendMessage("Minimum seconds: " + this.getConfig().getInt("general.requirements.min")+ "\n");
            Bukkit.getConsoleSender().sendMessage("Maximum seconds: " + this.getConfig().getInt("general.requirements.max")+ "\n");
            message.get();
            message.reloadconf();
            message.save();


            this.getCommand("vampire").setExecutor(new Commands(this));
            new Config(this);
            new Game(this);
            new Listeners(this);
            new Utils(this);
            new ItemTemplate();
            for (final Player player : this.getServer().getOnlinePlayers()) {
                this.gamers.put(player.getName(), new Gamer(this, player.getName()));
            }
            Bukkit.getConsoleSender().sendMessage("§cEnabled arenas:" + "\n");
            int i = 1;
            for (final String name : this.getConfig().getStringList("arenas.enabled-arenas")) {
                this.arenas.put(name, new Arena(this, name));
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
    	Bukkit.getConsoleSender().sendMessage("§c========================================\n");
    	Bukkit.getConsoleSender().sendMessage("§c			Disabling VAMPIREZ			\n");
    	Bukkit.getConsoleSender().sendMessage("§c========================================\n");
    }
}
