package com.tigerhix.vampirez;

import org.bukkit.plugin.*;
//import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.*;
//import org.bukkit.util.*;
//import org.bukkit.util.Vector;

import com.tigerhix.vampirez.lib.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;
import org.bukkit.event.*;

public class VampireShop implements Listener
{
    public Main plugin;
    public String name;
    public List<Item> items;
    
    public VampireShop(final Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.name = ""+plugin.message.get().get("shop");
        (this.items = new ArrayList<Item>()).add(new Item(new ItemStack(Material.BLAZE_POWDER), null, "§c§l"+plugin.message.get().get("flaming-arrow")+"", ""+plugin.message.get().get("flaming-arrow-desc"), 5));
        this.items.add(new Item(new ItemStack(Material.REDSTONE_BLOCK), null, "§4§l"+plugin.message.get().get("vampire-regeneration")+"", ""+plugin.message.get().get("vampire-regeneration-desc"), 10));
        this.items.add(new Item(new ItemStack(Material.REDSTONE_ORE), null, "§6§l"+plugin.message.get().get("instant-health")+"", ""+plugin.message.get().get("instant-health-desc"), 15));
        this.items.add(new Item(new ItemStack(Material.BEACON), null, "§f§l"+plugin.message.get().get("vampire-aura")+"", ""+plugin.message.get().get("vampire-aura-desc"), 50));
        this.items.add(new Item(new ItemStack(Material.FEATHER), null, "§b§l"+plugin.message.get().get("vampire-jump")+"", ""+plugin.message.get().get("vampire-jump-desc"), 30));
        this.items.add(new Item(new ItemStack(Material.SPONGE), null, "§c§l"+plugin.message.get().get("health-boost")+"", ""+plugin.message.get().get("health-boost-desc"), 30));
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
        if (Utils.getGamer((Player)evt.getWhoClicked()).alive) {
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
            if ((id == 1 || id == 2) && gamer.getPlayer().getHealth() == 20.0) {
                gamer.getPlayer().closeInventory();
                gamer.getPlayer().sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + ""+plugin.message.get().get("not-sure-needed")+".");
                HandlerList.unregisterAll((Listener)this);
                return;
            }
            this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    gamer.getPlayer().closeInventory();
                    gamer.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("buy-in-shop-success"));
                    if (id == 0) {
                        if (gamer.flameArrowID != -1) {
                            VampireShop.this.plugin.getServer().getScheduler().cancelTask(gamer.flameArrowID);
                        }
                        gamer.sendMessage(ChatColor.RED + "[VampireZ] " + plugin.message.get().get("follow-arrow"));
                        final Player player = gamer.getPlayer();
                        final Location vampire = player.getLocation();
                        final Location survivor = gamer.getNearestSurvivor();
                        //final LocationIterator llllll = new LocationIterator(final World world, final org.bukkit.util.Vector vector, final Vector vector2, final double yOffset, final int maxDistance)
                        final LocationIterator locs = new LocationIterator(player.getWorld(), survivor.toVector(), new Vector(survivor.getBlockX() - vampire.getBlockX(),survivor.getBlockY() - vampire.getBlockY(), survivor.getBlockZ() - vampire.getBlockZ()), 0.0, (int)Math.floor(vampire.distance(survivor)));
                        final List<Location> places = new ArrayList<Location>();
                        for (int i = 1; i <= 10; ++i) {
                            try {
                                final Location loc = locs.next();
                                places.add(loc);
                            }
                            catch (NoSuchElementException e) {
                                break;
                            }
                        }
                        gamer.flameArrowID = VampireShop.this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)VampireShop.this.plugin, (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                for (final Location loc : places) {
                                    player.getWorld().playEffect(loc, Effect.FIREWORK_SHOOT, 20);
                                }
                            }
                        }, 1L, 10L);
                        VampireShop.this.plugin.getServer().getScheduler().runTaskLater((Plugin)VampireShop.this.plugin, (Runnable)new Runnable() {
                            @Override
                            public void run() {
                                VampireShop.this.plugin.getServer().getScheduler().cancelTask(gamer.flameArrowID);
                            }
                        }, 100L);
                    }
                     if (id == 1) {
                        gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 2));
                    }
                    else if (id == 2) {
                        gamer.getPlayer().setHealth(20.0);
                    }
                    else if (id == 3) {
                        gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 2));
                    }
                    else if (id == 4) {
                        gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 5));
                    }
                    else if (id == 5) {
                        gamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 4));
                    }
                }
            }, 1L);
        }
        HandlerList.unregisterAll((Listener)this);
    }
}
