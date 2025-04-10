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


package com.github.dixiecyanide.btemoreenhanced.events;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.userdata.*;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CheckJoinPlayerUd implements Listener {
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static UdUtils ud = bme.getUdUtils();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (ud.isThereUd(id) == false) {
            ud.createUd(id);
            ud.writeDefaultUd(id);
        }
        bme.putOnlineUd(id, ud.getUd(id));
    }
}