package com.tigerhix.vampirez;

import org.bukkit.block.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class Sign implements Listener
{
    public Main plugin, plugin2;
    public Arena arena;
    public Block block;
    public org.bukkit.block.Sign sign;
    public int ID;
    
    public Sign(Main plugin, final Arena arena, final Block block) {
        if (arena == null || block == null || block.getState() == null) {
            this.save(true);
            return;
        }
        plugin2 = plugin;
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
        this.arena = arena;
        
        final List<String> enabledArenas1 = (List<String>)this.plugin.getConfig().getStringList("arenas.enabled-arenas");
        if (!enabledArenas1.contains(this.arena.name)) {
        	Utils.getArena(this.arena.name).status = "disabled";
        }else {
        	//Bukkit.getConsoleSender().sendMessage("§6" + this.arena.name);	
        }

        this.block = block;
        BlockState state = block.getState();
        if (state instanceof org.bukkit.block.Sign) {
            this.sign = (org.bukkit.block.Sign) state;
        }
        this.ID = 0;
    }
    
    public void save(final Boolean remove) {
        final List<String> signs = (List<String>)Listeners.plugin.getConfig().getStringList("signs");
        if (!remove) {
            signs.add(Utils.locationToString(this.block.getLocation()) + "@" + this.arena.name);
        }
        else {
            signs.remove(Utils.locationToString(this.block.getLocation()) + "@" + this.arena.name);
        }
        Listeners.plugin.getConfig().set("signs", (Object)signs);
       //- Vampirezlobboy,-872.0,95.0,-856.0,0.0,0.0@Village
        Listeners.plugin.saveConfig();
    }
    
    public void startTimer() {
       if (this.arena == null || this.block == null || this.sign == null) {
            return;
        }
    	
    	
    	
        this.ID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (Config.signStyle == 1) {
                   Sign.this.sign.setLine(1, Sign.this.arena.status.equals("waiting") ? Config.waitingText : Config.startedText);
                    Sign.this.sign.setLine(2, Sign.this.arena.gamers.size() + "/" + Config.maxPlayer);
                    sign.update();
                }
                else  {
                	Sign.this.sign.setLine(0, ChatColor.GREEN + "[VampireZ]");
                	Sign.this.sign.setLine(1, Sign.this.arena.status.equals("waiting") ? (ChatColor.DARK_GREEN + Config.roomText.replace("%id%", Sign.this.arena.name)) : (ChatColor.DARK_RED + Config.roomText.replace("%id%", Sign.this.arena.name)));
                   
                	String reasonsign = "";
                	if(Sign.this.arena.status.equals("waiting")) {
                		 reasonsign = ChatColor.DARK_GREEN + Config.waitingText;
                	}else if (Sign.this.arena.status.equals("started")) {
                		 reasonsign = ChatColor.DARK_RED + Config.startedText;
                	}
                	else {
                		 reasonsign = ChatColor.DARK_RED + "" + plugin.message.get().get("disabled");
                	}
                	Sign.this.sign.setLine(2, reasonsign);
                    Sign.this.sign.setLine(3, Sign.this.arena.status.equals("waiting") ? (ChatColor.GOLD + "" + Sign.this.arena.gamers.size() + "/" + Config.maxPlayer) : (ChatColor.DARK_RED + "" + Sign.this.arena.gamers.size() + "/" + Config.maxPlayer));
                    sign.update();
                }
            }
        }, 0L, 20L);
    }
    
    public void stopTimer() {
        this.plugin.getServer().getScheduler().cancelTask(this.ID);
        this.ID = -1;
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent evt) {
        if (this.ID == -1) {
            return;
        }
        if (evt.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (evt.getClickedBlock().getType() != Material.OAK_SIGN && evt.getClickedBlock().getType() != Material.OAK_WALL_SIGN) {
            return;
        }
        if (evt.getClickedBlock().getLocation().getBlockX() != this.block.getLocation().getBlockX() || evt.getClickedBlock().getLocation().getBlockY() != this.block.getLocation().getBlockY() || evt.getClickedBlock().getLocation().getBlockZ() != this.block.getLocation().getBlockZ()) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing != null) {
            evt.getPlayer().sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("already-in-game"));
            return;
        }
        if (this.arena.status.equals("started")) {
            evt.getPlayer().sendMessage(ChatColor.GREEN+"[VampireZ] " + ChatColor.RED + plugin.message.get().get("game-started"));
            return;
        }
        if (this.arena.status.equals("disabled")) {
            //evt.getPlayer().sendMessage(ChatColor.GREEN+"[VampireZ] " + ChatColor.RED + "Арена отключена!");
            return;
        }
        if (this.arena.gamers.size() == Config.maxPlayer) {
            evt.getPlayer().sendMessage(ChatColor.GREEN+"[VampireZ] " +ChatColor.RED + plugin.message.get().get("arena-is-full"));
            return;
        }
        try {
            Game.join(Objects.requireNonNull(Utils.getArena(String.valueOf(this.arena))), Utils.getGamer(evt.getPlayer()));
//            Game.join(this.arena, Utils.getGamer(evt.getPlayer()));
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        }
    }
}
