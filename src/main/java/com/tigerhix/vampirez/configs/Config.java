package com.tigerhix.vampirez.configs;

import com.tigerhix.vampirez.Main;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;

public class Config
{

    public static int minPlayer;
    public static int maxPlayer;
    public static int minSeconds;
    public static int maxSeconds;
    public static int signStyle;
    public static String waitingText;
    public static String startedText;
    public static String roomText;
    public static String language;
    public static Collection<PotionEffect> survivorEffects;
    public static Collection<PotionEffect> waveEffects;
    public static Collection<PotionEffect> vampireEffects;
    public static boolean punch;
    
    public Config(final Main plugin) {
        Config.minPlayer = plugin.getConfig().getInt("general.requirements.min");
        Config.maxPlayer = plugin.getConfig().getInt("general.requirements.max");
        if (Config.minPlayer < 2) {
            plugin.getConfig().set("general.requirements.min", 2);
            plugin.saveConfig();
        }
        if (Config.maxPlayer < 2) {
            plugin.getConfig().set("general.requirements.max", 16);
            plugin.saveConfig();
        }
        if (Config.minSeconds <0 || Config.maxSeconds <0) {
            plugin.getConfig().set("general.seconds.min", 60);
            plugin.getConfig().set("general.seconds.max", 15);
            plugin.saveConfig();
        }

        Config.minSeconds = plugin.getConfig().getInt("general.seconds.min");
        Config.maxSeconds = plugin.getConfig().getInt("general.seconds.max");
        Config.signStyle = 2;
        Config.waitingText = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "- "+plugin.message.get().get("sign-waiting") + " -";
        Config.startedText = ChatColor.DARK_RED + "" + ChatColor.BOLD + "- "+plugin.message.get().get("sign-started") + " -";
        Config.roomText = ChatColor.BOLD + "- %id% -";
        (Config.survivorEffects = new ArrayList<>()).add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        Config.survivorEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        Config.survivorEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
        (Config.waveEffects = new ArrayList<>()).add(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        Config.waveEffects.add(new PotionEffect(PotionEffectType.CONFUSION, 300, 0));
        (Config.vampireEffects = new ArrayList<>()).add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        Config.vampireEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        Config.vampireEffects.add(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        Config.punch = false;


        plugin.getConfig().addDefault("mysql.enabled", false);
        plugin.getConfig().addDefault("mysql.ip", "127.0.0.1");
        plugin.getConfig().addDefault("mysql.port", "3306");
        plugin.getConfig().addDefault("mysql.login", "login");
        plugin.getConfig().addDefault("mysql.password", "password");
        plugin.getConfig().addDefault("mysql.database", "db_name");
        plugin.saveConfig();
    }
}
