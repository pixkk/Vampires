package com.tigerhix.vampirez;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Arena {
    public Main plugin;
    public String name;
    public String status;
    public List<Gamer> gamers;
    public List<Entity> zombies;
    public Location survivorSpawn;
    public Location vampireSpawn;
    public Location lobbySpawn;

    public String survivorSpawnString;
    public String vampireSpawnString;
    public String lobbySpawnString;
    public List<String> zombieSpawnsString;

    public List<Location> zombieSpawns;
    public int firstX;
    public int firstZ;
    public int secondX;
    public int secondZ;
    public int wave;
    public int timeLeft;
    public int timePlayed;
    public int waitingSeconds;
    public int waitingID;
    public int matchID;
    public int scoreboardID;

    public Scoreboard board;
    Objective obj;

    public List<Gamer> getGamers() {
        return gamers;
    }

    public Arena(final Main plugin, final String name) {
        this.plugin = plugin;
        this.name = name;
        this.status = "waiting";
        this.gamers = new CopyOnWriteArrayList<Gamer>();
        this.zombies = new CopyOnWriteArrayList<Entity>();
        this.survivorSpawn = Utils.stringToLocation(plugin.getConfig().getString("arenas." + name + ".survivor-spawn"));
        this.vampireSpawn = Utils.stringToLocation(plugin.getConfig().getString("arenas." + name + ".vampire-spawn"));

        this.lobbySpawn = Utils.stringToLocation(plugin.getConfig().getString("arenas." + name + ".lobby-spawn"));
        this.zombieSpawns = Utils.stringListToLocation(plugin.getConfig().getStringList("arenas." + name + ".zombie-spawns"));

        this.lobbySpawnString = plugin.getConfig().getString("arenas." + name + ".lobby-spawn");
        this.vampireSpawnString = plugin.getConfig().getString("arenas." + name + ".vampire-spawn");
        this.survivorSpawnString = plugin.getConfig().getString("arenas." + name + ".survivor-spawn");
        this.zombieSpawnsString = plugin.getConfig().getStringList("arenas." + name + ".zombie-spawns");

        this.firstX = ((plugin.getConfig().getString("arenas." + name + ".coordinate") == null) ? 0 : Integer.parseInt(plugin.getConfig().getString("arenas." + name + ".coordinate").split(",")[0]));
        this.firstZ = ((plugin.getConfig().getString("arenas." + name + ".coordinate") == null) ? 0 : Integer.parseInt(plugin.getConfig().getString("arenas." + name + ".coordinate").split(",")[1]));
        this.secondX = ((plugin.getConfig().getString("arenas." + name + ".coordinate") == null) ? 0 : Integer.parseInt(plugin.getConfig().getString("arenas." + name + ".coordinate").split(",")[2]));
        this.secondZ = ((plugin.getConfig().getString("arenas." + name + ".coordinate") == null) ? 0 : Integer.parseInt(plugin.getConfig().getString("arenas." + name + ".coordinate").split(",")[3]));
        this.wave = 0;
        this.timeLeft = 0;
        this.timePlayed = 0;
        this.waitingSeconds = 0;
        this.waitingID = 0;
        this.matchID = 0;
        this.scoreboardID = 0;
        plugin.arenas.put(name, this);
    }

    public void save() {
        this.plugin.getConfig().set("arenas." + this.name + ".survivor-spawn", (Object) Utils.locationToString(this.survivorSpawn));
        this.plugin.getConfig().set("arenas." + this.name + ".vampire-spawn", (Object) Utils.locationToString(this.vampireSpawn));
        this.plugin.getConfig().set("arenas." + this.name + ".lobby-spawn", (Object) Utils.locationToString(this.lobbySpawn));
        this.plugin.getConfig().set("arenas." + this.name + ".zombie-spawns", (Object) Utils.locationToStringList(this.zombieSpawns));
        this.plugin.getConfig().set("arenas." + this.name + ".coordinate", (Object) (this.firstX + "," + this.firstZ + "," + this.secondX + "," + this.secondZ));
        final List<String> enabledArenas = (List<String>) this.plugin.getConfig().getStringList("arenas.enabled-arenas");
        if (!enabledArenas.contains(this.name)) {
            enabledArenas.add(this.name);
            this.plugin.getConfig().set("arenas.enabled-arenas", (Object) enabledArenas);
        }
        this.plugin.saveConfig();
    }

    public void reset() {
        Bukkit.getScheduler().cancelTask(this.waitingID);
        Bukkit.getScheduler().cancelTask(this.matchID);
        Bukkit.getScheduler().cancelTask(this.scoreboardID);
        this.status = "waiting";
        this.gamers = new CopyOnWriteArrayList<Gamer>();
        for (final Entity entity : this.zombies) {
            entity.remove();
        }
        this.zombies = new CopyOnWriteArrayList<Entity>();
        this.survivorSpawn = Utils.stringToLocation(this.plugin.getConfig().getString("arenas." + this.name + ".survivor-spawn"));
        this.vampireSpawn = Utils.stringToLocation(this.plugin.getConfig().getString("arenas." + this.name + ".vampire-spawn"));
        this.lobbySpawn = Utils.stringToLocation(this.plugin.getConfig().getString("arenas." + this.name + ".lobby-spawn"));
        this.zombieSpawns = Utils.stringListToLocation(this.plugin.getConfig().getStringList("arenas." + this.name + ".zombie-spawns"));
        this.firstX = ((this.plugin.getConfig().getString("arenas." + this.name + ".coordinate") == null) ? 0 : Integer.parseInt(this.plugin.getConfig().getString("arenas." + this.name + ".coordinate").split(",")[0]));
        this.firstZ = ((this.plugin.getConfig().getString("arenas." + this.name + ".coordinate") == null) ? 0 : Integer.parseInt(this.plugin.getConfig().getString("arenas." + this.name + ".coordinate").split(",")[1]));
        this.secondX = ((this.plugin.getConfig().getString("arenas." + this.name + ".coordinate") == null) ? 0 : Integer.parseInt(this.plugin.getConfig().getString("arenas." + this.name + ".coordinate").split(",")[2]));
        this.secondZ = ((this.plugin.getConfig().getString("arenas." + this.name + ".coordinate") == null) ? 0 : Integer.parseInt(this.plugin.getConfig().getString("arenas." + this.name + ".coordinate").split(",")[3]));
        this.wave = 0;
        this.timeLeft = 0;
        this.timePlayed = 0;
        this.waitingSeconds = 0;
        this.waitingID = 0;
        this.matchID = 0;
        this.scoreboardID = 0;
    }

    public void startTimer() {
        for (final Gamer gamer : this.gamers) {
            gamer.getPlayer().setScoreboard(gamer.board);
        }

        this.matchID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {


            for (final Gamer gamer : this.gamers) {
                if (gamer.getPlayer() == null) return;

                String waveString;
                if (Arena.this.wave == 0) {
                    waveString = ChatColor.GOLD + " " + plugin.message.get().get("get-ready") + "";
                } else if (Arena.this.wave < 20) {
                    waveString = ChatColor.GOLD + " " + plugin.message.get().get("wave") + " ¹" + Utils.getRomanNumeral(Arena.this.wave);
                } else {
                    waveString = ChatColor.GOLD + " " + plugin.message.get().get("last-wave");
                }

                gamer.resetScoreboard();
                gamer.getPlayer().setScoreboard(gamer.board);
                gamer.obj.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + Utils.getFormattedTime(Arena.this.timePlayed) + waveString + " " + Utils.getFormattedTime(Arena.this.timeLeft));

                gamer.obj.getScore(ChatColor.RED + "").setScore(10);
                gamer.obj.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("survivors") + ": " + Arena.this.getSurvivors().size()).setScore(9);

                gamer.obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + plugin.message.get().get("vampires") + ": " + Arena.this.getVampires().size()).setScore(8);

                gamer.obj.getScore(ChatColor.RED + "").setScore(7);

                gamer.obj.getScore(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + plugin.message.get().get("zombies") + ": " + Arena.this.zombies.size()).setScore(6);

                gamer.obj.getScore(ChatColor.RED + "").setScore(5);

                gamer.obj.getScore(gamer.alive ? (ChatColor.GOLD + "" + ChatColor.BOLD + plugin.message.get().get("gold-3") + ": " + gamer.cash) : (ChatColor.DARK_RED + "" + plugin.message.get().get("blood-3") + ": " + gamer.cash)).setScore(4);
                if (!gamer.alive) {
                    final ItemStack blood = new ItemStack(Material.REDSTONE, 1);
                    final ItemMeta meta = blood.getItemMeta();
                    meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + gamer.cash + " " + plugin.message.get().get("blood-3"));
                    blood.setItemMeta(meta);
                    gamer.getPlayer().getInventory().setItem(8, blood);
                } else {
                    final ItemStack gold = new ItemStack(Material.GOLD_NUGGET, 1);
                    final ItemMeta meta = gold.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + gamer.cash + " " + plugin.message.get().get("gold-3"));
                    gold.setItemMeta(meta);
                    gamer.getPlayer().getInventory().setItem(8, gold);
                }
            }

            ++timePlayed;
            --timeLeft;
            if (Arena.this.timeLeft == 0) {
                Game.next(Objects.requireNonNull(Utils.getArena(Arena.this.name)));
            }
        }, 0L, 20L);
    }

    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(this.matchID);
    }

    public List<Gamer> getSurvivors() {
        final List<Gamer> survivors = new ArrayList<>();
        for (final Gamer gamer : this.gamers) {
            if (gamer.alive) {
                survivors.add(gamer);
            }
        }
        return survivors;
    }

    public List<Gamer> getVampires() {
        final List<Gamer> vampires = new ArrayList<>();
        for (final Gamer gamer : this.gamers) {
            if (!gamer.alive) {
                vampires.add(gamer);
            }
        }
        return vampires;
    }

    public String getWinningTeam() {
        if (this.getSurvivors().size() == 0) {
            return plugin.message.get().get("vampires").toString();
        }
        return plugin.message.get().get("survivors").toString();
    }

    public void broadcast(final String message) {
        for (final Gamer gamer : this.gamers) {
            gamer.sendMessage(message);
        }
    }

    public void broadcastOthers(final String message, final Gamer except) {
        for (final Gamer gamer : this.gamers) {
            if (gamer != except) {
                gamer.sendMessage(message);
            }
        }
    }
}
