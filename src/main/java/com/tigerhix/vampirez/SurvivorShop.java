package com.tigerhix.vampirez;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurvivorShop implements Listener
{
    public Main plugin;
    public String name;
    public List<Item> items;
    public HashMap<Enchantment, Integer> enchants;
    
    @SuppressWarnings({ "deprecation" })
	public SurvivorShop(final Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.name = (String) plugin.message.get().get("shop");
        this.items = new ArrayList<Item>();
        this.enchants = new HashMap<Enchantment, Integer>();
        this.items.add(new Item(new ItemStack(Material.WOODEN_SWORD), null, "§c§l" +plugin.message.get().get("wooden-sword")+"", null, 3));
        this.items.add(new Item(new ItemStack(Material.STONE_SWORD), null, "§7§l"+plugin.message.get().get("stone-sword")+"", null, 10));
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), null, "§f§l"+plugin.message.get().get("iron-sword")+"", null, 20));
        this.enchants.put(Enchantment.FIRE_ASPECT, 1);
        this.items.add(new Item(new ItemStack(Material.WOODEN_SWORD), this.enchants, "§c§l"+plugin.message.get().get("blazing-blade")+"", null, 30));
        this.items.add(new Item(new ItemStack(Material.STONE_SWORD), this.enchants, "§7§l"+plugin.message.get().get("lava-sword")+"", null, 50));
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), this.enchants, "§f§l"+plugin.message.get().get("plasma-cutter")+"", null, 100));
        this.enchants.clear();
        this.enchants.put(Enchantment.KNOCKBACK, 1);
        this.items.add(new Item(new ItemStack(Material.WOODEN_SWORD), this.enchants, "§c§l"+plugin.message.get().get("inflatable-bar")+"", null, 30));
        this.items.add(new Item(new ItemStack(Material.STONE_SWORD), this.enchants, "§7§l"+plugin.message.get().get("pumice-sword")+"", null, 50));
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), this.enchants, "§f§l"+plugin.message.get().get("steel-spring")+"", null, 100));
        this.enchants.clear();
        this.enchants.put(Enchantment.DAMAGE_ALL, 1);
        this.items.add(new Item(new ItemStack(Material.WOODEN_SWORD), this.enchants, "§c§l"+plugin.message.get().get("piercing-shard")+"", null, 30));
        this.items.add(new Item(new ItemStack(Material.STONE_SWORD), this.enchants, "§7§l"+plugin.message.get().get("stone-shard")+"", null, 50));
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), this.enchants, "§f§l"+plugin.message.get().get("hardened-steel")+"", null, 100));
        this.enchants.put(Enchantment.FIRE_ASPECT, 1);
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), this.enchants, "§f§l"+plugin.message.get().get("tempered-piercing-shard")+"", null, 200));
        this.enchants.remove(Enchantment.FIRE_ASPECT);
        this.enchants.put(Enchantment.KNOCKBACK, 1);
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), this.enchants, "§f§l"+plugin.message.get().get("tempered-stone-shard")+"", null, 200));
        this.enchants.remove(Enchantment.DAMAGE_ALL);
        this.enchants.put(Enchantment.FIRE_ASPECT, 1);
        this.items.add(new Item(new ItemStack(Material.IRON_SWORD), this.enchants, "§f§l"+plugin.message.get().get("tempered-plasma-cutter")+"", null, 200));
        this.enchants.clear();
        this.enchants.put(Enchantment.DAMAGE_ALL, 1);
        this.enchants.put(Enchantment.KNOCKBACK, 1);
        this.enchants.put(Enchantment.FIRE_ASPECT, 1);
        this.items.add(new Item(new ItemStack(Material.DIAMOND_SWORD), this.enchants, "§b§l"+plugin.message.get().get("sword-of-god")+"", null, 500));
        this.items.add(new Item(new ItemStack(Material.LEATHER_HELMET), null, "§c§l"+plugin.message.get().get("leather-set")+"", null, 50));
        this.items.add(new Item(new ItemStack(Material.IRON_HELMET), null, "§f§l"+plugin.message.get().get("iron-set")+"", null, 100));
        this.enchants.clear();
        this.enchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.items.add(new Item(new ItemStack(Material.LEATHER_HELMET), this.enchants, "§c§l"+plugin.message.get().get("tempered-leather-set")+"", null, 175));
        this.items.add(new Item(new ItemStack(Material.IRON_HELMET), this.enchants, "§f§l"+plugin.message.get().get("tempered-iron-set")+"", null, 350));
        this.enchants.clear();
        this.enchants.put(Enchantment.THORNS, 1);
        this.items.add(new Item(new ItemStack(Material.LEATHER_HELMET), this.enchants, "§c§l"+plugin.message.get().get("spiked-leather-set")+"", null, 150));
        this.items.add(new Item(new ItemStack(Material.IRON_HELMET), this.enchants, "§f§l"+plugin.message.get().get("spiked-iron-set")+"", null, 350));
        this.enchants.clear();
        this.enchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.enchants.put(Enchantment.THORNS, 1);
        this.items.add(new Item(new ItemStack(Material.DIAMOND_HELMET), this.enchants, "§b§l"+plugin.message.get().get("good-set")+"", null, 2000));
        this.items.add(new Item(new ItemStack(Material.BAKED_POTATO), null, "§6§l"+plugin.message.get().get("student-snack")+"", null, 5));
        this.items.add(new Item(new ItemStack(Material.MELON), null, "§6§l"+plugin.message.get().get("tropical-resort")+"", null, 5));
        this.items.add(new Item(new ItemStack(Material.COOKED_CHICKEN), null, "§6§l"+plugin.message.get().get("take-out")+"", null, 10));
        this.items.add(new Item(new ItemStack(Material.COOKED_PORKCHOP), null, "§6§l"+plugin.message.get().get("pig-out")+"", null, 10));
        this.items.add(new Item(new ItemStack(Material.PUMPKIN_PIE), null, "§6§l"+plugin.message.get().get("homemade-food")+"", null, 10));
        this.items.add(new Item(new ItemStack(Material.GOLDEN_APPLE), null, "§6§l"+plugin.message.get().get("food-of-the-gods")+"", null, 100));
        this.items.add(new Item(new ItemStack(new Potion(PotionType.INSTANT_HEAL).toItemStack(2)), null, "§4§l"+plugin.message.get().get("health-potion")+"", null, 20));
        this.items.add(new Item(new ItemStack(new Potion(PotionType.REGEN).toItemStack(1)), null, "§4§l"+plugin.message.get().get("health-regeneration")+"", null, 50));
        this.items.add(new Item(new ItemStack(new Potion(PotionType.STRENGTH).toItemStack(1)), null, "§4§l"+plugin.message.get().get("potion-of-the-god")+"", null, 200));
    }
    
    public void open(final Player player) {
        final Inventory shopInventory = this.plugin.getServer().createInventory((InventoryHolder)player, 45, this.name);
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i) != null) {
                shopInventory.setItem(i, this.items.get(i).getItem());
            }
        }
        player.openInventory(shopInventory);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	@EventHandler
    public void onClickInventory(final InventoryClickEvent evt) {
        if (!evt.getView().getTitle().equalsIgnoreCase(this.name)) {
            return;
        }
        if (Utils.getGamer((Player)evt.getWhoClicked()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getWhoClicked()).playing == null) {
            return;
        }
        if (!Utils.getGamer((Player)evt.getWhoClicked()).alive) {
            return;
        }
        if (evt.getRawSlot() >= this.items.size()) {
            return;
        }
        evt.setCancelled(true);
        final Item target = this.items.get(evt.getRawSlot());
        if (target == null) {
            return;
        }
        final Gamer gamer = Utils.getGamer((Player)evt.getWhoClicked());
        final int cost = target.getCost();
        final int id = evt.getRawSlot();
        if (gamer.cash < cost) {
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    gamer.getPlayer().closeInventory();
                    gamer.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED +""+plugin.message.get().get("not-enough")+" " + (gamer.alive ? ""+plugin.message.get().get("gold-2")+"" : ""+plugin.message.get().get("blood-4")+"") + " "+plugin.message.get().get("for-purchase")+".");
                }
            }, 1L);
        }
        else {
            final Gamer gamer2 = gamer;
            gamer2.cash -= cost;
            if (id < 16 || id == 29 || id == 30) {
                final ItemStack given = target.getItem().clone();
                final ItemMeta meta = given.getItemMeta();
                meta.setLore((List)new ArrayList());
                given.setItemMeta(meta);
                gamer.getPlayer().getInventory().addItem(new ItemStack[] { given });
            }
            else {
                final HashMap<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
                if (id == 16) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS) });
                }
                else if (id == 17) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS) });
                }
                else if (id == 18) {
                    enchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new Item(new ItemStack(Material.LEATHER_HELMET), enchants, ""+plugin.message.get().get("hardened-leather-helm"), null, 0).getFresh(), new Item(new ItemStack(Material.LEATHER_CHESTPLATE), enchants, ""+plugin.message.get().get("hardened-leather-chestplate"), null, 0).getFresh(), new Item(new ItemStack(Material.LEATHER_LEGGINGS), enchants, ""+plugin.message.get().get("hardened-leather-leggings"), null, 0).getFresh(), new Item(new ItemStack(Material.LEATHER_BOOTS), enchants, ""+plugin.message.get().get("hardened-leather-boots"), null, 0).getFresh() });
                }
                else if (id == 19) {
                    enchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new Item(new ItemStack(Material.IRON_HELMET), enchants, ""+plugin.message.get().get("tempered-iron-helm"), null, 0).getFresh(), new Item(new ItemStack(Material.IRON_CHESTPLATE), enchants, ""+plugin.message.get().get("tempered-iron-chestplate"), null, 0).getFresh(), new Item(new ItemStack(Material.IRON_LEGGINGS), enchants, ""+plugin.message.get().get("tempered-iron-leggings"), null, 0).getFresh(), new Item(new ItemStack(Material.IRON_BOOTS), enchants, ""+plugin.message.get().get("tempered-iron-boots"), null, 0).getFresh() });
                }
                else if (id == 20) {
                    enchants.put(Enchantment.THORNS, 1);
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new Item(new ItemStack(Material.LEATHER_HELMET), enchants, ""+plugin.message.get().get("spiked-leather-helm"), null, 0).getFresh(), new Item(new ItemStack(Material.LEATHER_CHESTPLATE), enchants, ""+plugin.message.get().get("spiked-leather-chestplate"), null, 0).getFresh(), new Item(new ItemStack(Material.LEATHER_LEGGINGS), enchants, ""+plugin.message.get().get("spiked-leather-leggings"), null, 0).getFresh(), new Item(new ItemStack(Material.LEATHER_BOOTS), enchants, ""+plugin.message.get().get("spiked-leather-boots"), null, 0).getFresh() });
                }
                else if (id == 21) {
                    enchants.put(Enchantment.THORNS, 1);
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new Item(new ItemStack(Material.IRON_HELMET), enchants, ""+plugin.message.get().get("spiked-iron-helm"), null, 0).getFresh(), new Item(new ItemStack(Material.IRON_CHESTPLATE), enchants, ""+plugin.message.get().get("spiked-iron-chestplate"), null, 0).getFresh(), new Item(new ItemStack(Material.IRON_LEGGINGS), enchants, ""+plugin.message.get().get("spiked-iron-leggings"), null, 0).getFresh(), new Item(new ItemStack(Material.IRON_BOOTS), enchants, ""+plugin.message.get().get("spiked-iron-boots"), null, 0).getFresh() });
                }
                else if (id == 22) {
                    enchants.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    enchants.put(Enchantment.THORNS, 1);
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new Item(new ItemStack(Material.DIAMOND_HELMET), enchants, ""+plugin.message.get().get("diamond-helm"), null, 0).getFresh(), new Item(new ItemStack(Material.DIAMOND_CHESTPLATE), enchants, ""+plugin.message.get().get("diamond-chestplate"), null, 0).getFresh(), new Item(new ItemStack(Material.DIAMOND_LEGGINGS), enchants, ""+plugin.message.get().get("diamond-leggings"), null, 0).getFresh(), new Item(new ItemStack(Material.DIAMOND_BOOTS), enchants, ""+plugin.message.get().get("diamond-boots"), null, 0).getFresh() });
                }
                else if (id == 23) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.BAKED_POTATO), new ItemStack(Material.APPLE) });
                }
                else if (id == 24) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.MELON), new ItemStack(Material.COOKED_SALMON) });
                }
                else if (id == 25) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.COOKED_BEEF) });
                }
                else if (id == 26) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.COOKED_PORKCHOP, 2) });
                }
                else if (id == 27) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.PUMPKIN_PIE), new ItemStack(Material.CARROT) });
                }
                else if (id == 28) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.GOLDEN_CARROT) });
                }
                else if (id == 31) {
                    gamer.getPlayer().getInventory().addItem(new ItemStack[] { new ItemStack(new Potion(PotionType.STRENGTH, 1).toItemStack(1)), new ItemStack(new Potion(PotionType.REGEN, 2).toItemStack(1)) });
                }
            }
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    gamer.getPlayer().closeInventory();
                    gamer.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("buy-in-shop-success"));
                }
            }, 1L);
        }
        HandlerList.unregisterAll((Listener)this);
    }
}
