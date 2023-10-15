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


package com.github.dixiecyanide.btemoreenhanced.schempicker;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SchemCollector {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final Plugin plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static String treepackFolder = plugin.getConfig().getString("TreepackFolder");
    private static File folderWE = new File(we.getDataFolder() + File.separator + "schematics" + File.separator + treepackFolder);

    private static final List<String> directories = new ArrayList<>();
    private static final List<String> schems = new ArrayList<>();

    public SchemCollector () {
        directories.addAll(collectFiles(folderWE.listFiles()));
    }
    
    private static List<String> collectFiles (File[] files) {
        List<String> interDirecs = new ArrayList<>();
        
        for (File file : files) {
            if (!file.isDirectory()) {
                String fullFileName = file.getName();
                String fileExt = fullFileName.substring(fullFileName.lastIndexOf("."));
                String fileName = fullFileName.substring(0, fullFileName.length() - fileExt.length());
                String directory = file.toString().substring(folderWE.toString().length()); // fucking awful
                
                if (!schems.contains(fileName)) {
                    schems.add(fileName);
                }
                if (!interDirecs.contains(directory)) {
                    interDirecs.add(directory);
                }
            } else {
                interDirecs.addAll(collectFiles(file.listFiles()));
            }
        }
        return interDirecs;
    }
    
    public static List<String> getDirectories() {
        return directories;
    }

    public static List<String> getSchematics() {
        return schems;
    }

    public static void reloadPlugin() {
        treepackFolder = plugin.getConfig().getString("TreepackFolder");
        folderWE = new File(we.getDataFolder() + File.separator + "schematics" + File.separator + treepackFolder);
        directories.clear();
        schems.clear();
        directories.addAll(collectFiles(folderWE.listFiles()));
    }
}