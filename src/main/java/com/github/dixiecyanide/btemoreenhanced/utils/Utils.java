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

package com.github.dixiecyanide.btemoreenhanced.utils;

import java.util.ArrayList;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.registry.LegacyMapper;

public class Utils {
    private static LegacyMapper LM = LegacyMapper.getInstance();

    public static ArrayList<String> FixMultiLegacyID (ArrayList<String> idList)
        throws NumberFormatException, NullPointerException {
        ArrayList<String> fixedIdList = new ArrayList<String>();
        for (String id : idList) {
            try{
                fixedIdList.add(FixSingleLegacyID(id));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(id);
            } catch (NullPointerException e) {
                throw new NullPointerException(id);
            }
        }
        return fixedIdList;
    }

    public static String FixSingleLegacyID (String id) 
        throws NumberFormatException, NullPointerException {
        String fixedId = "";
        String legacyID = "0";
        String legacyData = "0";
        // zeroes are here cuz id = 0 is air, and data = 0 works for any block and gives default state
        // i.e: 2 and 2:0 will give same block of grass (grass_block in modern id system)
        // i.e: 17 and 17:0 will give same block of oak log in the same orientation, while 17:1 will give spruce log
        fixedId = id; // just returning modern id without any manipulations
        if (BlockTypes.get(id) == null) {    
            if (id.contains(":")){
                legacyID = id.substring(0, id.indexOf(":"));
                legacyData = id.substring(id.indexOf(":") + 1);
            } else {
                legacyID = id;
            }

            try {
                BlockState BS = LM.getBlockFromLegacy(Integer.parseInt(legacyID), Integer.parseInt(legacyData));
                BlockType BT = BS.getBlockType();
                fixedId = BT.getId().substring(10); // substring removes "minecratf:" thingy
            } catch (NumberFormatException e) {
                throw new NumberFormatException(id);
            } catch (NullPointerException e) {
                throw new NullPointerException(id);
            }
        }
        return fixedId;
    }
}
