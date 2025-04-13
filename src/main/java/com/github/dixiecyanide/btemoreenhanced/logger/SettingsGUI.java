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

package com.github.dixiecyanide.btemoreenhanced.logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.userdata.UdUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SettingsGUI {
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static UdUtils udUtils = bme.getUdUtils();
    Map<String, String> strings;

    public SettingsGUI(Map<String, String> strings) {
        this.strings = strings;
    }

    public void getSettings(CommandSender commandSender) {
        Player player = (Player) commandSender;
        UUID id = player.getUniqueId();

        ArrayList<String> valueNames = new ArrayList<>();
        ArrayList<String> valueCodes = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : strings.entrySet()) {
            valueNames.add(entry.getValue());
            valueCodes.add(entry.getKey());
            values.add(udUtils.getOnlineUdValue(id, entry.getKey()).toString());
        }

        TextComponent msgHeader = new TextComponent("BTEMoreEnhanced settings.");
            msgHeader.setColor(ChatColor.DARK_PURPLE);
            msgHeader.setBold(false);
            msgHeader.setUnderlined(true);

        TextComponent msgValueName = new TextComponent();
            msgValueName.setColor(ChatColor.DARK_PURPLE);
            msgValueName.setBold(false);
            msgValueName.setUnderlined(false);
        
        TextComponent msgValue = new TextComponent();
            msgValue.setColor(ChatColor.WHITE);
            msgValue.setBold(true);
            msgValue.setUnderlined(false);

        TextComponent msgChangeBtn = new TextComponent(" Change value");
            msgChangeBtn.setColor(ChatColor.GREEN);
            msgChangeBtn.setBold(false);
            msgChangeBtn.setUnderlined(false);
            msgChangeBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                                       new Text("Click to enter new value")));

        TextComponent resetButton = new TextComponent("\nReset");
            resetButton.setColor(ChatColor.RED);
            resetButton.setBold(false);
            resetButton.setUnderlined(false);
            resetButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                      new Text("Click to reset setting to default values.")));
            resetButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                      "//bmesettings reset"));

        for (Integer i = 0; i < 6; i++) {
            msgValueName.setText("\n- " + valueNames.get(i) + ": ");
            msgValue.setText(values.get(i));
            msgChangeBtn.setClickEvent
                (new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                ("//settings " + valueCodes.get(i) + " ")));
            msgHeader.addExtra(msgValueName.duplicate());
            msgHeader.addExtra(msgValue.duplicate());
            msgHeader.addExtra(msgChangeBtn.duplicate());
            }
        msgHeader.addExtra(resetButton.duplicate());
        commandSender.spigot().sendMessage(msgHeader);
    }
}
