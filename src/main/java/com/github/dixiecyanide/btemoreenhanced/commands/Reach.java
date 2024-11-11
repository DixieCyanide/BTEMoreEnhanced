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

import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.logger.Logger;

public class Reach implements TabExecutor {
    private static final BTEMoreEnhanced plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private Logger chatLogger;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        chatLogger = plugin.getBMEChatLogger();
        if (!commandSender.hasPermission("btemoreenhanced.player.reach") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            chatLogger.error(commandSender, "bme.not-a-player", null);
            return true;
        }
        if (args.length > 1) {
            chatLogger.error(commandSender, "bme.too-many-args", null);
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
            chatLogger.info(commandSender, "bme.reach.default", null);
            return true;
        }

        try {
            blockReachAtt.setBaseValue(Double.valueOf(args[0]));
            entityReachAtt.setBaseValue(Double.valueOf(args[0]));
        } catch (NumberFormatException e) {
            chatLogger.error(commandSender, "bme.NaN", null);
            return true;
        }

        chatLogger.info(commandSender, "bme.reach.changed", args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
