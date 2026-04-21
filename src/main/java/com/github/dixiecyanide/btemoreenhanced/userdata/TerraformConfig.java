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

public class TerraformConfig {
    private String terrBlock;
    private String terrBiome;
    private Integer terrTop;
    private Integer terrBot;

    public TerraformConfig() {}

    public String getTerrBlock() {
        return terrBlock;
    }

    public void setTerrBlock(String terrBlock) {
        if (terrBlock.contains(":")) {
            terrBlock = terrBlock.substring(10);
        }
        this.terrBlock = terrBlock;
    }

    public String getTerrBiome() {
        return terrBiome;
    }

    public void setTerrBiome(String terrBiome) {
        this.terrBiome = terrBiome;
    }

    public Integer getTerrTop() {
        return terrTop;
    }

    public void setTerrTop(Integer terrTop) {
        this.terrTop = terrTop;
    }

    public Integer getTerrBot() {
        return terrBot;
    }

    public void setTerrBot(Integer terrBot) {
        this.terrBot = terrBot;
    }
}
