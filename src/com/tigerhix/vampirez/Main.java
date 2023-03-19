package com.tigerhix.vampirez;


import com.tigerhix.vampirez.lib.ItemMessage;
import com.tigerhix.vampirez.lib.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;

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

    public MessagesConfig getMessagesConfig() {
        return message;
    }

    public void onEnable() {
    	
    	Bukkit.getConsoleSender().sendMessage("§c========================================");
    	Bukkit.getConsoleSender().sendMessage("§c			Enabling VAMPIREZ			");
    	Bukkit.getConsoleSender().sendMessage("§c========================================");
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
    	Bukkit.getConsoleSender().sendMessage("§c========================================");
    	Bukkit.getConsoleSender().sendMessage("§c			Disabling VAMPIREZ			");
    	Bukkit.getConsoleSender().sendMessage("§c========================================");
    }
}
