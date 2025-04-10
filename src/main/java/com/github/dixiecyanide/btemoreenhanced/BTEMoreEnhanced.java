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
import com.github.dixiecyanide.btemoreenhanced.userdata.UdUtils;

import com.github.dixiecyanide.btemoreenhanced.logger.Logger;
import com.github.dixiecyanide.btemoreenhanced.events.CheckJoinPlayerUd;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BTEMoreEnhanced extends JavaPlugin {
    private Logger chatLogger;
    private UdUtils udUtils;
    private Map<UUID, Object> udMap = new HashMap<>();

    @Override
    public void onDisable() {
        getLogger().info("\033[0;35m" + "Goodbye!" + "\033[0m");
    }

    @Override
    public void onEnable() {
        try {
            Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
        } catch (Exception e) {
            getLogger().severe("Couldn't find FastAsyncWorldEdit plugin. Please check plugins.");
            Bukkit.getPluginManager().disablePlugin(BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class));
        }
        Integer serverVersion = Integer.valueOf(Bukkit.getBukkitVersion().substring(2, 4));
        saveDefaultConfig();
        getCommand("/wood").setExecutor(new WoodCommand());
        getCommand("btemoreenhanced-reload").setExecutor(new ReloadConfig());
        getCommand("/dellast").setExecutor(new DelLast());
        getCommand("/delfirst").setExecutor(new DelFirst());
        getCommand("/delpoint").setExecutor(new DelPoint());
        getCommand("/treebrush").setExecutor(new TreeBrush());
        getCommand("/terraform").setExecutor(new Terraform());
        getCommand("/bmesettings").setExecutor(new BMESettings());
        if (serverVersion >= 21) {
            getCommand("/reach").setExecutor(new Reach());
        } else {
            getLogger().warning("Unsupported server version for //reach command.");
        }
        getServer().getPluginManager().registerEvents(new UpdateNotification(), this);
        getConfig().options().copyDefaults(true);
        saveConfig();
        chatLogger = new Logger();
        udUtils = new UdUtils();
        udUtils.checkUdFolder();
        getServer().getPluginManager().registerEvents(new CheckJoinPlayerUd(), this);
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

    public Logger getBMEChatLogger() {
        return chatLogger;
    }

    public UdUtils getUdUtils() {
        return udUtils;
    }

    public Map<UUID, Object> getOnlineUdMap() {

        return udMap;
    }

    public Map<String, Object> getOnlineUd(UUID id) {
        return (Map<String, Object>) udMap.get(id);
    }

    public void putOnlineUd(UUID id, Map<String, Object> map) {
        if (udMap.containsKey(id)) {
            udMap.replace(id, map);
            return;
        }
        udMap.putIfAbsent(id, map);
    }

    public void removeOnlineUd(UUID id) {
        udMap.remove(id);
    }
}
