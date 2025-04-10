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
import com.github.dixiecyanide.btemoreenhanced.userdata.UdUtils;

public class Reach implements TabExecutor {
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static UdUtils udUtils = bme.getUdUtils();
    private Logger chatLogger;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        chatLogger = bme.getBMEChatLogger();
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
            Double value = 4.5;
            
            value = Double.parseDouble((String) udUtils.getOnlineUdValue(player.getUniqueId(), "Reach"));

            if (value != -1) {
                blockReachAtt.setBaseValue(value);
                entityReachAtt.setBaseValue(value);
                chatLogger.info(commandSender, "bme.reach.default", value.toString());
                return true;
            }

            if (player.getGameMode() == GameMode.CREATIVE) {
                value = 5.0;
            } else {
                value = 4.5;
            }
            blockReachAtt.setBaseValue(value);
            value = 3.5;                                          
            entityReachAtt.setBaseValue(value);
            chatLogger.info(commandSender, "bme.reach.default", value.toString());
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
