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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeBrush implements TabExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    List<String> folderList = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> schemNames = new ArrayList<>();
        if (!commandSender.hasPermission("schematicbrush.brush.use") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        if (Bukkit.getPluginManager().getPlugin("SchematicBrushReborn") == null) {
            commandSender.sendMessage(ChatColor.RED + "Plugin SchematicBrush is not installed.");
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Specify a tree type: " + String.join("; ", treeTypesCompleter(args)));
            return true;
        }

        schemNames = argsProcessing(args);
        //commandSender.sendMessage(ChatColor.AQUA + "schbr " + String.join("@*!* ", schemNames) + "@*!* -place:bottom -yoff:1");  // debug
        
        Bukkit.dispatchCommand(commandSender, "schbr " + String.join("@*!* ", schemNames) + "*@*!* -place:bottom -yoff:1");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        List<String> treeTypes = new ArrayList<>();
        Integer argsLength = args.length;
        
        if (argsLength > 0 & argsLength < 5) {
            if (argsLength == 1) {
                args[0] = "";
            }
            
            treeTypes = treeTypesCompleter(Arrays.copyOfRange(args, 0, argsLength - 1));
            StringUtil.copyPartialMatches(args[argsLength - 1], treeTypes, completions);
            
            if (treeTypes.isEmpty()) {
                completions.add("There are no more folders.");
            } else {
                completions.add(0, "any");
            }
        }
        return completions;
    }

    public static List<String> treeTypesCompleter(String[] args) {
        File folderWE = new File(we.getDataFolder() + File.separator + "schematics" + File.separator + "newtrees");
        List<File>treeTypesPaths = List.of(new File(""));
        List<String> treeTypes = new ArrayList<>();
        
        for (String arg : args) {
            List<File>interPaths = new ArrayList<>();
            if (arg == null || treeTypesPaths.isEmpty()){
                continue;
            }
            if (arg.equals("any")) {
                treeTypesPaths = collectDirectories(folderWE, treeTypesPaths, arg);
            } else {
                for (File treeTypePath : treeTypesPaths) {
                    interPaths.add(new File(treeTypePath + File.separator + arg));
                }
                treeTypesPaths = interPaths;
            }
        }

        for (File treeTypePath : treeTypesPaths) {
            File folder = new File(folderWE + treeTypePath.toString());
            File[] files = folder.listFiles();
            
            if (!folder.exists() || files == null) {
                continue;
            }
            for (File file : files) {
                if (file.isDirectory() & !treeTypes.contains(file.getName())) {
                    treeTypes.add(file.getName());
                }
            }
        }
        
        return treeTypes;
    }

    public static List<String> argsProcessing(String[] args) {
        File folderWE = new File(we.getDataFolder() + File.separator + "schematics" + File.separator + "newtrees");
        List<String>schemNames = new ArrayList<>();
        List<File>folderPaths = List.of(new File(""));
        
        for (String arg : args) {
            if (arg == null || folderPaths.isEmpty()){
                continue;
            }
            if (arg.equals("any")) {
                folderPaths = collectDirectories(folderWE, folderPaths, arg);
                continue;
            }
            folderPaths = collectDirectories(folderWE, folderPaths, arg);
        }

        for (File folder : folderPaths) {
            File schemFolder = new File(folderWE + folder.toString());
            File[] files = schemFolder.listFiles();

            for (File schem : files) {
                if (!schem.isDirectory()) {
                    schemNames.add(schem.getName().substring(0, schem.getName().length() - 10));
                }
            }
        }
        
        return schemNames;
    }

    public static List<File> collectDirectories(File folderWE, List<File> folderPaths, String arg) {         // move to Utils
        List<File>itemPaths = new ArrayList<>();
        for (File folderPath : folderPaths) {
            File schemPath = new File(folderWE + folderPath.toString());
            File[] schemPaths = schemPath.listFiles();

            if (!schemPath.exists() || schemPaths == null) {
                continue;
            }

            for (File folder : schemPaths) {
                if (folder.isDirectory() & arg.equals("any")) {                                      
                    itemPaths.add(new File(folderPath + File.separator + folder.getName()));
                } else if (folder.isDirectory() & folder.getName().equals(arg)) {
                    itemPaths.add(new File(folderPath + File.separator + folder.getName()));
                }
            }
        }
        return itemPaths;
    }
}