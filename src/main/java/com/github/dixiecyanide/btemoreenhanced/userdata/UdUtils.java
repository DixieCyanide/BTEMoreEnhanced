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


package com.github.dixiecyanide.btemoreenhanced.userdata;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


public class UdUtils {
    private static final Plugin plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static Integer defaultTopRemove = plugin.getConfig().getInt("DefaultTopRemove");
    private static Integer defaultBotRemove = plugin.getConfig().getInt("DefaultBotRemove");
    private static String defaultBlock = plugin.getConfig().getString("DefaultBlock");
    private static String defaultBiome = plugin.getConfig().getString("DefaultBiome");
    private File userdataDir;

    public UdUtils() {
        userdataDir = new File(plugin.getDataFolder() + File.separator + "userdata");
    }
    
    public void checkUdFolder() {
        userdataDir.mkdir(); // mkdir checks if directory exist, so here's where function name comes from.
    }

    public boolean isThereUd(UUID id) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        return userdataFile.exists();
    }

    public void createUd(UUID id) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        try {
            userdataFile.createNewFile();
        } catch (IOException e) {
            checkUdFolder();
        }
    }

    public void writeDefaultUd(UUID id) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        Map<String, Object> dataMap = getDefaultUd();
        PrintWriter writer;

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        
        try {
            writer = new PrintWriter(userdataFile);
            Yaml yaml = new Yaml(options);
            yaml.dump(dataMap, writer);
            bme.putOnlineUd(id, dataMap);
        } catch (FileNotFoundException e) {
            createUd(id);
            writeDefaultUd(id);
        }
    }

    public boolean updateUd(UUID id, String key, Object value) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        Map<String, Object> udMap = getUd(id);
        PrintWriter writer;
        CommandSender commandSender = (CommandSender) Bukkit.getPlayer(id);

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        switch (key) {
            case "Reach":
                try {
                    udMap.replace(key, Double.parseDouble(value.toString()));
                } catch (ClassCastException e) {
                    bme.getBMEChatLogger().error(commandSender, "bme.error.NaN", null);
                    return false;
                }
            break;
            case "TerrTop":
                try {
                    value = Integer.valueOf(value.toString());
                } catch (NumberFormatException e) {
                    bme.getBMEChatLogger().error(commandSender, "bme.error.not-an-integer", null);
                    return false;
                }
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    tfMap.replace(key, value);
                    udMap.replace("Terraform", tfMap);
                } catch (ClassCastException e) {
                    udMap.replace("Terraform", getDefaultUd().get("Terraform"));
                    return false;
                    // throw wrror
                }
            break;
            case "TerrBot":
                try {
                    value = Integer.valueOf(value.toString());
                } catch (NumberFormatException e) {
                    bme.getBMEChatLogger().error(commandSender, "bme.error.not-an-integer", null);
                    return false;
                }
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    tfMap.replace(key, value);
                    udMap.replace("Terraform", tfMap);
                } catch (Exception e) {
                    udMap.replace("Terraform", getDefaultUd().get("Terraform"));
                    return false;
                    //throw error
                }
            break;
            case "TerrBlock":
                if (BlockTypes.get(value.toString()) == null) {
                    bme.getBMEChatLogger().error(commandSender, "bme.error.settings.invalid-block", value.toString());
                    return false;
                }
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    tfMap.replace(key, value);
                    udMap.replace("Terraform", tfMap);
                } catch (Exception e) {
                    udMap.replace("Terraform", getDefaultUd().get("Terraform"));
                    return false;
                }
            break;
            case "TerrBiome":
                if (BiomeTypes.get(value.toString()) == null) {
                    bme.getBMEChatLogger().error(commandSender, "bme.error.settings.invalid-biome", value.toString());
                    return false;
                }
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    tfMap.replace(key, value);
                    udMap.replace("Terraform", tfMap);
                } catch (Exception e) {
                    udMap.replace("Terraform", getDefaultUd().get("Terraform"));
                    return false;
                }
            break;
            case "Terraform":
                udMap.replace(key, value);
            break;
            case "UnusedTreepacks":
                if (value.toString().equals("none")){
                    value = "";
                }
                udMap.replace(key, List.of(value.toString().replace(" ", "").split(",")));
            break;
            default:
                bme.getBMEChatLogger().error(commandSender, "bme.error.invalid-arg", null);
            break;
        }

        try {
            writer = new PrintWriter(userdataFile);
            Yaml yaml = new Yaml(options);
            yaml.dump(udMap, writer);
            bme.putOnlineUd(id, udMap);
        } catch (FileNotFoundException e) {
            createUd(id);
            writeDefaultUd(id);
            return false;
        }
        return true;
    }

    public Map<String, Object> getUd(UUID id) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        try {
            InputStream inputStream = new FileInputStream(userdataFile);
            return new Yaml().load(inputStream);
        } catch (FileNotFoundException e) {
            createUd(id);
            writeDefaultUd(id);
            return getDefaultUd();
        }
    }

    public Object getOnlineUdValue(UUID id, String key) {
        Map<String, Object> udMap = bme.getOnlineUd(id);
        Object value = null;

        switch (key) {
            case "Reach":
                value = udMap.get(key);
            break;
            case "TerrTop":
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    value = tfMap.get(key);
                } catch (Exception e) {
                    updateUd(id, "Terraform", getDefaultUd().get("Terraform"));
                }
            break;
            case "TerrBot":
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    value = tfMap.get(key);
                } catch (Exception e) {
                    updateUd(id, "Terraform", getDefaultUd().get("Terraform"));
                }
            break;
            case "TerrBlock":
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    value = tfMap.get(key);
                } catch (Exception e) {
                    updateUd(id, "Terraform", getDefaultUd().get("Terraform"));
                }
            break;
            case "TerrBiome":
                try {
                    Map<String, Object> tfMap = (Map<String, Object>) udMap.get("Terraform");
                    value = tfMap.get(key);
                } catch (Exception e) {
                    updateUd(id, "Terraform", getDefaultUd().get("Terraform"));
                }
            break;
            case "UnusedTreepacks":
                value = udMap.get(key).toString().substring(1, udMap.get(key).toString().length() - 1);
            break;
        
            default:
                break;
        }

        return value;
    }

    public Map<String, Object> getDefaultUd() {
        Map<String, Object> terraformMap = new HashMap<>();
        terraformMap.put("TerrTop", defaultTopRemove);
        terraformMap.put("TerrBot", defaultBotRemove);
        terraformMap.put("TerrBlock", defaultBlock);
        terraformMap.put("TerrBiome", defaultBiome);
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Reach", -1);
        dataMap.put("Terraform", terraformMap);
        dataMap.put("UnusedTreepacks", new ArrayList<String>());
        
        return dataMap;
    }
}