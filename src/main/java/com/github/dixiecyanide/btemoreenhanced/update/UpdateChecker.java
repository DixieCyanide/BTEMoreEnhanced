/*
 * BTEMoreEnhanced, a building tool
 * Copyright 2023 (C) DixieCyanide
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

package com.github.dixiecyanide.btemoreenhanced.update;

import com.github.dixiecyanide.btemoreenhanced.BTEMoreEnhanced;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class UpdateChecker implements Runnable {
    private final Logger logger;
    private String current;
    private String latest;

    public UpdateChecker(BTEMoreEnhanced bteMoreEnhanced) {
        this.logger = bteMoreEnhanced.getLogger();
        this.current = cleanVersion(bteMoreEnhanced.getDescription().getVersion());
        this.latest = getLatestVersion();
    }

    @Override
    public void run() {
        logger.info("\033[0;35m" + "-----CHECKING FOR UPDATES-----" + "\033[0m");
        logger.info("\033[0;35m" + "Current version: " + current + "\033[0m");
        logger.info("\033[0;35m" + "Latest version: " + latest + "\033[0m");
        if (!isLatestVersion()) {
            logger.info("\033[0;31m" + "Plugin is not latest! Is it outdated? https://github.com/DixieCyanide/BTEMoreEnhanced/releases" + "\033[0m");
        } else {
            logger.info("\033[0;92m" + "Plugin is up to date." + "\033[0m");
        }
        logger.info("\033[0;35m" + "------------------------------" + "\033[0m");
    }

    public Boolean isLatestVersion() {
        latest = getLatestVersion();
        if (!current.equals(latest)) {
            return false;
        } else {
            return true;
        }
    }

    public String getCurrent() {
        return this.current;
    }

    public String getLatest() {
        return this.latest;
    }

    private String getLatestVersion() {
        String latestVersion = "NOT FOUND";
        try {
            URL url = new URL("https://api.github.com/repos/DixieCyanide/BTEMoreEnhanced/releases");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            int code = con.getResponseCode();
            if (code >= 200 && code <= 299) {
                JSONArray jsonObject = (JSONArray) JSONValue.parse(response.toString());
                latestVersion = cleanVersion((String) ((JSONObject) jsonObject.get(0)).get("tag_name"));
            } else {
                logger.severe("Request for latest release not successful. Response code: " + code);
            }
        } catch (Exception e) {
            logger.severe("Unexpected error occurred while getting latest release version number.");
            e.printStackTrace();
        }
        return latestVersion;
    }

    private static String cleanVersion(String version) {
        return version.replaceAll("[^0-9.]", "");
    }
}
