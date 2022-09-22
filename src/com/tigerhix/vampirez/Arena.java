package com.tigerhix.vampirez;

import org.bukkit.entity.*;
import java.util.concurrent.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.*;
import org.bukkit.scoreboard.*;

import java.util.*;

import static com.tigerhix.vampirez.Game.namearena;

public class Arena
{
    public Main plugin;
    public String name;
    public String status;
    public List<Gamer> gamers;
    public List<Entity> zombies;
    public Location survivorSpawn;
    public Location vampireSpawn;
    public List<Location> zombieSpawns;
    public Location lobbySpawn;
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
        this.plugin.getConfig().set("arenas." + this.name + ".survivor-spawn", (Object)Utils.locationToString(this.survivorSpawn));
        this.plugin.getConfig().set("arenas." + this.name + ".vampire-spawn", (Object)Utils.locationToString(this.vampireSpawn));
        this.plugin.getConfig().set("arenas." + this.name + ".lobby-spawn", (Object)Utils.locationToString(this.lobbySpawn));
        this.plugin.getConfig().set("arenas." + this.name + ".zombie-spawns", (Object)Utils.locationToStringList(this.zombieSpawns));
        this.plugin.getConfig().set("arenas." + this.name + ".coordinate", (Object)(this.firstX + "," + this.firstZ + "," + this.secondX + "," + this.secondZ));
        final List<String> enabledArenas = (List<String>)this.plugin.getConfig().getStringList("arenas.enabled-arenas");
        if (!enabledArenas.contains(this.name)) {
            enabledArenas.add(this.name);
            this.plugin.getConfig().set("arenas.enabled-arenas", (Object)enabledArenas);
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
    
    @SuppressWarnings("deprecation")
	public void startTimer() {
        for (final Gamer gamer : this.gamers) {
            gamer.getPlayer().setScoreboard(gamer.board);
//            gamer.obj.getScore(this.plugin.getServer().getOfflinePlayer("Зомби")).setScore(1);
//            gamer.obj.getScore(this.plugin.getServer().getOfflinePlayer("Зомби")).setScore(0);
        }
        this.matchID = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {


                for (final Gamer gamer : Arena.this.gamers) {
                    String waveString;
                    if (Arena.this.wave == 0) {
                        waveString = ChatColor.GOLD + " " +plugin.message.get().get("get-ready")+"";
                    }
                    else if (Arena.this.wave < 20) {
                        waveString = ChatColor.GOLD + " "+ plugin.message.get().get("wave")+" №" + Utils.getRomanNumeral(Arena.this.wave);
                    }
                    else {
                        waveString = ChatColor.GOLD + " "+ plugin.message.get().get("last-wave");
                    }
                    gamer.resetScoreboard();
                    gamer.getPlayer().setScoreboard(gamer.board);
                    gamer.obj.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + Utils.getFormattedTime(Arena.this.timePlayed) + waveString + " " + Utils.getFormattedTime(Arena.this.timeLeft));

                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(ChatColor.RED + "" )).setScore(10);

                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("survivors")+": "+Arena.this.getSurvivors().size())).setScore(9);
                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(ChatColor.RED + "" + ChatColor.BOLD + plugin.message.get().get("vampires")+": "+ Arena.this.getVampires().size())).setScore(8);

                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(ChatColor.RED + "" )).setScore(7);

                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + plugin.message.get().get("zombies")+": "+Arena.this.zombies.size())).setScore(6);

                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(ChatColor.RED + "" )).setScore(5);

                    gamer.obj.getScore(Arena.this.plugin.getServer().getOfflinePlayer(gamer.alive ? (ChatColor.GOLD +"" + ChatColor.BOLD + plugin.message.get().get("gold-3")+": "+ gamer.cash) : (ChatColor.DARK_RED +""+ plugin.message.get().get("blood-3")+": "+ gamer.cash))).setScore(4);
                    if (!gamer.alive) {
                        final ItemStack blood = new ItemStack(Material.REDSTONE, 1);
                        final ItemMeta meta = blood.getItemMeta();
                        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + gamer.cash + " " + plugin.message.get().get("blood-3"));
                        blood.setItemMeta(meta);
                        gamer.getPlayer().getInventory().setItem(8, blood);
                    }
                    else {
                        final ItemStack gold = new ItemStack(Material.GOLD_NUGGET, 1);
                        final ItemMeta meta = gold.getItemMeta();
                        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + gamer.cash + " " + plugin.message.get().get("gold-3"));
                        gold.setItemMeta(meta);
                        gamer.getPlayer().getInventory().setItem(8, gold);
                    }
                }
                final Arena this$0 = Arena.this;
                ++this$0.timePlayed;
                final Arena this$2 = Arena.this;
                --this$2.timeLeft;
                if (Arena.this.timeLeft == 0) {
                    Game.next(Objects.requireNonNull(Utils.getArena(Arena.this.name)));
                }
            }
        }, 0L, 20L);
    }
    
    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(this.matchID);
    }
    
    public List<Gamer> getSurvivors() {
        final List<Gamer> survivors = new ArrayList<Gamer>();
        for (final Gamer gamer : this.gamers) {
            if (gamer.alive) {
                survivors.add(gamer);
            }
        }
        return survivors;
    }
    
    public List<Gamer> getVampires() {
        final List<Gamer> vampires = new ArrayList<Gamer>();
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
