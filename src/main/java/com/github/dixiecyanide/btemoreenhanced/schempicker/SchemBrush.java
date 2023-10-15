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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchemBrush {
    private final String[] args;
    final List<String> directoies = SchemCollector.getDirectories();
    final List<String> schematics = SchemCollector.getSchematics();
    
    public SchemBrush(String[] args) {
        this.args = args;
    }

    public List<String> itemTabCompleter () {
        List<String> schemDirs = directoies;
        List<String> schems = schematics;
        List<String> itemTypes = new ArrayList<>();
        String regex = "^";
        Integer index = 0;

        if (args[0].equals("-s") & args.length < 3) {
            itemTypes = schems;
            return itemTypes;
        }

        for (String arg : Arrays.copyOfRange(args, 0, args.length - 1)) {
            index++;
            if (arg == null || schemDirs.isEmpty()){
                continue;
            }

            if (arg.indexOf(",") >= 0) {
                regex = processMultiarg(arg, regex);
            } else if (arg.equals("any")) {
                regex += "\\\\.*";
            } else {
                regex += String.format("\\\\%s", arg);
            }
            schemDirs = collectDirectories(schemDirs, regex);
        }

        for (String schemDir : schemDirs) {
            schemDir = schemDir.substring(0, schemDir.lastIndexOf("\\"));    // removing schematic name, so it won't appear as possible item type
            String[] schemDirParts = schemDir.split("\\\\");
            try {
                if (!itemTypes.contains(schemDirParts[index + 1])) {
                    itemTypes.add(schemDirParts[index + 1]);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return itemTypes; 
    }

    public List<String> argsProcessing() {
        List<String> schemNames = new ArrayList<>();
        List<String> schemDirs = directoies;
        String regex = "^";

        if (args[0].equals("-s")) {
            if (args[1].indexOf(",") >= 0) {
                return List.of(args[1].split(","));
            }
            return List.of(args[1]);
        }

        for (String arg : args) {
            if (arg == null || schemDirs.isEmpty()){
                continue;
            }
            if (arg.indexOf(",") >= 0) {
                regex = processMultiarg(arg, regex);
            } else if (arg.equals("any")) {
                regex += "\\\\.*";
            } else {
                regex += String.format("\\\\%s", arg);
            }
            schemDirs = collectDirectories(schemDirs, regex);
        }

        for (String schemDir : schemDirs) {
            String fullSchemName = schemDir.substring(schemDir.lastIndexOf("\\") + 1);
            String schemExt = fullSchemName.substring(fullSchemName.lastIndexOf("."));
            String schemName = fullSchemName.substring(0, fullSchemName.length() - schemExt.length());
            
            if (!schemNames.contains(schemName)) {
                schemNames.add(schemName);
            }
        }
        return schemNames;
    }

    public List<String> collectDirectories(List<String> schemDirs, String regex) {
        List<String> fittingSchemDirs = new ArrayList<>();
        regex += "\\\\.*";
        
        for (String schemDir : schemDirs) {
            if (schemDir.matches(regex)) {
                fittingSchemDirs.add(schemDir);
            }
        }
        return fittingSchemDirs;
    }

    public String processMultiarg (String arg, String regex) {
        String[] argParts = arg.split(",");
        String regexPart = "(?:";
        for (String argPart : argParts) {
            regexPart += String.format("|%s", argPart);
        }
        regex += String.format("\\\\%s)", regexPart);
        return regex;
    }
}