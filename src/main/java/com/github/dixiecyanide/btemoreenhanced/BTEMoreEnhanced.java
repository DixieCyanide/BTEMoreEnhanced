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


package com.github.dixiecyanide.btemoreenhanced;

import com.github.dixiecyanide.btemoreenhanced.bstats.Metrics;
import com.github.dixiecyanide.btemoreenhanced.commands.*;
import com.github.dixiecyanide.btemoreenhanced.schempicker.SchemCollector;
import com.github.dixiecyanide.btemoreenhanced.update.UpdateChecker;
import com.github.dixiecyanide.btemoreenhanced.update.UpdateNotification;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BTEMoreEnhanced extends JavaPlugin {
    @Override
    public void onDisable() {
        getLogger().info("\033[0;35m" + "Goodbye!" + "\033[0m");
    }

    @Override
    public void onEnable() {
        try {
            Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
        } catch (Exception e) {
            getLogger().info("\033[0;31m" + "Couldn't find FastAsyncWorldEdit plugin. Please check plugins." + "\033[0m");
            Bukkit.getPluginManager().disablePlugin(BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class));
        }
        
        saveDefaultConfig();
        getCommand("/wood").setExecutor(new WoodCommand());
        getCommand("btemoreenhanced-reload").setExecutor(new ReloadConfig());
        getCommand("/dellast").setExecutor(new DelLast());
        getCommand("/delpoint").setExecutor(new DelPoint());
        getCommand("/treebrush").setExecutor(new TreeBrush());
        getCommand("/terraform").setExecutor(new Terraform());
        getServer().getPluginManager().registerEvents(new UpdateNotification(), this);
        getConfig().options().copyDefaults(true);
        saveConfig();
        new Metrics(this, 20042);
        getLogger().info("\033[0;35m" + "Searching schematics..." + "\033[0m");
        new SchemCollector();
        if (getConfig().getBoolean("UpdateCheckEnabled")) {
            Thread updateChecker = new Thread(new UpdateChecker(this));
            updateChecker.start();
        } else {
            getLogger().info("\033[0;31m" + "Update checking is disabled. Check for releases at https://github.com/DixieCyanide/BTEMoreEnhanced/releases." + "\033[0m");
        }
        getLogger().info("\033[0;92m" + "BTEMoreEnhanced enabled!" + "\033[0m");
    }
}
