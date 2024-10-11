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


package com.github.dixiecyanide.btemoreenhanced.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class Reach implements TabExecutor{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("btemoreenhanced.player.reach") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        if (args.length > 1) {
            commandSender.sendMessage(ChatColor.RED + "Too many arguments.");
            return true;
        }

        Player player = (Player) commandSender;

        AttributeInstance blockReachAtt = player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE);
        AttributeInstance entityReachAtt = player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
        
        if (args.length == 0) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                blockReachAtt.setBaseValue(5);
            } else {
                blockReachAtt.setBaseValue(4.5);
            }                                            
            entityReachAtt.setBaseValue(3.5);
            commandSender.sendMessage(ChatColor.DARK_PURPLE + "Changed reach distance to default values.");
            return true;
        }

        try {
            blockReachAtt.setBaseValue(Double.valueOf(args[0]));
            entityReachAtt.setBaseValue(Double.valueOf(args[0]));
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Inputs must be numbers.");
            return true;
        }

        commandSender.sendMessage(ChatColor.DARK_PURPLE + "Changed reach distance to " + args[0] + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
