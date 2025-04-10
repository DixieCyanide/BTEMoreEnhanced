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


package com.github.dixiecyanide.btemoreenhanced.wood;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.logger.Logger;
import com.github.dixiecyanide.btemoreenhanced.schempicker.SchemBrush;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wood {
    private static final int N = 2;
    private final Player p;
    private CommandSender commandSender;
    private final String[] schemArgs;
    private List<String> schemDirs;
    private String[] targetBlocks;
    private float radius;
    private float radiusSum;
    private int schematicsOverMaxSize = 0;
    private int selectedBlocks = 0;
    private boolean ignoreAirBlocks = true;
    private boolean randomRotation = true;
    private boolean inverseMask = false;
    private EditSession editSession;
    private Tree[][] possibleVectorsGrid;
    private ArrayList<BlockVector3> startBlockVectors;
    private Tree[][] grid;
    private float cellSize;
    private int cellsWidth;
    private int cellsLength;
    private int prevX;
    private int prevZ;
    private int regWidth;
    private int regLenght;
    private final ArrayList<Tree> points = new ArrayList<>();
    private final ArrayList<Clipboard> schematics = new ArrayList<>();
    private BlockVector3 minPointCoords;
    private final Random random = new Random();
    private static final Plugin we = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
    private static final Plugin plugin = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private Logger chatLogger;
    private static String treepackFolder = plugin.getConfig().getString("TreepackFolder");
    private static File folderWE = new File(we.getDataFolder() + File.separator + "schematics" + File.separator + treepackFolder);
    private final int MAX_TRIES = plugin.getConfig().getInt("MaxTries");

    public Wood(Player p, CommandSender commandSender, String[] schemArgs, String target, String[] flags) {
        this.p = p;
        this.commandSender = commandSender;
        this.schemArgs = schemArgs;
        this.radius = Float.NaN;
        setTargetBlocks(target);
        chatLogger = bme.getBMEChatLogger();

        for (String flag : flags) {
            if (flag.equals("-includeAir")) {
                this.ignoreAirBlocks = false;
            } else if (flag.equals("-dontRotate")) {
                this.randomRotation = false;
            } else if (flag.startsWith("-r:")) {
                try {
                    this.radius = Float.parseFloat(flag.substring(flag.indexOf(':') + 1));
                } catch (Exception e) {
                    commandSender.sendMessage(ChatColor.RED + "Radius is not a number.");
                    return;
                }
            }
        }
    }

    public Wood(Player p, CommandSender commandSender, String[] schemArgs, String target) {
        this.p = p;
        this.commandSender = commandSender;
        this.schemArgs = schemArgs;
        this.radius = Float.NaN;
        setTargetBlocks(target);
        chatLogger = bme.getBMEChatLogger();
    }

    public void execute() {
        final long startTime = System.nanoTime();
        WorldEdit worldEdit = WorldEdit.getInstance();
        SessionManager manager = worldEdit.getSessionManager();
        LocalSession localSession = manager.get(p);
        Region region;
        World selectionWorld = localSession.getSelectionWorld();
        SchemBrush schemBrush = new SchemBrush(schemArgs);

        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            chatLogger.warning(commandSender, "bme.no-selection", null);
            return;
        }
        if(!(region instanceof Polygonal2DRegion)) {
            chatLogger.warning(commandSender, "bme.wood.selection.wrong-selection", null);
            return;
        }

        schemDirs = schemBrush.argsProcessing(p.getUniqueId(), true);
        List<String> presentSchems = new ArrayList<String>();

        for (String schemDir : schemDirs) {
            String schemName = schemDir.substring(schemDir.lastIndexOf(File.separator) + 1);

            if (presentSchems.contains(schemName)) {
                continue;
            }
            presentSchems.add(schemName);
            File file = new File(folderWE + schemDir);
            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            ClipboardReader reader;

            if (file.length() > plugin.getConfig().getInt("MaxSchemSize")) {
                chatLogger.warning(commandSender, "bme.wood.max-size", null);
                continue;
            }

            try {
                if (format != null) {
                    reader = format.getReader(new FileInputStream(file));
                    clipboard = reader.read();
                    radiusSum += radius(clipboard);
                    schematics.add(clipboard);
                }
            } catch (Exception e) {
                chatLogger.warning(commandSender, "bme.wood.damaged", null);
                continue;
            }
        }

        if (Float.isNaN(radius)) radius = radiusSum / schematics.size();

        regWidth = region.getWidth();
        regLenght = region.getLength();
        BlockVector3 minimumPoint = region.getMinimumPoint();

        possibleVectorsGrid = new Tree[regWidth][regLenght];
        
        for (int i = 0; i < regWidth; i++) {
            for (int j = 0; j < regLenght; j++) {
                possibleVectorsGrid[i][j] = null;
            }
        }

        minPointCoords = BlockVector3.at(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ());
        editSession = localSession.createEditSession(p);
        editSession.setMask(localSession.getMask());
        
        startBlockVectors = new ArrayList<>();
        prevX = 0;
        prevZ = 0;

        List<BlockVector3> regionList = new ArrayList<>();
        region.iterator().forEachRemaining(regionList::add);
        
        Integer regionListSize = regionList.size();
        if (regionListSize < 0){
            chatLogger.warning(commandSender, "bme.wood.selection.small", null);
            return;
        }
        Integer randomStartIndex = random.nextInt(regionList.size());

        for (Integer i = randomStartIndex; i < regionListSize; i++) {
            makePossibleVectorsGrid(regionList, i);
        }

        for (Integer i = 0; i < randomStartIndex; i++) {                        // moving from randomStartIndex towards 0 results in bunches of trees on one edge
            makePossibleVectorsGrid(regionList, i);
        }

        if (selectedBlocks == 0) {
            chatLogger.warning(commandSender, "bme.wood.not-found", null);
            return;
        }
        
        cellSize = (float) Math.floor(radius / Math.sqrt(N));
        cellsWidth = (int) (Math.ceil(regWidth / cellSize) + 1);
        cellsLength = (int) Math.ceil(regLenght / cellSize) + 1;
        grid = new Tree[cellsWidth][cellsLength];
        
        for (int i = 0; i < cellsWidth; i++) {
            for (int j = 0; j < cellsLength; j++) {
                grid[i][j] = null;
            }
        }

        for (BlockVector3 point : startBlockVectors) {
            points.addAll(poissonDiskSampling(MAX_TRIES, new Tree(point, randomSchematic()), regWidth, regLenght));
        }

        for (Tree tree : points) {
            BlockVector3 pos = BlockVector3.at(tree.getX(), tree.getY() + 1, tree.getZ());
            ClipboardHolder clipboardHolder = new ClipboardHolder(tree.getClipboard());

            if (randomRotation) {
                AffineTransform transform = new AffineTransform();
                Integer rotateAngle = random.nextInt(4) * 90;
                clipboardHolder.setTransform(transform.rotateY(rotateAngle));
            }

            PasteBuilder pb = clipboardHolder.createPaste(editSession).to(pos).ignoreAirBlocks(ignoreAirBlocks);

            try {
                Operations.completeLegacy(pb.build());
            } catch (MaxChangedBlocksException e) {
                chatLogger.error(commandSender, "bme.limit", null);
                return;
            }
        }
        localSession.remember(editSession);
        commandSender.sendMessage(ChatColor.DARK_PURPLE + "Done! " + points.size() + " trees pasted. " + schematics.size() + " schematics in pool. " + selectedBlocks + " blocks matched mask. " + (schematicsOverMaxSize == 0 ? "" : schematicsOverMaxSize + " schematics too large."));
        commandSender.sendMessage(ChatColor.DARK_PURPLE + "Took " + (System.nanoTime() - startTime) / 1e6 + " milliseconds.");
    }

    private void makePossibleVectorsGrid(List<BlockVector3> regionList, Integer i) {
        BlockVector3 position = regionList.get(i);
        BlockType block = editSession.getBlock(position).getBlockType();

        if ((block.getMaterial().isAir()) || !hasAirAbove(position) || matchesTarget(block) == inverseMask) {
            return;                                                                                           // just skip if position does not match the criteria
        }

        int x = Math.abs((int) (position.getX() - minPointCoords.getX()));                                    // relative coord in selection
        int z = Math.abs((int) (position.getZ() - minPointCoords.getZ()));

        if (startBlockVectors.size() == 0) {
            startBlockVectors.add(position);
            possibleVectorsGrid[x][z] = new Tree(position);
            selectedBlocks++;
            return;
        }
        Tree tree = possibleVectorsGrid[x][z];

        if (tree == null || tree.getY() < (int) position.getY()) {
            int xDist = x - prevX;                                                                            // distance between current and previous trees
            int zDist = z - prevZ;
            // Allowing trees to be 1 closer produces better results in testing
            int radius2 = (int) (radius - 1);

            if (zDist > radius2 || xDist >radius2) {
                startBlockVectors.add(position);
            } else if ((xDist * xDist + zDist * zDist) + 1 > radius2 * radius2) {
                if (!isAdjacentToExistingPoint(x, z, regWidth, regLenght)) {
                    startBlockVectors.add(position);
                }
            }

            possibleVectorsGrid[x][z] = new Tree(position);
            selectedBlocks++;
            prevX = x;
            prevZ = z;
        }
    }

    private ArrayList<Tree> poissonDiskSampling(int k, Tree startingPoint, int width, int height) {
        ArrayList<Tree> points = new ArrayList<>();
        ArrayList<Tree> active = new ArrayList<>();
        /*
            This seems to provide okay spacing. With Bridson's algorithm, changing the spacing per tree is not possible.
            Using trees with a wide range of widths/lengths may result in trees too close or far from each other.
         */
        points.add(startingPoint);
        active.add(startingPoint);
        insertPoint(cellSize, startingPoint);

        while (active.size() > 0) {
            int randomIndex = random.nextInt(active.size());
            BlockVector3 position = active.get(randomIndex).getBlockVector();
            Clipboard randomClipboard = randomSchematic();
            boolean found = false;

            for (int tries = 0; tries < k; tries++) {
                float theta = random.nextFloat() * 360;
                float newRadius = radius + random.nextFloat() * (2 * radius - radius);
                int x = Math.abs((int) (position.getX() - minPointCoords.getX()));
                int z = Math.abs((int) (position.getZ() - minPointCoords.getZ()));
                float newX = (float) (x + newRadius * Math.cos(Math.toRadians(theta)));
                float newZ = (float) (z + newRadius * Math.sin(Math.toRadians(theta)));

                if (!isValidPoint(cellSize, (int) newX, (int) newZ, width, height)) {
                    continue;
                }

                Tree tree = possibleVectorsGrid[(int) newX][(int) newZ];
                tree = new Tree(tree.getBlockVector(), randomClipboard);
                points.add(tree);
                insertPoint(cellSize, tree);
                active.add(tree);
                found = true;
                break;
            }
            /* If no point was found after k tries, remove p */
            if (!found) {
                active.remove(randomIndex);
            }
        }
        return points;
    }

    private void insertPoint(float cellSize, Tree tree) {
        int x = Math.abs((int) (tree.getBlockVector().getX() - minPointCoords.getX()));
        int z = Math.abs((int) (tree.getBlockVector().getZ() - minPointCoords.getZ()));
        int xIndex = (int) Math.floor(x / cellSize);
        int zIndex = (int) Math.floor(z / cellSize);
        grid[xIndex][zIndex] = tree;
    }

    private boolean isValidPoint(float cellSize, int x, int z, int width, int height) {
        /* Make sure the point is in the region */
        if (x < 0 || z < 0 || x >= width || z >= height) {
            return false;
        }
        if (possibleVectorsGrid[x][z] == null) {
            return false;
        }
        /* Check neighboring eight cells */
        int xIndex = (int) Math.floor(x / cellSize);
        int zIndex = (int) Math.floor(z / cellSize);
        int i0 = Math.max(xIndex - 1, 0);
        int i1 = Math.min(xIndex + 1, cellsWidth - 1);
        int j0 = Math.max(zIndex - 1, 0);
        int j1 = Math.min(zIndex + 1, cellsLength - 1);

        for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
                if (grid[i][j] != null) {
                    if (distance(Math.abs(grid[i][j].getX() - minPointCoords    // i hate it by just looking at it, but it looks important so i won't touch it
                    .getX()), Math.abs(grid[i][j].getZ() - minPointCoords
                    .getZ()), x, z) < radius) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isAdjacentToExistingPoint(int x, int z, int width, int height) {
        // Allowing trees to be 1 closer produces better results in testing
        int i0 = (int) Math.max(x - radius + 1, 0);
        int i1 = (int) Math.min(x + radius - 1, width - 1);
        int j0 = (int) Math.max(z - radius + 1, 0);
        int j1 = (int) Math.min(z + radius - 1, height - 1);
        for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
                if (possibleVectorsGrid[i][j] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private Clipboard randomSchematic() {
        return schematics.get(random.nextInt(schematics.size()));
    }
    
    private void setTargetBlocks(String target) {
        if (target.startsWith("!") && target.length() > 1) {
            this.targetBlocks = target.substring(1).split(",");
            this.inverseMask = true;
        } else {
            this.targetBlocks = target.split(",");
        }
    }

    private boolean matchesTarget(BlockType block) {
        for (String targetBlock : targetBlocks) {
            String blockID = block.getId().substring(10); // substring removes "minecratf:" thingy
            if (blockID.equals(targetBlock)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAirAbove(BlockVector3 blockPos) {
        BlockVector3 blockAbove = BlockVector3.at(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
        if (editSession.getBlock(blockAbove).getBlockType().getMaterial().isAir()) {
            return true;
        } else {
            return false;
        }
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private static float radius(Clipboard clipboard) {
        return Math.max(clipboard.getRegion().getWidth() / 2f, clipboard.getRegion().getLength() / 2f) + 1;
    }
}
