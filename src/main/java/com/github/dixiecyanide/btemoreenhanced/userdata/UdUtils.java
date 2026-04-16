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
import com.github.dixiecyanide.btemoreenhanced.utils.Utils;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
    private Yaml yaml;

    public UdUtils() {
        userdataDir = new File(plugin.getDataFolder() + File.separator + "userdata");
        DumperOptions yamlOptions = new DumperOptions();
        yamlOptions.setIndent(2);
        yamlOptions.setPrettyFlow(true);
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(yamlOptions);
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
        Userdata userdata = getDefaultUd();
        PrintWriter writer;
        
        try {
            writer = new PrintWriter(userdataFile);
            yaml.dump(userdata, writer);
            bme.putOnlineUd(id, userdata);
        } catch (FileNotFoundException e) {
            createUd(id);
            writeDefaultUd(id);
        }
    }

    public boolean updateUd(UUID id, String key, Object value) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        Userdata userdata = getUd(id);
        PrintWriter writer;
        CommandSender commandSender = (CommandSender) Bukkit.getPlayer(id);

        try {
            switch (key) {
                case "Reach":
                    userdata.setReach(Double.parseDouble(value.toString()));
                case "TerrBlock": // this way it should automatically reject multiple ids
                    value = Utils.FixSingleLegacyID(value.toString());
                    userdata.getTerraformConfig().setTerrBlock(BlockTypes.get(value.toString()));
                case "TerrBiome":
                    userdata.getTerraformConfig().setTerrBiome(BiomeTypes.get(value.toString()));
                case "TerrTop":
                    userdata.getTerraformConfig().setTerrTop(Integer.parseInt(value.toString()));
                case "TerrBot":
                    userdata.getTerraformConfig().setTerrBot(Integer.parseInt(value.toString()));
                case "UnusedTreepacks":
                    if (value.toString().equals("none")){
                        value = "";
                    }
                    userdata.setUnusedTreepacks(List.of(value.toString().replace(" ", "").split(",")));
            }
        } catch (NumberFormatException e) {
            bme.getBMEChatLogger().error(commandSender, "bme.error.NaN", null);
            return false;
        } catch (NullPointerException e) {
            bme.getBMEChatLogger().error(commandSender,  "bme.error.invalid-id", e.getMessage());
            return false;
        }
        
        try {
            writer = new PrintWriter(userdataFile);
            yaml.dump(userdata, writer);
            bme.putOnlineUd(id, userdata);
        } catch (FileNotFoundException e) {
            createUd(id);
            writeDefaultUd(id);
            return false;
        }
        return true;
    }

    public Userdata getUd(UUID id) {
        File userdataFile = new File(userdataDir + File.separator + id.toString() + ".yml");
        try {
            InputStream inputStream = new FileInputStream(userdataFile);
            return yaml.loadAs(inputStream, Userdata.class);
        } catch (Exception e) {
            createUd(id);
            writeDefaultUd(id);
            return getDefaultUd();
        }
    }

    public Object getOnlineUdValue(UUID id, String key) {
        Userdata userdata = bme.getOnlineUd(id);
        Object value = null;

        switch (key) {
            case "Reach":
                value = userdata.getReach();
            break;
            case "TerrBlock":
                userdata.getTerraformConfig().getTerrBlock();  
            break;
            case "TerrBiome":
                userdata.getTerraformConfig().getTerrBiome();  
            break;  
            case "TerrTop":
                userdata.getTerraformConfig().getTerrTop();  
            break;
            case "TerrBot":
                userdata.getTerraformConfig().getTerrBot(); 
            break;
            case "UnusedTreepacks":
                userdata.getUnusedTreepacks();
            break;
            default:
            break;
        }
        return value;
    }

    public Userdata getDefaultUd() {
        Userdata userdata = new Userdata();
        TerraformConfig tfCfg = new TerraformConfig();

        tfCfg.setTerrBlock(BlockTypes.get(defaultBlock));
        tfCfg.setTerrBiome(BiomeTypes.get(defaultBiome));
        tfCfg.setTerrTop(defaultTopRemove);
        tfCfg.setTerrBot(defaultBotRemove);

        userdata.setReach(-1);
        userdata.setTerraformConfig(tfCfg);
        userdata.setUnusedTreepacks(new ArrayList<String>());

        return userdata;
    }
}