package com.tigerhix.vampirez;

import com.tigerhix.vampirez.configs.Config;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import static com.tigerhix.vampirez.Reason.INITIATIVE;


public class Game {
    public static Main plugin;
    public static Random random;
    public static Location lobby;
    public static String namearena;

    public Game(final Main plugin) {
        Game.plugin = plugin;
        Game.random = new Random();
    }

    public static void scoreboardGame(ScoreboardManager manager, final Player player, final Arena arena, final int playersize) {


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Game.plugin, new Runnable() {
            public void run() {
                Scoreboard board = manager.getNewScoreboard();
                Objective objective = board.registerNewObjective("Infogame", "dummy");
                objective.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "VampireZ");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

                for (final Gamer gamer : arena.gamers) {
                    final Player player = gamer.getPlayer();
                    player.setScoreboard(board);
                }

                Score space2 = objective.getScore(ChatColor.WHITE + "" + ChatColor.RESET);
                space2.setScore(10);
                Score score = objective.getScore(ChatColor.WHITE + "" + plugin.message.get().get("map-name") + ": " + ChatColor.DARK_GREEN + namearena); //Get a fake offline player
                score.setScore(9);
                Score score2 = objective.getScore(ChatColor.WHITE + "" + plugin.message.get().get("players") + ": " + ChatColor.DARK_GREEN + arena.gamers.size() + ChatColor.WHITE +"/" + ChatColor.DARK_GREEN + Config.maxPlayer); //Get a fake offline player
                score2.setScore(8);
                Score space = objective.getScore(ChatColor.WHITE + " ");
                space.setScore(7);
                Score score3 = objective.getScore(ChatColor.WHITE + "" + plugin.message.get().get("sign-waiting") + " ... "); //Get a fake offline player
                score3.setScore(6);
                Score space4 = objective.getScore(ChatColor.WHITE + " ");
                space4.setScore(5);
            }
        }, 5L);

    }

    public static void join(final Arena arena, final Gamer gamer) {

        namearena = arena.name;

        arena.gamers.add(gamer);

        arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + gamer.name + " " + plugin.message.get().get("joined") + " (" + arena.gamers.size() + "/" + Config.maxPlayer + ")");
//        arena.broadcast(String.valueOf(arena.lobbySpawn));
        arena.lobbySpawn = Utils.stringToLocation(arena.lobbySpawnString);
        gamer.playing = arena;
        final Player player = gamer.getPlayer();
        try {

            try {
                player.teleport(Utils.stringToLocation(arena.lobbySpawnString), PlayerTeleportEvent.TeleportCause.COMMAND);
            }catch (IllegalArgumentException e){
                WorldCreator worldCreator = new WorldCreator(Objects.requireNonNull(Utils.stringToLocation(arena.lobbySpawnString).getWorld()).getName());
                Bukkit.createWorld(worldCreator);
            }

            Game.plugin.inventories.put(player.getName(), player.getInventory().getContents());
            player.getInventory().clear();
            Utils.updateInventoryLater(player);
            World worldName = player.getLocation().getWorld();
            worldName.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            worldName.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
//            worldName.setDifficulty(Difficulty.PEACEFUL);

            player.getInventory().setItem(8, ItemTemplate.SLIME_BALL.getItem());
            player.setExp(0.0f);
            player.setLevel(0);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);



            scoreboardGame(Bukkit.getScoreboardManager(), gamer.getPlayer(), arena, arena.gamers.size());
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter"));
            player.sendMessage(ChatColor.GREEN + " ");
            player.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("you-are-joined") + " " + plugin.message.get().get("map-name") + " " + ChatColor.GREEN + "\"" + namearena + "\"");
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD);
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter") + "\n");
            if (arena.gamers.size() == Config.minPlayer) {
                ready(arena, Config.minSeconds);
            }
            if (arena.gamers.size() == Config.maxPlayer) {
                ready(arena, Config.maxSeconds);
            }


        } catch (IllegalArgumentException e) {
            leave(gamer, INITIATIVE);
        }
    }


    public static void leave(final Gamer gamer, final String reason) {

        final Arena arena = gamer.playing;
        arena.gamers.remove(gamer);
        if (reason.equals(INITIATIVE)) {
            arena.broadcastOthers(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("player") + ChatColor.GOLD + " " + gamer.name + " " + plugin.message.get().get("left-the-game") + " " + " (" + arena.gamers.size() + "/" + Config.maxPlayer + ")", gamer);
        }
        if (reason.equals(Reason.OPERATOR)) {
            arena.broadcastOthers(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("player") + ChatColor.GOLD + " " + gamer.name + " " + plugin.message.get().get("was-kicked") + " (" + arena.gamers.size() + "/" + Config.maxPlayer + ")", gamer);
        }

        gamer.reset();
        final Player player = gamer.getPlayer();

        player.setScoreboard(Game.plugin.sm.getNewScoreboard());
        player.getInventory().clear();
        player.getInventory().setContents((ItemStack[]) Game.plugin.inventories.get(player.getName()));
        player.getInventory().setArmorContents((ItemStack[]) null);
//        Utils.updateInventoryLater(player);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setHealth(20.0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(false);
        player.setInvisible(false);


        World worldName = player.getLocation().getWorld();
        worldName.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        worldName.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
//        worldName.setDifficulty(Difficulty.PEACEFUL);

        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (!reason.equals(Reason.AUTOMATIC)) {
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter"));
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD);
            player.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("you-left-the-game"));
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD);
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter") + "\n");
        }
        if (plugin.getConfig().getBoolean("proxy-mode")) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(plugin.getConfig().getString("proxy-main-server"));
            } catch (IOException eee) {
                Bukkit.getConsoleSender().sendMessage(eee.getMessage());
            }
            Bukkit.getPlayer(player.getUniqueId()).sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
