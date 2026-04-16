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


package com.github.dixiecyanide.btemoreenhanced.userdata;

import java.util.List;  

public class Userdata {
    private double reach;
    private TerraformConfig terraform;
    private List<String> unusedTreepacks;

    public Userdata() {}

    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }

    public TerraformConfig getTerraformConfig() {
        return terraform;
    }

    public void setTerraformConfig(TerraformConfig terraform) {
        this.terraform = terraform;
    }

    public List<String> getUnusedTreepacks() {
        return unusedTreepacks;
    }

    public void setUnusedTreepacks(List<String> unusedTreepacks) {
        this.unusedTreepacks = unusedTreepacks;
    }
}
