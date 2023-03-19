package com.tigerhix.vampirez;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandsTabCompleter implements TabCompleter {

    private final Main plugin;
    public CommandsTabCompleter(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> subcommands;
        if (args.length == 1) {
            if(commandSender.isOp()) {
                subcommands = Arrays.asList("join", "arenas", "leave", "lobby", "help", "create", "setsurvivor", "setvampire", "setzombie", "setprepare", "reload");
            }
            else {
                subcommands = Arrays.asList("join", "arenas", "leave", "lobby", "help");
            }

            return StringUtil.copyPartialMatches(args[0], subcommands, new ArrayList<>());
        } else if (args.length == 2) {

            if (args[0].equals("join")) {
                List<String> vampireArenas = plugin.getConfig().getStringList("arenas.enabled-arenas");
                return StringUtil.copyPartialMatches(args[1], vampireArenas, new ArrayList<>());
            }
        }
        return Collections.emptyList();

    }
}
