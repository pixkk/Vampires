package com.tigerhix.vampirez;

import net.minecraft.server.v1_16_R3.PacketPlayInClientCommand;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.material.*;
import org.bukkit.block.*;
import org.bukkit.event.*;
import org.bukkit.potion.*;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;

public class Listeners implements Listener
{
    public static Main plugin;
    public static Arena arena;

    public Listeners(final Main plugin) {
        Listeners.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler
    public void onSignChange(final SignChangeEvent evt) {
        if (!evt.getLine(0).equalsIgnoreCase("[Vampirez]")) {
            return;
        }
        if (!evt.getPlayer().isOp()) {
            return;
        }
        if (evt.getLine(1) == null) {
            evt.getPlayer().sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + "" + plugin.message.get().get("no-name-of-arena"));
            return;
        }
        if (!Listeners.plugin.arenas.containsKey(evt.getLine(1))) {
            evt.getPlayer().sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED +   plugin.message.get().get("arena-does-not-exist"));
            return;
        }
        evt.getPlayer().sendMessage(ChatColor.GREEN + "[VampireZ] " +   plugin.message.get().get("sign-created"));
        final Sign sign = new Sign(Listeners.plugin, Listeners.plugin.arenas.get(evt.getLine(1)), evt.getBlock());
        Listeners.plugin.signs.put(evt.getBlock().getLocation(), sign);
        sign.startTimer();
        sign.save(false);
    }
    
    @EventHandler
    public void onSignBreak(final BlockBreakEvent evt) {
    	//evt.getBlock().getType() != Material.OAK_SIGN && 
        if (evt.getBlock().getType() != Material.OAK_WALL_SIGN && evt.getBlock().getType() != Material.OAK_SIGN ) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing != null) {
            return;
        }
        if (!Listeners.plugin.signs.containsKey(evt.getBlock().getLocation())) {
            return;
        }
        if (!evt.getPlayer().isOp()) {
            evt.setCancelled(true);
            return;
        }
        evt.getPlayer().sendMessage(ChatColor.RED + "[VampireZ] " +   plugin.message.get().get("sign-removed"));
        final Sign sign = Listeners.plugin.signs.get(evt.getBlock().getLocation());
        Listeners.plugin.signs.remove(evt.getBlock().getLocation());
        sign.stopTimer();
        sign.save(true);
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent evt) {
        Listeners.plugin.gamers.put(evt.getPlayer().getName(), new Gamer(Listeners.plugin, evt.getPlayer().getName()));
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) != null && Utils.getGamer(evt.getPlayer()).playing != null) {
            Game.leave(Utils.getGamer(evt.getPlayer()), Reason.INITIATIVE);
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(final AsyncPlayerChatEvent evt) {
        final Player player = evt.getPlayer();
        final Gamer gamer = Utils.getGamer(player);
        final Arena arena = gamer.playing;

        try {
            if(arena == null){
                evt.setFormat(evt.getFormat());
            }
            else {
                if (Objects.equals(arena.status, "waiting")) {
                    evt.setFormat(evt.getFormat());
                }
                else {
                    if (gamer.alive) {
                        evt.setFormat(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "[Alive] " + ChatColor.RESET + evt.getFormat());
                    }
                    else {
                        evt.setFormat(ChatColor.RED + "" + ChatColor.BOLD + "[Vampire] " + ChatColor.RESET + evt.getFormat());
                    }
                }

            }
        }catch (NullPointerException ignored) {
            evt.setFormat(evt.getFormat());
        }

    }
    
//    @SuppressWarnings("deprecation")
//	@EventHandler
//    public void onLampOff(final BlockRedstoneEvent evt) {
//        for (final Sign sign : Listeners.plugin.signs.values()) {
//            final Block notifier = sign.block.getRelative(BlockFace.UP).getRelative(((Directional)sign.block.getType().getNewData(sign.block.getData())).getFacing().getOppositeFace());
//            if (notifier.getLocation().getBlockX() == evt.getBlock().getLocation().getBlockX() && notifier.getLocation().getBlockY() == evt.getBlock().getLocation().getBlockY() && notifier.getLocation().getBlockZ() == evt.getBlock().getLocation().getBlockZ()) {
//                evt.setNewCurrent(5);
//            }
//        }
//    }
    
    @EventHandler
    public void onRightClickShop(final PlayerInteractEvent evt) {
        if (evt.getAction() != Action.RIGHT_CLICK_AIR && evt.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (evt.getItem() == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        if (evt.getItem().getType() == Material.GOLD_NUGGET) {
            final SurvivorShop shop = new SurvivorShop(Listeners.plugin);
            shop.open(evt.getPlayer());
        }
        else if (evt.getItem().getType() == Material.REDSTONE) {
            final VampireShop shop2 = new VampireShop(Listeners.plugin);
            shop2.open(evt.getPlayer());
        }
        if (evt.getItem().getType() == Material.SLIME_BALL && Utils.getGamer(evt.getPlayer()).playing != null) {
        	Game.leave(Utils.getGamer(evt.getPlayer()), Reason.INITIATIVE);
        	return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent evt) {
        if (Utils.getGamer(evt.getEntity()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getEntity()).playing == null) {
            return;
        }
        evt.setDeathMessage((String)null);
        evt.getDrops().clear();
        evt.setDroppedExp(0);
        final Player player = evt.getEntity();
        final Gamer gamer = Utils.getGamer(player);
        final Arena arena = gamer.playing;
        if (gamer.alive) {
            gamer.alive = false;
            gamer.transferring = true;
            if (player.getKiller() == null) {
                arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + gamer.getDisplayName() + " "+  plugin.message.get().get("id-dead") +". "+ plugin.message.get().get("gamers-left")+ " " + arena.getSurvivors().size() + " "+  plugin.message.get().get("survivors-2") +"" + ((arena.getSurvivors().size() < 2) ? "" : "") );
            }
            else {
                final Player killer = player.getKiller();
                arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + gamer.getDisplayName() + " "+ ChatColor.RED + plugin.message.get().get("was-killed") +" " + killer.getDisplayName() + ". "+  plugin.message.get().get("gamers-left") + " " + arena.getSurvivors().size() + " "+  plugin.message.get().get("survivors-2") +"" + ((arena.getSurvivors().size() < 2) ? "" : "") );
                Utils.getGamer(killer).ding();
                Utils.getGamer(killer).addCash(15);
                Utils.getGamer(killer).addCoins(5);
                final Gamer gamer2 = Utils.getGamer(killer);
                ++gamer2.survivorKills;
                for (final Gamer other : arena.getVampires()) {
                    if (other.name.equalsIgnoreCase(killer.getName())) {
                        continue;
                    }
                    other.addCash(5);
                }
            }

            if (arena.getSurvivors().size() == 0) {
                Game.stop(arena);
            }
        }
        else {
            if (player.getKiller() == null) {
                return;
            }
            final Player killer = player.getKiller();
            arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + gamer.getDisplayName() + " "+  plugin.message.get().get("was-killed") +" " + killer.getDisplayName() + ".");
            Utils.getGamer(killer).ding();
            Utils.getGamer(killer).addCash(10);
            Utils.getGamer(killer).addCoins(5);
            final Gamer gamer3 = Utils.getGamer(killer);
            ++gamer3.vampireKills;

        }

        ((CraftPlayer)player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        if (evt.getMessage().length() < 8 || !evt.getMessage().substring(0, 8).equalsIgnoreCase("/vampire")) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + ""+  plugin.message.get().get("impossible-use-command-now") +". "+  plugin.message.get().get("enter-command") +" " + ChatColor.AQUA + "/vampire leave" + ChatColor.RED + ", "+  plugin.message.get().get("for-leave-arena") +".");
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        final Player player = evt.getPlayer();
        final Gamer gamer = Utils.getGamer(player);
        final Arena arena = gamer.playing;
        evt.setRespawnLocation(arena.vampireSpawn);
        player.getInventory().clear();
//        if (!arena.status.equals("started")) {
//            return;
//        }
        if (gamer.transferring) {
            gamer.transferring = false;
            player.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + ""+   ChatColor.BOLD + ""+  plugin.message.get().get("became-vampire") +".");
            gamer.cash = 0;
        }
        else {
            gamer.alive = false;
        }
        Listeners.plugin.getServer().getScheduler().runTaskLater((Plugin)Listeners.plugin, (Runnable)new Runnable() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void run() {
                gamer.addCash(20);
                player.getInventory().setItem(0, ItemTemplate.VAMPIRE_FANG.getItem());
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.addPotionEffects((Collection)Config.vampireEffects);
                player.getInventory().setHelmet(ItemTemplate.VAMPIRE_HEAD.getItem());
                player.getInventory().setChestplate(ItemTemplate.VAMPIRE_CLOTH.getItem());
            }
        }, 1L);
    }
    
    @EventHandler
    public void onDamageTeammate(final EntityDamageByEntityEvent evt) {
        if (!(evt.getDamager() instanceof Player) || !(evt.getEntity() instanceof Player)) {
            return;
        }
        if (Utils.getGamer((Player)evt.getDamager()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getDamager()).playing == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()).playing == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getDamager()).alive != Utils.getGamer((Player)evt.getEntity()).alive) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onVampireDamageZombie(final EntityDamageByEntityEvent evt) {
        if (!(evt.getDamager() instanceof Player) || !(evt.getEntity() instanceof Zombie)) {
            return;
        }
        if (Utils.getGamer((Player)evt.getDamager()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getDamager()).playing == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getDamager()).alive) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onZombieDamageVampire(final EntityDamageByEntityEvent evt) {
        if (!(evt.getEntity() instanceof Player) || !(evt.getDamager() instanceof Zombie)) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()).playing == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()).alive) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onSurvivorKillZombie(final EntityDeathEvent evt) {
        if (!(evt.getEntity() instanceof Zombie)) {
            return;
        }
        for (final Arena arena : Listeners.plugin.arenas.values()) {
            for (final Entity zombie : arena.zombies) {
                if (zombie.equals(evt.getEntity())) {
                    evt.getDrops().clear();
                    arena.zombies.remove(zombie);
                }
            }
        }
        if (evt.getEntity().getKiller() == null) {
            return;
        }
        if (Utils.getGamer(evt.getEntity().getKiller()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getEntity().getKiller()).playing == null) {
            return;
        }
        evt.setDroppedExp(0);
        final Player player = evt.getEntity().getKiller();
        final Gamer gamer = Utils.getGamer(player);
        Utils.getGamer(player).ding();
        gamer.addCash(2);
        gamer.addCoins(2);
    }
    
    @EventHandler
    public void onTargetVampire(final EntityTargetLivingEntityEvent evt) {
        if (!(evt.getTarget() instanceof Player)) {
            return;
        }
        if (Utils.getGamer((Player)evt.getTarget()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getTarget()).playing == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getTarget()).alive) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onBreakDoor(final EntityBreakDoorEvent evt) {
        if (!(evt.getEntity() instanceof Zombie)) {
            return;
        }
        for (final Arena arena : Listeners.plugin.arenas.values()) {
            for (final Entity zombie : arena.zombies) {
                if (zombie.equals(evt.getEntity())) {
                    evt.setCancelled(true);
                }
            }
        }
    }
    
