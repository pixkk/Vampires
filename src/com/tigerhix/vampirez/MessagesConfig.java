package com.tigerhix.vampirez;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MessagesConfig {
	   
    public FileConfiguration messagesconf;
    public File messagesfile;
    public void setup(final Plugin plugin) {
    	messagesfile = new File(plugin.getDataFolder(), "messages.yml");
    	if (!messagesfile.exists()) {
    		try {
				messagesfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	messagesconf = YamlConfiguration.loadConfiguration(messagesfile);
    	
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
