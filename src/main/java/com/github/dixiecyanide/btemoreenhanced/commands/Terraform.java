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

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;

public class Terraform implements TabExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final Plugin plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static Integer defaultTopRemove = plugin.getConfig().getInt("DefaultTopRemove");
    private static Integer defaultBotRemove = plugin.getConfig().getInt("DefaultBotRemove");
    private EditSession editSession;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("btemoreenhanced.region.terraform") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "Specify terrain height.");
            return true;
        }
        for (String arg : args) {
            try {
                Integer.valueOf(arg);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + "Inputs must be integers.");
                return true;
            }
        }

        Player player = (Player) commandSender;
        com.sk89q.worldedit.entity.Player p = new BukkitPlayer((WorldEditPlugin) we, player);
        List<Integer> argsList = new ArrayList<>();
        argsList.add(Integer.parseInt(args[0]) - 1);
        
        // In case if defaults are disabled
        if (defaultBotRemove < 0) {                                              
            defaultBotRemove = 0;
        }
        if (defaultTopRemove < 0) {
            defaultTopRemove = 0;
        } 

        switch (args.length) {
            case 1:
                argsList.add(defaultBotRemove);
                argsList.add(defaultTopRemove);
                break;
            case 2:
                argsList.add(Integer.parseInt(args[1]));
                argsList.add(defaultTopRemove);
                break;
            case 3:
                argsList.add(Integer.parseInt(args[1]));
                argsList.add(Integer.parseInt(args[2]));
                break;
            default:
                // I mean there can't be less than 0 arguments...    
                commandSender.sendMessage(ChatColor.RED + "Too many arguments.");                             
                break;
        }
        
        WorldEdit worldEdit = WorldEdit.getInstance();
        SessionManager manager = worldEdit.getSessionManager();
        LocalSession localSession = manager.get(p);
        Region region;
        World selectionWorld = localSession.getSelectionWorld();
        
        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException e) {
            commandSender.sendMessage(ChatColor.RED + "Please make a region selection first.");
            return true;
        }

        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion reg = (Polygonal2DRegion) region;
            Integer[] ogVertBorders = {reg.getMaximumY(), reg.getMinimumY()};

            editSession = WorldEdit.getInstance().newEditSession(p.getWorld());
            editSession.setMask(localSession.getMask());
            
            // This thing is kinda order-dependent if selection is 1 block high.
            // So you need to "stretch" selection from top if desired height is higher
            // and vise versa. You should not assign minimumY first if it will be higher than highest point
            // (maximumY) of initial selection. I hope it's undestandable.

            if (reg.getMaximumY() < (argsList.get(0) + argsList.get(2))) {
                reg.setMaximumY(argsList.get(0) + argsList.get(2));
                reg.setMinimumY(argsList.get(0) - argsList.get(1));
            } else {
                reg.setMinimumY(argsList.get(0) - argsList.get(1));
                reg.setMaximumY(argsList.get(0) + argsList.get(2));
            }

            try {
                editSession.setBlocks(region, BlockTypes.AIR.getDefaultState());
                reg.setMaximumY(argsList.get(0));
                editSession.setBlocks(region, BlockTypes.STONE.getDefaultState());
                reg.setMinimumY(argsList.get(0));
                editSession.setBlocks(region, BlockTypes.EMERALD_BLOCK.getDefaultState());
            } catch (MaxChangedBlocksException e) {
                commandSender.sendMessage(ChatColor.RED + "Number of blocks changed exceeds limit set.");
                return true;
            }

            if(reg.getMaximumY() < (ogVertBorders[0])) {
                reg.setMaximumY(ogVertBorders[0]);
                reg.setMinimumY(ogVertBorders[1]);
            } else {
                reg.setMinimumY(ogVertBorders[1]);
                reg.setMaximumY(ogVertBorders[0]);
            }
        } else if (region instanceof CuboidRegion) {
            CuboidRegion reg = (CuboidRegion) region;
            CuboidRegion ogReg = reg.clone();

            editSession = WorldEdit.getInstance().newEditSession(p.getWorld());
            editSession.setMask(localSession.getMask());

            //max
            reg.setPos1(BlockVector3.at(ogReg.getPos1().getX(), argsList.get(0) + argsList.get(2), ogReg.getPos1().getZ()));
            //min
            reg.setPos2(BlockVector3.at(ogReg.getPos2().getX(), argsList.get(0) - argsList.get(1), ogReg.getPos2().getZ()));

            try {
                editSession.setBlocks(region, BlockTypes.AIR.getDefaultState());
                reg.setPos1(BlockVector3.at(ogReg.getPos1().getX(), argsList.get(0), ogReg.getPos1().getZ()));
                editSession.setBlocks(region, BlockTypes.STONE.getDefaultState());
                reg.setPos2(BlockVector3.at(ogReg.getPos2().getX(), argsList.get(0), ogReg.getPos2().getZ()));
                editSession.setBlocks(region, BlockTypes.EMERALD_BLOCK.getDefaultState());
            } catch (MaxChangedBlocksException e) {
                commandSender.sendMessage(ChatColor.RED + "Number of blocks changed exceeds limit set.");
                return true;
            }

            reg.setPos1(ogReg.getPos1());
            reg.setPos2(ogReg.getPos2());
        } else {
            commandSender.sendMessage(ChatColor.RED + "Only cuboid and poly regions are supported.");
            return true;
        }

        localSession.remember(editSession);
        commandSender.sendMessage(ChatColor.LIGHT_PURPLE + "Terraforming complete.");

        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        
        switch (args.length) {
            case 1:
                Player player = (Player) commandSender;
                Integer playerHeight = player.getLocation().getBlockY();
                completions.add(playerHeight.toString());
                break;
            case 2:
                if (defaultBotRemove > -1) {
                    completions.add(defaultBotRemove.toString());
                }    
                break;
            case 3: 
                if (defaultTopRemove > -1) {
                    completions.add(defaultTopRemove.toString());
                } 
                break;
            default:
                break;
        }
        return completions;
    }

    public static void reloadConfig() {
        defaultTopRemove = plugin.getConfig().getInt("DefaultTopRemove");
        defaultBotRemove = plugin.getConfig().getInt("DefaultBotRemove");
    }
}