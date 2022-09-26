package com.tigerhix.vampirez;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;

import java.util.Objects;

public class Commands implements CommandExecutor
{
    private Main plugin;
    
    public Commands(final Main plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"[VampireZ]"+ ChatColor.GOLD+" The VampireZ plugin is developed by TigerHix. User Pixkk continues to support the plugin.");

                return true;
            }
//        	final String action = args[0];
            if(args.length >0){
                final String action = args[0];
                if (action.equalsIgnoreCase("reload")) {
                    this.plugin.getServer().getPluginManager().disablePlugin(plugin);
                    this.plugin.getServer().getPluginManager().enablePlugin(plugin);
                    Bukkit.getConsoleSender().sendMessage("§4§l[VampireZ] Plugin reloaded!");
                    return true;

                }
            }

            sender.sendMessage(ChatColor.GREEN+"[VampireZ]"+ ChatColor.RED+" Only players can use this command!");
            return true;
        }
        final Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.GREEN+"[VampireZ]"+ ChatColor.GOLD+" Plugin was developed by TigerHix,\nmodified by Pixkk.\n");
            return true;
        }
        if (args.length > 0) {
            final String action = args[0];
            if (action.equalsIgnoreCase("help")) {
                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "" + plugin.message.get().get("too-many-arguments") + ": /vampire help");
                    return true;
                }
                player.sendMessage(ChatColor.GREEN +"[VampireZ]"+ ChatColor.AQUA + " "+ plugin.message.get().get("available-commands") +":");
                player.sendMessage(ChatColor.GRAY + " /vampire join [name] - " + plugin.message.get().get("join-command") +".");
                player.sendMessage(ChatColor.GRAY + " /vampire leave - " + plugin.message.get().get("leave-command") +".");
                player.sendMessage(ChatColor.GRAY + " /vampire lobby - " + plugin.message.get().get("lobby-command") +".");
                player.sendMessage(ChatColor.GRAY + " /vampire help - "+ plugin.message.get().get("help-command") +".");
                if (Config.punch) {
                    //player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Punch" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "craft " + ChatColor.GOLD + "" + ChatColor.BOLD + "|" + ChatColor.AQUA + "" + ChatColor.BOLD + " Plugin by TigerHix");
                }
                return true;
            }
            else {
                if (action.equalsIgnoreCase("join")) {
                    if (args.length != 2) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + "" + plugin.message.get().get("invalid-arguments") + ": /vampire join [name]!");
                        return true;
                    }
                    final String target = args[1];
                    if (!this.plugin.arenas.containsKey(target)) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + "" + plugin.message.get().get("arena-not-exist"));
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).playing != null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + "" + plugin.message.get().get("already-in-game"));
                        return true;
                    }
                    if (!Objects.requireNonNull(Utils.getArena(target)).status.equals("waiting")) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + ""+ plugin.message.get().get("game-started"));
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getArena(target)).gamers.size() == Config.maxPlayer) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + ""+ plugin.message.get().get("full-arena"));
                        return true;
                    }
                    Game.join(Objects.requireNonNull(Utils.getArena(target)), Utils.getGamer(player));
                    return true;
                }
                else if (action.equalsIgnoreCase("leave")) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + "" + plugin.message.get().get("invalid-arguments") + ": /vampire leave!");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).playing == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("not-in-game"));
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).playing.status.equals("finished")) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("no-leave"));
                        return true;
                    }
                    Game.leave(Objects.requireNonNull(Utils.getGamer(player)), Reason.INITIATIVE);
                    return true;
                }
                else if (action.equalsIgnoreCase("lobby")) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED +"" + plugin.message.get().get("invalid-arguments") + ": /vampire leave!");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).playing != null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("in-game"));
                        return true;
                    }
                    player.teleport(Game.lobby);
                    return true;
                }
                else if (action.equalsIgnoreCase("create") && player.isOp()) {
                    if (args.length != 2) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED +"" + plugin.message.get().get("invalid-arguments") + ": /vampire create [target]");
                        return true;
                    }
                    final String target = args[1];
                    if (this.plugin.arenas.containsKey(target)) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED +"" + plugin.message.get().get("arena-exist"));
                        return true;
                    }
                    new Arena(this.plugin, target);
                    Objects.requireNonNull(Utils.getGamer(player)).selected = Utils.getArena(target);
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] "+plugin.message.get().get("arena-created")); //. Now go to survivor spawn of the arena and type /vampire setsurvivor
                    return true;
                }
                else if (action.equalsIgnoreCase("setsurvivor") && player.isOp()) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire setsurvivor");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("need-create-arena"));
                        return true;
                    }
                    Objects.requireNonNull(Utils.getGamer(player)).selected.survivorSpawn = player.getLocation();
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] "+plugin.message.get().get("survivor-spawn-created"));
                    return true;
                }
                else if (action.equalsIgnoreCase("setvampire") && player.isOp()) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire setvampire");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("need-create-arena"));
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.survivorSpawn == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("need-survivor-spawn"));
                        return true;
                    }
                    Objects.requireNonNull(Utils.getGamer(player)).selected.vampireSpawn = player.getLocation();
                    Objects.requireNonNull(Utils.getGamer(player)).selected.save();
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] "+plugin.message.get().get("vampire-spawn-created"));
                    return true;
                }
                else if (action.equalsIgnoreCase("setzombie") && player.isOp()) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire setzombie");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire create [target]");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.survivorSpawn == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("need-survivor-spawn"));
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.vampireSpawn == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("need-vampire-spawn"));
                        return true;
                    }
                    Objects.requireNonNull(Utils.getGamer(player)).selected.zombieSpawns.add(player.getLocation());
                    Objects.requireNonNull(Utils.getGamer(player)).selected.save();
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.zombieSpawns.size() < 5) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+plugin.message.get().get("zombie-spawn-in-progress"));
//                        player.sendMessage(ChatColor.GRAY + "* Zombie spawn set. You need to set at least 5. Type /vampire setzombie again to set another zombie spawn.");
                    }
                    else {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+plugin.message.get().get("zombie-spawn-created"));
