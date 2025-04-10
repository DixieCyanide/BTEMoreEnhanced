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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

import javax.annotation.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Logger {
    private static final Gson gson = new GsonBuilder().create();
    private static final Type STRING_MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();
    private Map<String, String> strings;
    private String prefix;
    
    public Logger() {
        InputStream iStream = this.getClass().getClassLoader().getResourceAsStream("strings.json");
        Reader reader = new InputStreamReader(iStream, StandardCharsets.UTF_8);
        strings = gson.fromJson(reader, STRING_MAP_TYPE);
        prefix = (ChatColor.DARK_PURPLE + strings.get("prefix"));
    }

    public void info(CommandSender sender, String messageCode, @Nullable String argument){
        String msg = compose(messageCode, "info", argument);
        sender.sendMessage(msg);
    }

    public void warning(CommandSender sender, String messageCode, @Nullable String argument){
        String msg = compose(messageCode, "warning", argument);
        sender.sendMessage(msg);
    }

    public void error(CommandSender sender, String messageCode, @Nullable String argument){
        String msg = compose(messageCode, "error", argument);
        sender.sendMessage(msg);
    }

    private String compose(String messageCode, String messageType, @Nullable String argument){
        String msg = strings.get(messageCode);
        ChatColor color = ChatColor.WHITE;
        if (argument != null) {
            msg = String.format(msg, argument);
        }
        switch (messageType) {
            case "info":
                color = ChatColor.DARK_PURPLE;
                break;
            case "warning":
                color = ChatColor.YELLOW;
                break;
            case "error":
                color = ChatColor.RED;
                break;
            default:
                break;
        }
        String finalMsg = (prefix + color + msg);
        return finalMsg;
    }
}
