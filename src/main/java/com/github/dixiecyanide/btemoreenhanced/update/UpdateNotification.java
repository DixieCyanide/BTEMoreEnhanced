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


package com.github.dixiecyanide.btemoreenhanced.update;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotification implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            BTEMoreEnhanced bme = (BTEMoreEnhanced) Bukkit.getPluginManager().getPlugin("BTEMoreEnhanced");
            UpdateChecker uc = new UpdateChecker(bme);
            if (!uc.isLatestVersion()) {
                String versions = String.format("You are using version %s, newest version is %s", uc.getCurrent(), uc.getLatest());
                player.sendMessage(ChatColor.DARK_PURPLE + "BTEMoreEnhanced is outdated!");
                player.sendMessage(ChatColor.DARK_PURPLE + versions);
                player.sendMessage(ChatColor.DARK_PURPLE + "Download update here: https://github.com/DixieCyanide/BTEMoreEnhanced/releases");
            }
        }
    }
}
