/*
 * BTEMoreEnhanced, a building tool
 * Copyright 2022 (C) DixieCyanide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package com.github.dixiecyanide.btemoreenhanced.commands;

import com.github.dixiecyanide.btemoreenhanced.schempicker.SchemBrush;
import com.github.dixiecyanide.btemoreenhanced.wood.Wood;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.platform.CommandSuggestionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.command.CommandUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WoodCommand implements TabExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private Boolean isIDpresent = false;
    private Integer IDPresenceSpot = null;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("btemoreenhanced.region.wood") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Integer argsLen = args.length;
        Player player = (Player) commandSender;
        com.sk89q.worldedit.entity.Player p = new BukkitPlayer((WorldEditPlugin) we, player);
        if (argsLen == 0) {
            commandSender.sendMessage(ChatColor.RED + "Specify tree type.");
            return false;
        }
        if (argsLen == 1 || (args[0].equals("-s") && argsLen < 3)) {
            commandSender.sendMessage(ChatColor.RED + "Specify the block you want trees to be placed above.");
            return false;
        }

        Wood wood;
        // If flags
        if (args[argsLen - 3].indexOf("-") == 0 && !args[argsLen - 3].equals("-s")) {            // if 3 flags (check for -s caused by collisions)
            String[] schemArgs = Arrays.copyOfRange(args, 0, argsLen - 4);
            String[] flags = Arrays.copyOfRange(args, argsLen - 4, argsLen);
            wood = new Wood(p, commandSender, schemArgs, args[argsLen - 4], flags);
        }else if (args[argsLen - 2].indexOf("-") == 0) {                                                  // if 2 flags
            String[] schemArgs = Arrays.copyOfRange(args, 0, argsLen - 3);
            String[] flags = Arrays.copyOfRange(args, argsLen - 4, argsLen);
            wood = new Wood(p, commandSender, schemArgs, args[argsLen - 3], flags);
        } else if (args[argsLen - 1].indexOf("-") == 0) {                                                 // if 1 flag
            String[] schemArgs = Arrays.copyOfRange(args, 0, argsLen - 2);
            String[] flags = Arrays.copyOfRange(args, argsLen - 4, argsLen);
            wood = new Wood(p, commandSender, schemArgs, args[argsLen - 2], flags);
        } else {                                                                                              // if no flags
            String[] schemArgs = Arrays.copyOfRange(args, 0, argsLen - 1);
            wood = new Wood(p, commandSender, schemArgs, args[argsLen - 1]);
        }

        isIDpresent = false;
        IDPresenceSpot = null;
        wood.execute();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        List<String> treeTypes = new ArrayList<>();
        Integer argsLen = args.length;

        SchemBrush schemBrush = new SchemBrush(args);
        treeTypes = schemBrush.itemTabCompleter();

        if (args[argsLen - 1].lastIndexOf(",") >= 0 && !treeTypes.isEmpty()) {                            // multiarg tabcompletion
            List<String> multiTreeTypes = new ArrayList<>();
            multiTreeTypes.addAll(treeTypes);

            for (Integer i = 0; i < multiTreeTypes.size(); i++) {                    
                String multiTreeType = multiTreeTypes.get(i);
                multiTreeTypes.set(i, args[argsLen - 1]
                .substring(0, args[argsLen - 1]
                .lastIndexOf(",") + 1) + multiTreeType);
            }

            StringUtil.copyPartialMatches(args[argsLen - 1], multiTreeTypes, completions);
            return completions;
        }
        StringUtil.copyPartialMatches(args[argsLen - 1], treeTypes, completions);

        if (IDPresenceSpot != null){
            if (argsLen - 1 > IDPresenceSpot) {
                isIDpresent = true;
            } else {
                isIDpresent = false;
            }
        } else {
            isIDpresent = false;
        }

        if (treeTypes.isEmpty()) {
            if (isIDpresent) {
                completions.addAll(List.of("-r:", "-dontRotate", "-includeAir"));
            } else {
                IDPresenceSpot = argsLen - 1;
                // block id suggestion, "//set " is used as trigger for suggester (kinda stupid, ngl)
                CommandSuggestionEvent suggestEvent = new CommandSuggestionEvent(wrapCommandSender(sender), "//set " + args[argsLen - 1]);
                WorldEdit.getInstance().getEventBus().post(suggestEvent);
                completions.addAll(CommandUtil.fixSuggestions("//set " + args[argsLen - 1], suggestEvent.getSuggestions()));
            }
        } else if (argsLen < 2) {
            completions.add(0, "-s");
        } else if (!args[0].equals("-s")){
            completions.add(0, "any");
        }
        return completions;
    }
    
    public Actor wrapCommandSender(CommandSender sender) {
        return wrapPlayer((Player) sender);
    }

    // Grab from FAWE
    public BukkitPlayer wrapPlayer(Player player) {
        //FAWE start - Use cache over returning a direct BukkitPlayer
        BukkitPlayer wePlayer = getCachedPlayer(player);
        if (wePlayer == null) {
            synchronized (player) {
                wePlayer = getCachedPlayer(player);
                if (wePlayer == null) {
                    wePlayer = new BukkitPlayer(    player);
                    player.setMetadata("WE", new FixedMetadataValue(we, wePlayer));
                    return wePlayer;
                }
            }
        }
        return wePlayer;
        //FAWE end
    }

    BukkitPlayer getCachedPlayer(Player player) {
        List<MetadataValue> meta = player.getMetadata("WE");
        if (meta.isEmpty()) {
            return null;
        }
        return (BukkitPlayer) meta.get(0).value();
    }
    // End grab from FAWE 
}
