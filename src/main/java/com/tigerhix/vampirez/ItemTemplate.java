package com.tigerhix.vampirez;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

import static com.tigerhix.vampirez.Game.plugin;

public class ItemTemplate
{
    public static Item VAMPIRE_FANG;
    public static Item ORIGINAL_VAMPIRE_FANG;
    public static Item VAMPIRE_HEAD;
    public static Item VAMPIRE_CLOTH;
    public static Item WOOD_SWORD;
    public static Item SLIME_BALL;
    
    public Map<Enchantment, Integer> enchants;
    
    @SuppressWarnings("deprecation")
	public ItemTemplate() {
        (this.enchants = new HashMap<Enchantment, Integer>()).put(Enchantment.DAMAGE_ALL, 1);
        ItemTemplate.VAMPIRE_FANG = new Item(new ItemStack(Material.GHAST_TEAR), this.enchants, ChatColor.BOLD + "" + plugin.message.get().get("vampire-fang"), null, 0);
        this.enchants.put(Enchantment.DAMAGE_ALL, 2);
        ItemTemplate.ORIGINAL_VAMPIRE_FANG = new Item(new ItemStack(Material.GHAST_TEAR), this.enchants, ChatColor.BOLD + "" + plugin.message.get().get("original-vampire-fang"), null, 0);
        this.enchants.clear();
        ItemTemplate.VAMPIRE_HEAD = new Item(new ItemStack(Material.WITHER_SKELETON_SKULL, 1, (short)1), null, ChatColor.DARK_GRAY + "" + plugin.message.get().get("head")+"", null, 0);
        ItemTemplate.VAMPIRE_CLOTH = new Item(new ItemStack(Material.LEATHER_CHESTPLATE, 1), null, ChatColor.DARK_GRAY + "" + plugin.message.get().get("clothes")+"", null, 0);
        final LeatherArmorMeta armorMeta = (LeatherArmorMeta)ItemTemplate.VAMPIRE_CLOTH.getItem().getItemMeta();
        armorMeta.setColor(Color.fromRGB(0, 0, 0));
        ItemTemplate.VAMPIRE_CLOTH.getItem().setItemMeta((ItemMeta)armorMeta);
        ItemTemplate.WOOD_SWORD = new Item(new ItemStack(Material.WOODEN_SWORD), null, ChatColor.RED + ""+ plugin.message.get().get("wooden-sword"), null, 0);
        ItemTemplate.SLIME_BALL = new Item(new ItemStack(Material.SLIME_BALL), null, ChatColor.RED + "" + ChatColor.BOLD + plugin.message.get().get("leave-arena"), null, 0);
        
        
    }
}
