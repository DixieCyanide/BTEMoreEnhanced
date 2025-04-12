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
import com.github.dixiecyanide.btemoreenhanced.logger.Logger;
import com.github.dixiecyanide.btemoreenhanced.userdata.UdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.command.BiomeCommands;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockTypes;

public class Terraform implements TabExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static UdUtils udUtils;
    private Integer botRemove;
    private Integer topRemove;
    private String block;
    private String biome;
    private EditSession editSession;
    private Logger chatLogger;
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        chatLogger = bme.getBMEChatLogger();
        if (!commandSender.hasPermission("btemoreenhanced.region.terraform") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            chatLogger.error(commandSender, "bme.error.not-a-player", null);
            return true;
        }
        if (args.length == 0) {
            chatLogger.error(commandSender, "bme.error.terraform.no-height", null);
            return true;
        }
        if (args.length > 3) {
            chatLogger.error(commandSender, "bme.error.too-many-args", null);
            return true;
        }
        for (String arg : args) {
            try {
                Integer.valueOf(arg);
            } catch (NumberFormatException e) {
                chatLogger.error(commandSender, "bme.error.not-an-integer", null);
                return true;
            }
        }

        Player player = (Player) commandSender;
        com.sk89q.worldedit.entity.Player p = new BukkitPlayer((WorldEditPlugin) we, player);
        List<Integer> argsList = new ArrayList<>();
        argsList.add(Integer.parseInt(args[0]) - 1);
        udUtils = bme.getUdUtils();
        UUID id = player.getUniqueId();

        topRemove = Integer.parseInt(udUtils.getOnlineUdValue(id, "TerrTop").toString());
        botRemove = Integer.parseInt(udUtils.getOnlineUdValue(id, "TerrBot").toString());
        block = udUtils.getOnlineUdValue(id, "TerrBlock").toString();
        biome = udUtils.getOnlineUdValue(id, "TerrBiome").toString();
        
        // In case if defaults are disabled
        if (botRemove < 0) {                                              
            botRemove = 0;
        }
        if (topRemove < 0) {
            topRemove = 0;
        } 

        switch (args.length) {
            case 1:
                argsList.add(botRemove);
                argsList.add(topRemove);
                break;
            case 2:
                argsList.add(Integer.parseInt(args[1]));
                argsList.add(topRemove);
                break;
            case 3:
                argsList.add(Integer.parseInt(args[1]));
                argsList.add(Integer.parseInt(args[2]));
                break;
            default:
                // I mean there can't be less than 0 arguments...    
                chatLogger.error(commandSender, "bme.error.too-many-args", null);                        
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
            chatLogger.warning(commandSender, "bme.warn.no-selection", null);
            return true;
        }

        editSession = localSession.createEditSession(p);
        editSession.setMask(localSession.getMask());

        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion reg = (Polygonal2DRegion) region;
            Integer[] ogVertBorders = {reg.getMaximumY(), reg.getMinimumY()};
            
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
                editSession.setBlocks(region, BlockTypes.get(block).getDefaultState());
            } catch (MaxChangedBlocksException e) {
                chatLogger.error(commandSender, "bme.error.limit", null);
                return true;
            }

            if (!biome.equals("none")) {
                BiomeCommands bc = new BiomeCommands();
                reg.setMaximumY(10000); // such values so biome will be from top to bottom 
                reg.setMinimumY(-1000);
                
                try {
                    bc.setBiome(p, selectionWorld, localSession, editSession, BiomeTypes.get(biome), false);
                } catch (WorldEditException e) {
                    // error
                }
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

            //max
            reg.setPos1(BlockVector3.at(ogReg.getPos1().getX(), argsList.get(0) + argsList.get(2), ogReg.getPos1().getZ()));
            //min
            reg.setPos2(BlockVector3.at(ogReg.getPos2().getX(), argsList.get(0) - argsList.get(1), ogReg.getPos2().getZ()));

            try {
                editSession.setBlocks(region, BlockTypes.AIR.getDefaultState());
                reg.setPos1(BlockVector3.at(ogReg.getPos1().getX(), argsList.get(0), ogReg.getPos1().getZ()));
                editSession.setBlocks(region, BlockTypes.STONE.getDefaultState());
                reg.setPos2(BlockVector3.at(ogReg.getPos2().getX(), argsList.get(0), ogReg.getPos2().getZ()));
                editSession.setBlocks(region, BlockTypes.get(block).getDefaultState());
            } catch (MaxChangedBlocksException e) {
                chatLogger.error(commandSender, "bme.error.limit", null);
                return true;
            }
            
            if (!biome.equals("none")) {
                BiomeCommands bc = new BiomeCommands();
                reg.setPos1(BlockVector3.at(ogReg.getPos1().getX(), 10000, ogReg.getPos1().getZ()));
                reg.setPos2(BlockVector3.at(ogReg.getPos2().getX(), -1000, ogReg.getPos2().getZ()));

                try {
                    bc.setBiome(p, selectionWorld, localSession, editSession, BiomeTypes.get(biome), false);
                } catch (WorldEditException e) {
                    // wrong biome name error
                }
            }

            reg.setPos1(ogReg.getPos1());
            reg.setPos2(ogReg.getPos2());
        } else {
            chatLogger.warning(commandSender, "bme.warn.terraform.wrong-selection", null);
            return true;
        }

        localSession.remember(editSession);
        chatLogger.info(commandSender, "bme.info.terraform.complete", null);

        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String label, String[] args) {
        final List<String> completions = new ArrayList<>();
        Player player = (Player) commandSender;
        udUtils = bme.getUdUtils();
        UUID id = player.getUniqueId();

        topRemove = Integer.parseInt(udUtils.getOnlineUdValue(id, "TerrTop").toString());
        botRemove = Integer.parseInt(udUtils.getOnlineUdValue(id, "TerrBot").toString());

        switch (args.length) {
            case 1:
                Integer playerHeight = player.getLocation().getBlockY();
                completions.add(playerHeight.toString());
                break;
            case 2:
                if (botRemove > -1) {
                    completions.add(botRemove.toString());
                }    
                break;
            case 3: 
                if (topRemove > -1) {
                    completions.add(topRemove.toString());
                } 
                break;
            default:
                break;
        }
        return completions;
    }
}