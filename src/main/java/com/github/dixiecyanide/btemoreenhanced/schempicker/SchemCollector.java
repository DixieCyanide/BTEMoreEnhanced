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


package com.github.dixiecyanide.btemoreenhanced.schempicker;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SchemCollector {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final Plugin plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static ArrayList<String> treepacksFolders = new ArrayList<>();
    private static File folderWE = new File(we.getDataFolder() + File.separator + "schematics");

    private static final Map<String, List<String>> directories = new HashMap<>();
    private static final Map<String, List<String>> schems = new HashMap<>();

    public SchemCollector () {
        treepacksFolders.add(plugin.getConfig().getString("TreepackFolder"));
        if (!plugin.getConfig().getString("TreepackAddons").equals("")) {
            treepacksFolders.addAll(Arrays.asList(plugin.getConfig().getString("TreepackAddons").split(","))); // cus i need default pack always first and i think it could be done easier, but whatever...
        }

        for(String folder : treepacksFolders) {
            File folderFile = new File(folderWE + File.separator + folder);
            List<String> folderFiles = (collectFiles(folderFile.listFiles())); //these similar names are not really good, despite being technically descriptive
            directories.put(folderFile.getName(), folderFiles);
        }
    }
    
    private static List<String> collectFiles (File[] files) {
        List<String> interDirecs = new ArrayList<>();
        
        for (File file : files) {
            if (file.isDirectory()) {
                interDirecs.addAll(collectFiles(file.listFiles()));
                continue;
            }

            String fullFileName = file.getName();
            String fileName = fullFileName.substring(0, fullFileName.indexOf("."));
            String directory = file.toString().substring(folderWE.toString().length());
            String folderName = directory.substring(1, directory.indexOf(File.separator,1));

            if (!schems.containsKey(folderName)) {
                we.getLogger().warning("created folder key");
                schems.put(folderName, Arrays.asList(fileName));
            }
            for (Map.Entry<String, List<String>> entry : schems.entrySet()) {
                if (entry.getKey().equals(folderName) && !entry.getValue().contains(fileName)) {
                    List<String> newList = new ArrayList<>();
                    newList.addAll(entry.getValue());
                    newList.addLast(fileName);
                    schems.replace(folderName, newList);
                }
            }   
            if (!interDirecs.contains(directory)) {
                interDirecs.add(directory);
            }
        }
        return interDirecs;
    }
    
    public static Map<String, List<String>> getDirectories() {
        return directories;
    }

    public static Map<String, List<String>> getSchematics() {
        return schems;
    }
}