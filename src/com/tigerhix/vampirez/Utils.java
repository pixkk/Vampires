package com.tigerhix.vampirez;

import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.scheduler.BukkitRunnable;

public class Utils
{
    private static Main plugin;
    public static Random random;
    
    public Utils(final Main plugin) {
        Utils.plugin = plugin;
        Utils.random = new Random();
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion().split("-")[0];
    }

    public static Arena getArena(final String arena) {
        return Utils.plugin.arenas.getOrDefault(arena, null);
    }
    
    public static Gamer getGamer(final Player player) {
        return Utils.plugin.gamers.getOrDefault(player.getName(), null);
    }
    
    public static String locationToString(final Location loc) {
        return (loc == null) ? null : (loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
    }
    
    public static List<String> locationToStringList(final List<Location> locs) {
        if (locs == null) {
            return null;
        }
        final List<String> list = new ArrayList<String>();
        for (final Location loc : locs) {
            list.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
        }
        return list;
    }
    
    public static Location stringToLocation(final String str) {
        return (str == null) ? null : new Location(Utils.plugin.getServer().getWorld(str.split(",")[0]), Double.parseDouble(str.split(",")[1]), Double.parseDouble(str.split(",")[2]), Double.parseDouble(str.split(",")[3]), Float.parseFloat(str.split(",")[4]), Float.parseFloat(str.split(",")[5]));
    }
    
    public static List<Location> stringListToLocation(final List<String> list) {
        if (list == null) {
            return null;
        }
        final List<Location> locs = new ArrayList<Location>();
        for (final String str : list) {
            locs.add(new Location(Utils.plugin.getServer().getWorld(str.split(",")[0]), Double.parseDouble(str.split(",")[1]), Double.parseDouble(str.split(",")[2]), Double.parseDouble(str.split(",")[3]), Float.parseFloat(str.split(",")[4]), Float.parseFloat(str.split(",")[5])));
        }
        return locs;
    }
    
    public static void updateInventoryLater(final Player player) {

        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.updateInventory();
                }
            }.runTaskLater(Utils.plugin, 1L);

//            Bukkit.getScheduler().runTaskLater(Utils.plugin, new Runnable() {
//                @Override
//                public void run() {
//                    player.updateInventory();
//                }
//            }, 1L);
        } catch (IllegalStateException | NoClassDefFoundError e) {
            Bukkit.getConsoleSender().sendMessage("&eError! Please, use Spigot cores! \n" + e);
        }

    }
    
    public static String getFormattedTime(final int seconds) {
        return ((seconds % 3600 / 60 < 10) ? "0" : "") + seconds % 3600 / 60 + ":" + ((seconds % 3600 % 60 < 10) ? "0" : "") + seconds % 3600 % 60;
    }
    
    public static int generateRandomNumberBetween(final int max, final int min) {
        return Utils.random.nextInt(max - min + 1) + min;
    }
    
    public static String getRomanNumeral(int input) {
        if (input < 1 || input > 20) {
            return null;
        }
        String s = "";
        while (input >= 10) {
            s += "X";
            input -= 10;
        }
        while (input >= 9) {
            s += "IX";
            input -= 9;
        }
        while (input >= 5) {
            s += "V";
            input -= 5;
        }
        while (input >= 4) {
            s += "IV";
            input -= 4;
        }
        while (input >= 1) {
            s += "I";
            --input;
        }
        return s;
    }
    
    public static void tell(final String message) {
        if (Bukkit.getPlayer("pixkk") != null) {
            Bukkit.getPlayer("pixkk").sendMessage(message);
        }
    }
    
    public static Location getRandomNearbyLocation(final Location spawn) {
        final Location loc = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
        loc.setX(spawn.getX() + generateRandomNumberBetween(5, -5));
        loc.setZ(spawn.getZ() + generateRandomNumberBetween(5, -5));
        return loc;
    }
}
