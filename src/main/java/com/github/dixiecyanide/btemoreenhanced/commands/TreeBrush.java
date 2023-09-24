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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.schempicker.SchemBrush;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TreeBrush implements TabExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final Plugin plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static final String treepackFolder = plugin.getConfig().getString("TreepackFolder");
    private static final File folderWE = new File(we.getDataFolder() + File.separator + "schematics" + File.separator + treepackFolder);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SchemBrush schemBrush = new SchemBrush(folderWE, args);
        List<String> schemNames = new ArrayList<>();
        if (!sender.hasPermission("schematicbrush.brush.use") && !sender.isOp()) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        if (Bukkit.getPluginManager().getPlugin("SchematicBrushReborn") == null) {
            sender.sendMessage(ChatColor.RED + "Plugin SchematicBrush is not installed.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Specify a tree type: " + String.join("; ", schemBrush.itemTabCompleter()));
            return true;
        }
        schemNames = schemBrush.argsProcessing();
        // sender.sendMessage(ChatColor.AQUA + "schbr " + String.join("@*!* ", schemNames) + "@*!* -place:bottom -yoff:1");  // debug
        Bukkit.dispatchCommand(sender, "schbr " + String.join("@*!* ", schemNames) + "*@*!* -place:bottom -yoff:1"); // release
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        List<String> treeTypes = new ArrayList<>();
        Integer argsLength = args.length;

        if (argsLength > 0 & argsLength < 5) {                                  // gonna remove it i think
            SchemBrush schemBrush = new SchemBrush(folderWE, args);
            treeTypes = schemBrush.itemTabCompleter();

            for (Integer i = 0; i < treeTypes.size(); i++) {
                String treeType = treeTypes.get(i);
                treeTypes.set(i, args[argsLength - 1].substring(0, args[argsLength - 1].lastIndexOf(",") + 1) + treeType);
            }
            
            StringUtil.copyPartialMatches(args[argsLength - 1], treeTypes, completions);
            
            if (treeTypes.isEmpty()) {
                completions.add("There are no more folders.");
            } else if (argsLength < 2) {
                completions.add(0, "-s");
            } else if (!args[0].equals("-s")){
                completions.add(0, "any");
            }
        }
        return completions;
    }
}