//    @SuppressWarnings("deprecation")
//	@EventHandler
//    public void onStep(final PlayerInteractEvent evt) {
//        if (Utils.getGamer(evt.getPlayer()) == null) {
//            return;
//        }
//        if (Utils.getGamer(evt.getPlayer()).playing == null) {
//            return;
//        }
//        if (evt.getAction() != Action.PHYSICAL || evt.getClickedBlock().getType() != Material.LEGACY_SOIL) {
//            return;
//        }
//        evt.setCancelled(true);
//    }
    
    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onBreakBlock(final BlockBreakEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onHunger(final FoodLevelChangeEvent evt) {
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()).playing == null) {
            return;
        }
        evt.setFoodLevel(20);
    }
    
    @EventHandler
    public void onFallDamage(final EntityDamageEvent evt) {
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }
        if (evt.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()).playing == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getEntity()).alive) {
            return;
        }
        evt.setCancelled(true);
    }
    
    
    @EventHandler
    public void onClickInventory(final InventoryClickEvent evt) {
        if (evt.getView().getTitle().contains("Shop")) {
            return;
        }
        if (Utils.getGamer((Player)evt.getWhoClicked()) == null) {
            return;
        }
        if (Utils.getGamer((Player)evt.getWhoClicked()).playing == null) {
            return;
        }
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onDropItem(final PlayerDropItemEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        if (evt.getItemDrop().getItemStack().getType() == Material.GOLD_NUGGET || evt.getItemDrop().getItemStack().getType() == Material.REDSTONE) {
            evt.getItemDrop().getItemStack().setAmount(0);
            return;
        }
        evt.setCancelled(true);
        Utils.updateInventoryLater(evt.getPlayer());
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPickupItem(final PlayerPickupItemEvent evt) {
        if (Utils.getGamer(evt.getPlayer()) == null) {
            return;
        }
        if (Utils.getGamer(evt.getPlayer()).playing == null) {
            return;
        }
        evt.setCancelled(true);
    }
}
