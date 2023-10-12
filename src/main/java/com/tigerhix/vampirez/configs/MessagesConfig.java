package com.tigerhix.vampirez.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {
	   
    public FileConfiguration messagesconf;
    public File messagesfile;

	public boolean disabled = false;

	private void addMessages(FileConfiguration messagesconf) {
		messagesconf.addDefault("available-commands", "Available commands");
		messagesconf.addDefault("join-command", "Join the game");
		messagesconf.addDefault("leave-command", "Leave current game");
		messagesconf.addDefault("lobby-command", "Teleport to the lobby");
		messagesconf.addDefault("help-command", "Show this page");
		messagesconf.addDefault("list-of-arenas", "List of available arenas");
		messagesconf.addDefault("invalid-arguments", "Invalid arguments. Use command");
		messagesconf.addDefault("arena-not-exist", "Arena does not exist!");
		messagesconf.addDefault("arena-exist", "This arena already exists!");
		messagesconf.addDefault("arena-created", "The arena has been successfully created! Now set the spawn point for survivors (\"/vampire setsurvivor.\")");
		messagesconf.addDefault("survivor-spawn-created", "The spawn location of the survivors is set. Now set a spawn point of vampires (\"/vampire setvampire\")");
		messagesconf.addDefault("prepare-created", "Waiting room set up! The arena has been completed.");
		messagesconf.addDefault("vampire-spawn-created", "Vampire spawn location is set. Now set a spawn point of zombies (\"/vampire setvampire\")");
		messagesconf.addDefault("zombie-spawn-in-progress", "Zombie spawn location is set. Enter the command again to install next point. The minimum number of points is 5.");
		messagesconf.addDefault("zombie-spawn-created", "Zombie spawn locations are set. You can continue to install more points. If you are ready, type /vampire setprepare for install a spawn point of prepare.");
		messagesconf.addDefault("zombie-spawn-not-enough", "Not enough zombie spawn points. To install additional");
		messagesconf.addDefault("need-create-arena", "First, create an arena /vampire create [target].");
		messagesconf.addDefault("need-survivor-spawn", "First, set the survivor spawn location: \"/vampire setsurvivor.\"");
		messagesconf.addDefault("need-vampire-spawn", "First, set the vampire spawn location: \"/vampire setvampire.\"");
		messagesconf.addDefault("already-in-game", "You are already in the game!");
		messagesconf.addDefault("in-game", "You are in the game!");
		messagesconf.addDefault("game-started", "The game has already started!");
		messagesconf.addDefault("full-arena", "The arena is full!");
		messagesconf.addDefault("not-in-game", "You are not in the game!");
		messagesconf.addDefault("no-leave", "You can't leave the game now!");
		messagesconf.addDefault("too-many-arguments", "Too many arguments! Use command");
		messagesconf.addDefault("lobby-created", "Lobby successfully installed!");
		messagesconf.addDefault("plugin-reloaded", "Plugin settings reloaded!");
		messagesconf.addDefault("unknown-command", "Unknown command. List of all commands: \"/vampire help\"");
		messagesconf.addDefault("gold-1", "gold!");
		messagesconf.addDefault("gold-2", "Gold!");
		messagesconf.addDefault("gold-3", "Gold");
		messagesconf.addDefault("blood-1", "blood!");
		messagesconf.addDefault("blood-2", "Blood!");
		messagesconf.addDefault("blood-3", "Blood");
		messagesconf.addDefault("blood-4", "blood");
		messagesconf.addDefault("get-ready", "Get ready");
		messagesconf.addDefault("wave", "Wave");
		messagesconf.addDefault("survivors", "Survivors");
		messagesconf.addDefault("survivors-2", "survivors");
		messagesconf.addDefault("vampires", "Vampires");
		messagesconf.addDefault("zombies", "Zombie");
		messagesconf.addDefault("sign-waiting", "Expectation");
		messagesconf.addDefault("sign-started", "game in progress");
		messagesconf.addDefault("map-name", "Map");
		messagesconf.addDefault("players", "Players");
		messagesconf.addDefault("player", "Player");
		messagesconf.addDefault("joined", "joined the game.");
		messagesconf.addDefault("you-are-joined", "You have joined the game.");
		messagesconf.addDefault("was-kicked", "was excluded from the game.");
		messagesconf.addDefault("left-the-game", "left the game.");
		messagesconf.addDefault("you-left-the-game", "You have left the game.");
		messagesconf.addDefault("arena-reset", "The arena has been reset.");
		messagesconf.addDefault("the-game-will-start-soon", "The game will start soon!");
		messagesconf.addDefault("game-will-start-in", "The game will start in");
		messagesconf.addDefault("seconds", "sec.");
		messagesconf.addDefault("you-are-vampire", "TASTE THEIR BLOOD");
		messagesconf.addDefault("became-vampire", "YOU BECAME A VAMPIRE");
		messagesconf.addDefault("you-are-alive", "SURVIVE AT ANY COST");
		messagesconf.addDefault("first-wave", "The first wave will start in 30 seconds, get ready.");
		messagesconf.addDefault("before-the-next-wave", "before the next wave.");
		messagesconf.addDefault("last-wave", "Last wave. Hold on for more than 2 minutes...");
		messagesconf.addDefault("game-over", "The game is over!");
		messagesconf.addDefault("won", "won!");
		messagesconf.addDefault("cost", "Price");
		messagesconf.addDefault("vampire-fang", "Vampire Fang");
		messagesconf.addDefault("original-vampire-fang", "original vampire scream");
		messagesconf.addDefault("head", "Head");
		messagesconf.addDefault("clothes", "clothing");
		messagesconf.addDefault("wooden-sword", "wooden sword");
		messagesconf.addDefault("stone-sword", "stone sword");
		messagesconf.addDefault("iron-sword", "an iron sword");
		messagesconf.addDefault("lava-sword", "lava sword");
		messagesconf.addDefault("pumice-sword", "lava sword");
		messagesconf.addDefault("steel-spring", "steel spring");
		messagesconf.addDefault("plasma-cutter", "Plasma cutter");
		messagesconf.addDefault("inflatable-bar", "inflatable bar");
		messagesconf.addDefault("piercing-shard", "Piercing Shard");
		messagesconf.addDefault("stone-shard", "stone shard");
		messagesconf.addDefault("hardened-steel", "Hardened steel");
		messagesconf.addDefault("tempered-piercing-shard", "Tempered Piercing Shard");
		messagesconf.addDefault("tempered-stone-shard", "Hardened Stone Shard");
		messagesconf.addDefault("tempered-plasma-cutter", "Tempered Plasma Sword");
		messagesconf.addDefault("sword-of-god", "Sword of God");
		messagesconf.addDefault("leather-set", "Leather set");
		messagesconf.addDefault("iron-set", "iron kit");
		messagesconf.addDefault("tempered-leather-set", "Hardened Leather Set");
		messagesconf.addDefault("tempered-iron-set", "Hardened Iron Set");
		messagesconf.addDefault("spiked-leather-set", "Spiked Leather Set");
		messagesconf.addDefault("spiked-iron-set", "Spiked Iron Set");
		messagesconf.addDefault("good-set", "Good set");
		messagesconf.addDefault("student-snack", "Student snack");
		messagesconf.addDefault("tropical-resort", "tropical resort");
		messagesconf.addDefault("take-out", "take out");
		messagesconf.addDefault("pig-out", "pig out");
		messagesconf.addDefault("homemade-food", "homemade food");
		messagesconf.addDefault("food-of-the-gods", "food of the gods");
		messagesconf.addDefault("health-potion", "Health Potion");
		messagesconf.addDefault("health-regeneration", "Potion of Regeneration");
		messagesconf.addDefault("potion-of-the-god", "Potion of God");
		messagesconf.addDefault("hardened-leather-helm", "Hardened Leather Helm");
		messagesconf.addDefault("hardened-leather-chestplate", "Hardened Leather Chestplate");
		messagesconf.addDefault("hardened-leather-leggings", "Hardened Leather Leggings");
		messagesconf.addDefault("hardened-leather-boots", "Tempered Leather Boots");
		messagesconf.addDefault("tempered-iron-helm", "Wrought iron helmet");
		messagesconf.addDefault("tempered-iron-chestplate", "Wrought iron breastplate");
		messagesconf.addDefault("tempered-iron-leggings", "Wrought Iron Leggings");
		messagesconf.addDefault("tempered-iron-boots", "Wrought iron boots");
		messagesconf.addDefault("spiked-leather-helm", "Spiked Leather Helm");
		messagesconf.addDefault("spiked-leather-chestplate", "Spiked Leather Chestplate");
		messagesconf.addDefault("spiked-leather-leggings", "Spiked Leather Leggings");
		messagesconf.addDefault("spiked-leather-boots", "Studded leather boots");
		messagesconf.addDefault("spiked-iron-helm", "Spiked Iron Helm");
		messagesconf.addDefault("spiked-iron-chestplate", "Spiked Iron Breastplate");
		messagesconf.addDefault("spiked-iron-leggings", "Spiked Iron Leggings");
		messagesconf.addDefault("spiked-iron-boots", "Spiked Iron Boots");
		messagesconf.addDefault("diamond-helm", "God's Diamond Helmet");
		messagesconf.addDefault("diamond-chestplate", "Diamond Breastplate of God");
		messagesconf.addDefault("diamond-leggings", "Diamond Leggings of God");
		messagesconf.addDefault("diamond-boots", "Diamond Boots of God");
		messagesconf.addDefault("blazing-blade", "blazing blade");
		messagesconf.addDefault("leave-arena", "Leave the arena");
		messagesconf.addDefault("no-name-of-arena", "Arena name missing.");
		messagesconf.addDefault("sign-created", "Plate created");
		messagesconf.addDefault("sign-removed", "Decal removed");
		messagesconf.addDefault("id-dead", "killed by zombies or crashed");
		messagesconf.addDefault("gamers-left", "Left");
		messagesconf.addDefault("was-killed", "was killed");
		messagesconf.addDefault("impossible-use-command-now", "You can't use this command right now.");
		messagesconf.addDefault("enter-command", "Enter");
		messagesconf.addDefault("for-leave-arena", "to leave the game");
		messagesconf.addDefault("disabled", "Disabled...");
		messagesconf.addDefault("arena-is-full", "The arena is full!");
		messagesconf.addDefault("buy-in-shop-success", "The purchase was successful!");
		messagesconf.addDefault("shop", "Score");
		messagesconf.addDefault("not-enough", "Not enough");
		messagesconf.addDefault("for-purchase", "for purchase");
		messagesconf.addDefault("flaming-arrow", "flaming arrow");
		messagesconf.addDefault("flaming-arrow-desc", "Indicates nearest survivor");
		messagesconf.addDefault("vampire-regeneration", "Vampire regeneration");
		messagesconf.addDefault("vampire-regeneration-desc", "30 seconds of strong regeneration");
		messagesconf.addDefault("instant-health", "Instant Health");
		messagesconf.addDefault("instant-health-desc", "Fully replenish your health bar");
		messagesconf.addDefault("vampire-aura", "Vampire Aura");
		messagesconf.addDefault("vampire-aura-desc", "Super strength for 15 seconds");
		messagesconf.addDefault("vampire-jump", "Vampire Jump");
		messagesconf.addDefault("vampire-jump-desc", "Crazy jump. Lasts until next death");
		messagesconf.addDefault("health-boost", "Health Boost");
		messagesconf.addDefault("health-boost-desc", "Additional row of hearts. Lasts until next death");
		messagesconf.addDefault("not-sure-needed", "Not sure if you need it now");
		messagesconf.addDefault("follow-arrow", "follow the arrow");
		FileConfigurationOptions options = messagesconf.options();
		options.copyDefaults(true);
		options.header("Messages file");
		options.copyHeader(true);
		save();
	}
    public void setup(final Plugin plugin) {
    	messagesfile = new File(plugin.getDataFolder(),    "messages.yml");
		messagesconf = YamlConfiguration.loadConfiguration(messagesfile);
		this.addMessages(messagesconf);
	}
    public FileConfiguration get() {
    	return messagesconf;
    }
	
    public void save() {
    	try {
			messagesconf.save(messagesfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void reloadconf() {
    	messagesconf = YamlConfiguration.loadConfiguration(messagesfile);
		this.addMessages(messagesconf);
    }
}
