package com.tigerhix.vampirez;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MessagesConfig {
	   
    public FileConfiguration messagesconf;
    public File messagesfile;

	public boolean disable = false;

    public void setup(final Plugin plugin) {
    	messagesfile = new File(plugin.getDataFolder(), "messages.yml");
    	if (!messagesfile.exists()) {
    		try {
				messagesfile.createNewFile();
				Bukkit.getConsoleSender().sendMessage("§c------------------------------------------------------------------------------------------------------- \n");
				Bukkit.getConsoleSender().sendMessage("§cFile \"messages.yml\" is empty. Please, copy language file from https://github.com/pixkk/Vampires#languages \n");
				Bukkit.getConsoleSender().sendMessage("§c------------------------------------------------------------------------------------------------------- \n");
				Listeners.plugin.getServer().getPluginManager().disablePlugin(Listeners.plugin);
				disable = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		else {
			if (messagesfile.length() == 0) {
				Bukkit.getConsoleSender().sendMessage("§c------------------------------------------------------------------------------------------------------- \n");
				Bukkit.getConsoleSender().sendMessage("§cFile \"messages.yml\" is empty. Please, copy language file from https://github.com/pixkk/Vampires#languages \n");
				Bukkit.getConsoleSender().sendMessage("§c------------------------------------------------------------------------------------------------------- \n");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				disable = true;
			}
			messagesconf = YamlConfiguration.loadConfiguration(messagesfile);
		}
    	

    	
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
    }
}
