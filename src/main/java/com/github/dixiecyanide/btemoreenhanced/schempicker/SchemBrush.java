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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchemBrush {
    private final File folderWE;
    private final String[] args;
    
    public SchemBrush(File folderWE, String[] args) {
        this.folderWE = folderWE;
        this.args = args;
    }


    public List<String> itemTabCompleter () {
        List<File>itemTypesPaths = List.of(new File(""));
        List<String> itemTypes = new ArrayList<>();
        

        for (String arg : Arrays.copyOfRange(args, 0, args.length - 1)) {
            List<File>interPaths = new ArrayList<>();

            if (arg == null || itemTypesPaths.isEmpty()){
                continue;
            }

            if (arg.equals("any")) {
                itemTypesPaths = collectDirectories(itemTypesPaths, arg);
            } else {
                for (File itemTypePath : itemTypesPaths) {
                    interPaths.add(new File(itemTypePath + File.separator + arg));
                }
                itemTypesPaths = interPaths;
            }
        }

        for (File itemTypePath : itemTypesPaths) {
            File folder = new File(folderWE + itemTypePath.toString());
            File[] files = folder.listFiles();
            
            if (!folder.exists() || files == null) {
                continue;
            }
            for (File file : files) {
                if (file.isDirectory() & !itemTypes.contains(file.getName())) {
                    itemTypes.add(file.getName());
                }
            }
        }
        
        return itemTypes;
    }

    public List<String> argsProcessing() {
        List<String>schemNames = new ArrayList<>();
        List<File>folderPaths = List.of(new File(""));
        
        for (String arg : args) {
            if (arg == null || folderPaths.isEmpty()){
                continue;
            }
            if (arg.equals("any")) {
                folderPaths = collectDirectories(folderPaths, arg);
                continue;
            }
            folderPaths = collectDirectories(folderPaths, arg);
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

    // i don't like the fact i'm using same variable names in different functions

    public List<File> collectDirectories(List<File> folderPaths, String arg) {
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