//            player.kickPlayer(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("you-left-the-game"));
        } else {
            player.teleport((Game.lobby == null) ? Bukkit.getWorlds().get(0).getSpawnLocation() : Game.lobby, PlayerTeleportEvent.TeleportCause.COMMAND);
        }

        if (arena.gamers.size() == 0) {
            arena.stopTimer();
            arena.reset();
//            Utils.tell(ChatColor.GREEN+"[VampireZ] "+ ChatColor.RED + plugin.message.get().get("arena-reset"));
        }
        if (arena.gamers.size() == 1 && arena.status.equals("started")) {
            stop(arena);
        }
    }

    public static void ready(final Arena arena, final int time) {
        if (arena.waitingID != 0) {
            Bukkit.getScheduler().cancelTask(arena.waitingID);
        }
        arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("the-game-will-start-soon"));
        arena.waitingSeconds = time + 1;
        arena.waitingID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Game.plugin, new Runnable() {
            @Override
            public void run() {
                --arena.waitingSeconds;
                if (arena.waitingSeconds == 0) {
                    Game.start(arena);
                    Bukkit.getScheduler().cancelTask(arena.waitingID);
                } else if (arena.waitingSeconds <= 5 || arena.waitingSeconds == 10 || arena.waitingSeconds == 20 || arena.waitingSeconds == 30 || arena.waitingSeconds == 40 || arena.waitingSeconds == 50 || arena.waitingSeconds == 60) {
                    arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + "" + plugin.message.get().get("game-will-start-in") + " " + arena.waitingSeconds + " " + plugin.message.get().get("seconds") + "!");
                }
                for (final Gamer gamer : arena.gamers) {
                    final Player player = gamer.getPlayer();
                    player.setLevel(arena.waitingSeconds);
                    if (arena.waitingSeconds <= 5 && arena.waitingSeconds > 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
                    }
                }
                if (arena.gamers.size() < Config.minPlayer) {
                    Bukkit.getScheduler().cancelTask(arena.waitingID);
                    for (final Gamer gamer : arena.gamers) {
                        final Player player = gamer.getPlayer();
                        player.setLevel(0);
                    }
                }
            }
        }, 20L, 20L);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void start(final Arena arena) {

        arena.status = "started";

        final int randomIndex = (arena.gamers.size() == 1) ? 0 : Game.random.nextInt(arena.gamers.size() - 1);
        final Gamer firstVampire = arena.gamers.get(randomIndex);
        firstVampire.alive = false;

        if (arena.gamers.size() > 8) {
            final Gamer secondVampire = (arena.gamers.get(randomIndex + 1) == null) ? arena.gamers.get(randomIndex - 1) : arena.gamers.get(randomIndex + 1);
            secondVampire.alive = false;
        }
        for (final Gamer gamer : arena.gamers) {
            gamer.gameStarted = true;
            final Player player = gamer.getPlayer();
            if (!gamer.alive) {
                gamer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter") + "\n\n");
                gamer.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + ChatColor.BOLD + plugin.message.get().get("you-are-vampire"));
                gamer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter") + "\n");
                gamer.addCash(40);

                try {
                    player.teleport(Utils.stringToLocation(arena.vampireSpawnString), PlayerTeleportEvent.TeleportCause.COMMAND);
                }catch (IllegalArgumentException e){
                    WorldCreator worldCreator = new WorldCreator(Objects.requireNonNull(Utils.stringToLocation(arena.vampireSpawnString).getWorld()).getName());
                    Bukkit.createWorld(worldCreator);
                }

                player.getInventory().setItem(0, ItemTemplate.ORIGINAL_VAMPIRE_FANG.getItem());
                player.addPotionEffects((Collection) Config.vampireEffects);
                player.getInventory().setHelmet(ItemTemplate.VAMPIRE_HEAD.getItem());
                player.getInventory().setChestplate(ItemTemplate.VAMPIRE_CLOTH.getItem());
                player.setFoodLevel(20);
            } else {
                gamer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter") + "\n\n");
                gamer.sendMessage(ChatColor.GREEN + "[VampireZ] " + ChatColor.RED + ChatColor.BOLD + plugin.message.get().get("you-are-alive"));
                gamer.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.message.get().get("delimiter") + "\n");
                gamer.addCash(10);

                try {
                    player.teleport(Utils.stringToLocation(arena.survivorSpawnString), PlayerTeleportEvent.TeleportCause.COMMAND);
                }catch (IllegalArgumentException e){
                    WorldCreator worldCreator = new WorldCreator(Objects.requireNonNull(Utils.stringToLocation(arena.survivorSpawnString).getWorld()).getName());
                    Bukkit.createWorld(worldCreator);
                }

                player.getInventory().setItem(0, ItemTemplate.WOOD_SWORD.getItem());
                player.addPotionEffects((Collection) Config.survivorEffects);
                player.setFoodLevel(20);
            }
            player.getWorld().setDifficulty(Difficulty.HARD);
        }

        arena.startTimer();
        arena.timeLeft = 30;
        arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("first-wave"));
    }

    public static void next(final Arena arena) {
        if (!arena.status.equals("started")) {
            return;
        }
        ++arena.wave;
        if (arena.wave == 21) {
            stop(arena);
        }
        if (arena.wave == 1) {
            arena.timeLeft = 90;
        } else if (arena.wave == 2) {
            arena.timeLeft = 80;
        } else if (arena.wave == 3) {
            arena.timeLeft = 70;
        } else if (arena.wave == 4) {
            arena.timeLeft = 60;
        } else if (arena.wave == 5) {
            arena.timeLeft = 40;
        } else if (arena.wave == 20) {
            arena.timeLeft = 120;
        } else {
            arena.timeLeft = 20;
        }
        if (arena.wave != 20) {
            arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("wave") + " №" + arena.wave + ". " + arena.timeLeft + " " + plugin.message.get().get("seconds") + " " + plugin.message.get().get("before-the-next-wave") + "");
        } else {
            arena.broadcast(ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD + plugin.message.get().get("last-wave"));
        }
        for (final Gamer gamer : arena.gamers) {
            final Player player = gamer.getPlayer();
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
            player.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 5);
            if (!gamer.alive) {
                gamer.addCash(3);
                Gamer chosen = null;
                final int closest = 10000;
                for (final Gamer vampire : arena.getVampires()) {
                    for (final Gamer survivor : arena.getSurvivors()) {
                        if (vampire.getPlayer().getLocation().distance(survivor.getPlayer().getLocation()) < closest) {
                            chosen = vampire;
                        }
                    }
                }
                assert chosen != null;
                final Location thunderLocation = chosen.getPlayer().getLocation();
                thunderLocation.setX(thunderLocation.getX() + new Random().nextInt(10));
                thunderLocation.setZ(thunderLocation.getZ() + new Random().nextInt(10));
                chosen.getPlayer().getWorld().strikeLightningEffect(chosen.getPlayer().getLocation());
                thunderLocation.setX(thunderLocation.getX() - new Random().nextInt(10));
                thunderLocation.setZ(thunderLocation.getZ() - new Random().nextInt(10));
                chosen.getPlayer().getWorld().strikeLightningEffect(chosen.getPlayer().getLocation());
                thunderLocation.setX(thunderLocation.getX() + new Random().nextInt(10));
                thunderLocation.setZ(thunderLocation.getZ() + new Random().nextInt(10));
                chosen.getPlayer().getWorld().strikeLightningEffect(chosen.getPlayer().getLocation());
                thunderLocation.setX(thunderLocation.getX() - new Random().nextInt(10));
                thunderLocation.setZ(thunderLocation.getZ() - new Random().nextInt(10));
                chosen.getPlayer().getWorld().strikeLightningEffect(chosen.getPlayer().getLocation());
            } else {
                player.addPotionEffects(Config.waveEffects);
                gamer.addCash(10);
                if (arena.wave <= 1) {
                    continue;
                }
                gamer.addCoins(1);
            }
        }
        final List<Gamer> randomSurvivors = new ArrayList<Gamer>();
        for (int i = 1; i <= 3; ++i) {
            randomSurvivors.add((arena.getSurvivors().size() > 1) ? arena.getSurvivors().get(Game.random.nextInt(arena.getSurvivors().size() - 1)) : arena.getSurvivors().get(0));
        }
        for (final Gamer survivor2 : randomSurvivors) {
            final Location spawn = survivor2.getNearestZombieSpawn();
            for (int count = Game.random.nextInt(arena.wave) + 2, j = 1; j <= count; ++j) {

//                final LivingEntity zombie = (LivingEntity)arena.vampireSpawn.getWorld().spawnEntity(Utils.getRandomNearbyLocation(spawn), EntityType.ZOMBIE);
                final LivingEntity zombie = (LivingEntity) Utils.stringToLocation(arena.vampireSpawnString).getWorld().spawnEntity(Utils.getRandomNearbyLocation(spawn), EntityType.ZOMBIE);
                zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
                arena.zombies.add((Entity) zombie);


            }
        }
        for (final Gamer vampire2 : arena.getVampires()) {
            if (Game.random.nextInt(9) >= 3) {
                continue;
            }
            final LivingEntity betterZombie = (LivingEntity) vampire2.getPlayer().getWorld().spawnEntity(vampire2.getPlayer().getLocation(), EntityType.ZOMBIE);
            betterZombie.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET, 1));
            betterZombie.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
            arena.zombies.add((Entity) betterZombie);
        }
    }

    public static void stop(final Arena arena) {


        arena.stopTimer();
        if (arena.getWinningTeam().equalsIgnoreCase(plugin.message.get().get("survivors").toString())) {
            arena.getSurvivors().get(0).addCoins(100);
        } else if (arena.getWinningTeam().equalsIgnoreCase(plugin.message.get().get("vampires").toString())) {
            arena.getVampires().get(0).addCoins(200);
        }
        StringBuilder stats = new StringBuilder();
//        stats.append(plugin.message.get().get("vampires")).append(":\n");
        String title = "";
        for (final Gamer gamer : arena.gamers) {
            gamer.gameStarted = false;
            if (gamer.alive && arena.getWinningTeam().equalsIgnoreCase(plugin.message.get().get("survivors").toString())) {
                title = ChatColor.GREEN + "" + plugin.message.get().get("survivors") + " " + plugin.message.get().get("won");
            }
            if (!gamer.alive && !arena.getWinningTeam().equalsIgnoreCase(plugin.message.get().get("survivors").toString())) {
                title = ChatColor.RED + "" + plugin.message.get().get("vampires") + " " + plugin.message.get().get("won");
            }
            gamer.getPlayer().sendTitle(title, "", 1, 100, 1);
            stats.append(ChatColor.GREEN).append(ChatColor.BOLD).append(gamer.getPlayer().getName()).append(ChatColor.RESET).append(ChatColor.GOLD).append(" - ").append(ChatColor.GRAY).
                    append(plugin.message.get().get("survivor-kills").toString()).append(ChatColor.GOLD).append(" ").append(gamer.inGameSurvivorKills).append(", ").append(ChatColor.GRAY).
                    append(plugin.message.get().get("vampire-kills").toString()).append(ChatColor.GOLD).append(" ").
                    append(gamer.inGameVampireKills).append(" ").
                    append("(+").append(gamer.coins).append(" coins)\n");
        }
//        stats.append("\n").append(plugin.message.get().get("survivors")).append(":\n");
        arena.broadcast(
                ChatColor.GREEN + "[VampireZ] " + ChatColor.GOLD +
                        plugin.message.get().get("game-over") + " " +
                        arena.getWinningTeam().substring(0, 1).toUpperCase() +
                        arena.getWinningTeam().substring(1) + " " +
                        plugin.message.get().get("won") + "\n"+ ChatColor.GREEN + ChatColor.BOLD + "\n" +
                        ChatColor.BOLD + ChatColor.RED + plugin.message.get().get("game-stats") + "\n" +
                        stats


        );


        Bukkit.getScheduler().runTaskLater(Game.plugin, () -> {
            for (final Gamer gamer : arena.gamers) {
                gamer.save();
                Game.leave(gamer, Reason.AUTOMATIC);
            }
            arena.reset();
        }, 100L);

        arena.status = "waiting";
    }

}
