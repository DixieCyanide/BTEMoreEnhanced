/*
 * BTEMoreEnhanced, a building tool
 * Copyright 2022 (C) DixieCyanide
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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DelPoint implements CommandExecutor {
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("btemoreenhanced.selection.delpoint") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;
        com.sk89q.worldedit.entity.Player p = new BukkitPlayer((WorldEditPlugin) we, player);

        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "You must specify the point in the selection you want to delete.");
            return true;
        }
        int numToDelete;
        try {
            numToDelete = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + "Point to delete must be an integer.");
            return true;
        }
        if (numToDelete < 0) {
            commandSender.sendMessage(ChatColor.RED + "You can't delete a negative point.");
            return true;
        } else if (numToDelete == 0) {
            commandSender.sendMessage(ChatColor.RED + "You can't delete the 0th point, there is none. Points are numbered from 1.");
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
            commandSender.sendMessage(ChatColor.RED + "Please make a region selection first.");
            return true;
        }

        if(!(region instanceof Polygonal2DRegion) && !(region instanceof ConvexPolyhedralRegion)) {
            commandSender.sendMessage(ChatColor.RED + "Only poly or convex regions are supported.");
            return true;
        }

        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion reg = (Polygonal2DRegion) region;
            List<BlockVector2> points = reg.getPoints();
    
            if (numToDelete > points.size()) {
                commandSender.sendMessage(ChatColor.RED + "Point does not exist. You can delete 1 - " + points.size());
                return true;
            }
    
            List<BlockVector2> newPoints = new ArrayList<>(points);
            newPoints.remove(numToDelete - 1);
            Polygonal2DRegionSelector regionSelector = new Polygonal2DRegionSelector(selectionWorld, newPoints, reg.getMinimumY(), reg.getMaximumY());
            localSession.setRegionSelector(selectionWorld, regionSelector);
            regionSelector.explainRegionAdjust(p, localSession);
        }

        if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion reg = (ConvexPolyhedralRegion) region;
            Collection<BlockVector3> verts = reg.getVertices();
            
            if (numToDelete > verts.size()) {
                commandSender.sendMessage(ChatColor.RED + "Point does not exist. You can delete 1 - " + (verts.size()));
                return true;
            }

            ConvexPolyhedralRegionSelector newRegion = new ConvexPolyhedralRegionSelector(selectionWorld);
            
            Integer i = -1;
            for (BlockVector3 vert : verts) {
                if (numToDelete == 1 && i == -1) {                              // if it's needed to delete 1st point, we just skip it as it never existed
                    i++;    
                    continue;
                }

                if (i == 0) {
                    newRegion.selectPrimary(vert, ActorSelectorLimits.forActor(p));
                } else if (i != (numToDelete - 1)) {
                    newRegion.selectSecondary(vert, ActorSelectorLimits.forActor(p));
                }
                i++;
            }
            localSession.setRegionSelector(selectionWorld, newRegion);
            newRegion.explainRegionAdjust(p, localSession);
        }
        commandSender.sendMessage(ChatColor.LIGHT_PURPLE + "Point " + numToDelete + " deleted!");
        return true;        
    }
}
