package com.tigerhix.vampirez;

import org.bukkit.scoreboard.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.entity.*;

public class Gamer
{
    public Main plugin;
    public String name;
    public Arena playing;
    public Arena selected;
    public int cash;
    public boolean alive;
    public boolean transferring;
    public Scoreboard board;
    public Objective obj;
    public int coins;
    public int vampireKills;
    public int survivorKills;
    public int vampireWins;
    public int survivorWins;
    public int flameArrowID;
    
    @SuppressWarnings("deprecation")
	public Gamer(final Main plugin, final String name) {
        this.plugin = plugin;
        this.name = name;
        this.playing = null;
        this.selected = null;
        this.cash = 0;
        this.alive = true;
        this.transferring = false;
        this.coins = plugin.getConfig().getInt("players." + name + ".coins");
        this.vampireKills = plugin.getConfig().getInt("players." + name + ".vampire-kills");
        this.survivorKills = plugin.getConfig().getInt("players." + name + ".survivor-kills");
        this.vampireWins = plugin.getConfig().getInt("players." + name + ".vampire-wins");
        this.survivorWins = plugin.getConfig().getInt("players." + name + ".survivor-wins");
        this.board = plugin.sm.getNewScoreboard();
        (this.obj = this.board.registerNewObjective("players", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.flameArrowID = -1;
    }
    
    @SuppressWarnings("deprecation")
	public void reset() {
        this.playing = null;
        this.selected = null;
        this.cash = 0;
        this.alive = true;
        this.transferring = false;
        this.board = this.plugin.sm.getNewScoreboard();
        (this.obj = this.board.registerNewObjective("players", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
        this.flameArrowID = -1;
    }
    public void resetScoreboard() {
        this.board = this.plugin.sm.getNewScoreboard();
        (this.obj = this.board.registerNewObjective("players", "dummy")).setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void save() {
        this.plugin.getConfig().set("players." + this.name + ".coins", (Object)this.coins);
        this.plugin.getConfig().set("players." + this.name + ".vampire-kills", (Object)this.vampireKills);
        this.plugin.getConfig().set("players." + this.name + ".survivor-kills", (Object)this.survivorKills);
        this.plugin.getConfig().set("players." + this.name + ".vampire-wins", (Object)this.vampireWins);
        this.plugin.getConfig().set("players." + this.name + ".survivor-wins", (Object)this.survivorWins);
        this.plugin.saveConfig();
    }
    
    public void sendMessage(final String message) {
        this.getPlayer().sendMessage(message);
    }
    
    public void addCash(final int amount) {
        this.cash += amount;
//        Bukkit.getConsoleSender().sendMessage("§cCash:"+this.cash);
        this.plugin.messenger.sendMessage(this.getPlayer(), (this.alive ? ChatColor.GOLD : ChatColor.DARK_RED) + "" + ChatColor.BOLD + "+" + amount + " " + (this.alive ? plugin.message.get().get("gold-1") : plugin.message.get().get("blood-1")), 3);
//        Bukkit.getConsoleSender().sendMessage("§cMessage:" +(this.alive ? ChatColor.GOLD : ChatColor.DARK_RED) + "" + ChatColor.BOLD + "+" + amount + " " + (this.alive ? plugin.message.get().get("gold-1") : plugin.message.get().get("blood-1")));

    }
    
    public void addCoins(final int amount) {
        this.coins += amount;
        this.plugin.messenger.sendMessage(this.getPlayer(), ChatColor.YELLOW + "" + ChatColor.BOLD + "+" + amount + " "+ plugin.message.get().get("gold-2"), 3);
    }
    
    public void ding() {
        this.getPlayer().playSound(this.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    
    public Location getNearestSurvivor() {
        final List<Location> survivors = new ArrayList<Location>();
        for (final Gamer survivor : this.playing.getSurvivors()) {
            survivors.add(survivor.getPlayer().getLocation());
        }
        if (survivors.size() == 0) {
            return Utils.getRandomNearbyLocation(this.getPlayer().getLocation());
        }
        survivors.add(new Location(this.getPlayer().getWorld(), 9999.0, 9999.0, 9999.0));
        int min = survivors.size() - 1;
        for (int i = 0; i < survivors.size(); ++i) {
            if (survivors.get(i).distance(this.getPlayer().getLocation()) < survivors.get(min).distance(this.getPlayer().getLocation())) {
                min = i;
            }
        }
        return survivors.get(min);
    }
    
    public Location getNearestVampire() {
        final List<Location> vampires = new ArrayList<Location>();
        for (final Gamer vampire : this.playing.getVampires()) {
            vampires.add(vampire.getPlayer().getLocation());
        }
        vampires.add(new Location(this.getPlayer().getWorld(), 9999.0, 9999.0, 9999.0));
        int min = vampires.size() - 1;
        for (int i = 0; i < vampires.size(); ++i) {
            if (vampires.get(i).distance(this.getPlayer().getLocation()) < vampires.get(min).distance(this.getPlayer().getLocation())) {
                min = i;
            }
        }
        return vampires.get(min);
    }
    
    public Location getNearestZombieSpawn() {
        final List<Location> spawns = this.playing.zombieSpawns;
        spawns.add(new Location(this.getPlayer().getWorld(), 9999.0, 9999.0, 9999.0));
        int min = spawns.size() - 1;
        for (int i = 0; i < spawns.size(); ++i) {
            if (spawns.get(i).distance(this.getPlayer().getLocation()) < spawns.get(min).distance(this.getPlayer().getLocation())) {
                min = i;
            }
        }
        return spawns.get(min);
    }
    
    public Player getPlayer() {
        return this.plugin.getServer().getPlayer(this.name);
    }
    
    public String getDisplayName() {
        return (this.alive ? ChatColor.GOLD : ChatColor.DARK_RED) + this.name + ChatColor.GRAY;
    }
}
