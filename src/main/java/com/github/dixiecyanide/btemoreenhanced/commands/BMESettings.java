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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;
import com.github.dixiecyanide.btemoreenhanced.logger.Logger;
import com.github.dixiecyanide.btemoreenhanced.logger.SettingsGUI;
import com.github.dixiecyanide.btemoreenhanced.schempicker.SchemCollector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.event.platform.CommandSuggestionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.command.CommandUtil;

public class BMESettings implements TabExecutor {
    private static final BTEMoreEnhanced bme = BTEMoreEnhanced.getPlugin(BTEMoreEnhanced.class);
    private static final Gson gson = new GsonBuilder().create();
    private static final Type STRING_MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private static SettingsGUI gui;
    private Map<String, String> strings;
    private Logger chatLogger;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        chatLogger = bme.getBMEChatLogger();
        if (!commandSender.hasPermission("btemoreenhanced.player.settings") && !commandSender.isOp()) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            chatLogger.error(commandSender, "bme.error.not-a-player", null);
            return true;
        }

        InputStream iStream = this.getClass().getClassLoader().getResourceAsStream("settingNames.json");
        Reader reader = new InputStreamReader(iStream, StandardCharsets.UTF_8);
        strings = gson.fromJson(reader, STRING_MAP_TYPE);
        Player player = (Player) commandSender;

        switch (args.length) {
            case 0:
                gui = new SettingsGUI(strings);
                gui.getSettings(commandSender);
            break;
            case 1:
                if (args[0].equals("reset")) {
                    bme.getUdUtils().writeDefaultUd(player.getUniqueId());
                    chatLogger.info(commandSender, "bme.info.settings.reset", null);
                }
            break;
            case 2:
                if (bme.getUdUtils().updateUd(player.getUniqueId(), args[0], args[1])) {
                    chatLogger.info(commandSender, "bme.info.settings.changed", null);
                }
            break;
            default:
                chatLogger.error(commandSender, "bme.error.invalid-arg", null);
            break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], new ArrayList<>(Arrays.asList("reset")), completions);
            return completions;
        }
        if (args.length != 2) {
            return null;
        }

        Actor actor = BukkitAdapter.adapt(commandSender);
        CommandSuggestionEvent suggestEvent;

        switch (args[0]) {
            case "TerrBlock":
                suggestEvent = new CommandSuggestionEvent(actor, "//set " + args[1]);
                WorldEdit.getInstance().getEventBus().post(suggestEvent);
                completions.addAll(CommandUtil.fixSuggestions("//set " + args[1], suggestEvent.getSuggestions()));
            break;
            case "TerrBiome":
                suggestEvent = new CommandSuggestionEvent(actor, "//setbiome " + args[1]);
                WorldEdit.getInstance().getEventBus().post(suggestEvent);
                completions.addAll(CommandUtil.fixSuggestions("//setbiome " + args[1], suggestEvent.getSuggestions()));
            case "UnusedTreepacks":
                ArrayList<String> entryKeys = new ArrayList<>();

                for (Map.Entry<String, List<String>> entry : SchemCollector.getDirectories().entrySet()) {
                    entryKeys.add(entry.getKey());
                }

                if (args[1].lastIndexOf(",") >= 0) {
                    for (Integer i = 0; i < entryKeys.size(); i++) {                    
                        String multiEntryKey = entryKeys.get(i);
                        entryKeys.set(i, args[1].substring(0, args[1].lastIndexOf(",") + 1) + multiEntryKey);
                    }
                    StringUtil.copyPartialMatches(args[1], entryKeys, completions);
                    return completions;
                }

                for (Map.Entry<String, List<String>> entry : SchemCollector.getDirectories().entrySet()) {
                    entryKeys.add(entry.getKey());
                }
                StringUtil.copyPartialMatches(args[1], entryKeys, completions);
                break;
            default:
            return null;
        }

        return completions;
    }
}
