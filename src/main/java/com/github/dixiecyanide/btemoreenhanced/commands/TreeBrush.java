/*
 * BTEMoreEnhanced, a building tool
 * Copyright 2024 (C) DixieCyanide
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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.logger.Logger;
import com.github.dixiecyanide.btemoreenhanced.schempicker.SchemBrush;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TreeBrush implements TabExecutor {
    private static final BTEMoreEnhanced plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private Logger chatLogger;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        chatLogger = plugin.getBMEChatLogger();
        SchemBrush schemBrush = new SchemBrush(args);
        List<String> schemNames = new ArrayList<>();
        Player player = (Player) commandSender;
        
        if (!commandSender.hasPermission("schematicbrush.brush.use") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            chatLogger.error(commandSender, "bme.not-a-player", null);
            return true;
        }
        if (Bukkit.getPluginManager().getPlugin("SchematicBrushReborn") == null) {
            chatLogger.error(commandSender, "bme.treebr.no-plugin", null);
            return true;
        }
        if (args.length == 0) {
            chatLogger.error(commandSender, "bme.treebr.no-type", null);
            return true;
        }
        schemNames = schemBrush.argsProcessing(player.getUniqueId(), false);
        if (schemNames.isEmpty()) {
            chatLogger.warning(commandSender, "bme.treebr.zero-schems", null);
            return true;
        }

        commandSender.sendMessage(ChatColor.AQUA + "schbr " + String.join("@*!* ", schemNames) + "@*!* -place:bottom -yoff:1");  // debug
        // Bukkit.dispatchCommand(commandSender, "schbr " + String.join("@*!* ", schemNames) + "*@*!* -place:bottom -yoff:1"); // release
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        List<String> treeTypes = new ArrayList<>();
        Integer argsLen = args.length;
        Player player = (Player) commandSender;

        SchemBrush schemBrush = new SchemBrush(args);
        treeTypes = schemBrush.itemTabCompleter(player.getUniqueId());

        if (args[argsLen - 1].lastIndexOf(",") >= 0) {                                                    // multiarg tabcompletion
            List<String> multiTreeTypes = new ArrayList<>();
            multiTreeTypes.addAll(treeTypes);

            for (Integer i = 0; i < multiTreeTypes.size(); i++) {                    
                String multiTreeType = multiTreeTypes.get(i);
                multiTreeTypes.set(i, args[argsLen - 1].substring(0, args[argsLen - 1].lastIndexOf(",") + 1) + multiTreeType);
            }

            StringUtil.copyPartialMatches(args[argsLen - 1], multiTreeTypes, completions);
            return completions;
        }

        StringUtil.copyPartialMatches(args[argsLen - 1], treeTypes, completions);
        
        if (treeTypes.isEmpty()) {
            completions.add("There are no more folders.");
        } else if (argsLen < 2) {
            completions.add("-s");
            completions.add("any");
        } else if (!args[0].equals("-s")){
            completions.add(0, "any");
        }
        return completions;
    }
}