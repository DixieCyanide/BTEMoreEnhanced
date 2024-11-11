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

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

public class DelFirst implements CommandExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final BTEMoreEnhanced plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private Logger chatLogger;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        chatLogger = plugin.getBMEChatLogger();
        if (!commandSender.hasPermission("btemoreenhanced.selection.delfirst") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            chatLogger.error(commandSender, "bme.not-a-player", null);
            return true;
        }
        Player player = (Player) commandSender;
        com.sk89q.worldedit.entity.Player p = new BukkitPlayer((WorldEditPlugin) we, player);

        int numToDelete = 1;
        if (args.length > 0) {
            try {
                numToDelete = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                chatLogger.error(commandSender, "bme.not-an-integer", null);
                return true;
            }
        }
        if (numToDelete == 0) {
            chatLogger.error(commandSender, "bme.selection.delete.zero", null);
            return true;
        } else if (numToDelete < 0) {
            chatLogger.error(commandSender, "bme.selection.delete.negative", null);
            return true;
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
            chatLogger.warning(commandSender, "bme.no-selection", null);
            return true;
        }

        if(!(region instanceof Polygonal2DRegion) && !(region instanceof ConvexPolyhedralRegion)) {
            chatLogger.warning(commandSender, "bme.selection.wrong-selection", null);
            return true;
        }

        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion reg = (Polygonal2DRegion) region;
            List<BlockVector2> points = reg.getPoints();

            if (numToDelete > points.size() - 1) {
                chatLogger.error(commandSender, "bme.selection.delete.many", String.valueOf(points.size()));
                return true;
            }

            List<BlockVector2> newPoints = points.subList(numToDelete, points.size());
            Polygonal2DRegionSelector regionSelector = new Polygonal2DRegionSelector(selectionWorld, newPoints, reg.getMinimumY(), reg.getMaximumY());
            localSession.setRegionSelector(selectionWorld, regionSelector);
            regionSelector.explainRegionAdjust(p, localSession);
        }
        
        if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion reg = (ConvexPolyhedralRegion) region;
            Collection<BlockVector3> verts = reg.getVertices();
            
            if (numToDelete > verts.size() - 1) {
                chatLogger.error(commandSender, "bme.selection.delete.many", String.valueOf(verts.size()));
                return true;
            }

            ConvexPolyhedralRegionSelector newRegion = new ConvexPolyhedralRegionSelector(selectionWorld);
            
            Integer i = 0;
            for (BlockVector3 vert : verts) {
                if (i < numToDelete) {
                    i++;
                    continue;
                } else if (i == numToDelete) {
                    newRegion.selectPrimary(vert, ActorSelectorLimits.forActor(p));
                } else if (i < (verts.size())) {
                    newRegion.selectSecondary(vert, ActorSelectorLimits.forActor(p));
                }
                i++;
            }
            localSession.setRegionSelector(selectionWorld, newRegion);
            newRegion.explainRegionAdjust(p, localSession);
        } 
        chatLogger.info(commandSender, "bme.selection.edited", null);
        return true;
    }
}