//                        player.sendMessage(ChatColor.GRAY + "* Zombie spawn set. You have already set 5 or more spawns; you can continue to set more. Or, go to preparation room spawn and type /vampire setprepare to finish the setup wizard.");
                    }
                    return true;
                }
                else if (action.equalsIgnoreCase("setprepare") && player.isOp()) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire setprepare");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire create [target]");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.survivorSpawn == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire setsurvivor");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.vampireSpawn == null) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("invalid-arguments")+ ": /vampire setvampire");
                        return true;
                    }
                    if (Objects.requireNonNull(Utils.getGamer(player)).selected.zombieSpawns.size() < 5) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("zombie-spawn-not-enough"));
                        return true;
                    }
                    Objects.requireNonNull(Utils.getGamer(player)).selected.lobbySpawn = player.getLocation();
                    Objects.requireNonNull(Utils.getGamer(player)).selected.save();
                    Objects.requireNonNull(Utils.getGamer(player)).selected = null;
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ plugin.message.get().get("prepare-created"));
                    return true;
                }
                else if (action.equalsIgnoreCase("setlobby") && player.isOp()) {
                    if (args.length != 1) {
                        player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("too-many-arguments")+": /vampire setlobby");
                        return true;
                    }
                    Game.lobby = player.getLocation();
                    this.plugin.getConfig().set("general.lobby", (Object)Utils.locationToString(Game.lobby));
                    this.plugin.saveConfig();
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] " + plugin.message.get().get("lobby-created"));
                    return true;
                }
                else if (action.equalsIgnoreCase("reload") && player.isOp()) {
                   // this.plugin.saveConfig();
                    this.plugin.getServer().getPluginManager().disablePlugin(plugin);
                    this.plugin.getServer().getPluginManager().enablePlugin(plugin);
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] " + plugin.message.get().get("plugin-reloaded"));
//                    System.out.print("&4&l[VampireZ] Plugin reloaded!");
                    return true;
                }
                else {
                    player.sendMessage(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("unknown-command"));
                }
            }
        }
        return true;
    }
}
