package com.tigerhix.vampirez;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item
{
    private ItemStack item;
    private Map<Enchantment, Integer> enchants;
    private String name;
    private List<String> lore;
    private int cost;
    
    public Item(final ItemStack item, final Map<Enchantment, Integer> enchants, final String name, final String lore, final int cost) {
        final List<String> list = new ArrayList<String>();
        list.add(ChatColor.DARK_GRAY + lore);
        this.item = item;
        this.enchants = ((enchants == null) ? new HashMap<Enchantment, Integer>() : enchants);
        this.name = name;
        this.lore = ((lore == null) ? new ArrayList<String>() : list);
        if(cost > 0) {
            this.cost = cost;
        }
        this.init();
    }
    
    private void init() {
        this.item.addUnsafeEnchantments((Map<Enchantment, Integer>)this.enchants);
        if (this.cost > 0) {
            this.lore.add(""+Game.plugin.message.get().get("cost")+": " + this.cost);
        }
        final ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(this.name);
        meta.setLore((List<String>)this.lore);
        this.item.setItemMeta(meta);
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public ItemStack getFresh() {
        final ItemMeta meta = this.item.getItemMeta();
        meta.setLore((List<String>)null);
        this.item.setItemMeta(meta);
        return this.item;
    }
    
    public int getCost() {
        return this.cost;
    }
}
