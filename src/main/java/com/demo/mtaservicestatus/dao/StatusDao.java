package com.demo.mtaservicestatus.dao;

import com.demo.mtaservicestatus.domain.DataHolder;
import com.demo.mtaservicestatus.domain.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


@Repository

/**
 * Class to retrieve data from MTA service alert API,
 * to monitor and get route status
 *
 * @author Weiyao Tang
 * @version 1.0
 */
public class StatusDao {

    /**
     * Get the realtime status of a route
     *
     * The status data would be fetched through calling mta service alert api
     *
     * @param routeName The name of route
     * @return The realTime status of the route
     * @author Weiyao Tang
     */
    public String getStatusByRouteName(String routeName) {
        try {
            URL url = new URL(DataHolder.getApiUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-api-key", DataHolder.getApiKey());

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            /** The response data of current service alerts in json format */
            String jsonResponse = response.toString();

            long currentTime = System.currentTimeMillis() / 1000;

            conn.disconnect();

            ObjectMapper objectMapper = new ObjectMapper();


            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            /** Traverse the json node */
            for (JsonNode entity : jsonNode.findValue("entity")) {
                if (entity.findValue("alert") == null) {
                    continue;
                }
                JsonNode alert = entity.findValue("alert");
                String alertType = alert.findValue("transit_realtime.mercury_alert").findValue("alert_type").asText();
                long disPlayBeforeActive = alert.findValue("transit_realtime.mercury_alert").findValue("display_before_active") == null ? 0 : alert.findValue("transit_realtime.mercury_alert").findValue("display_before_active").asLong();
                if (alert.findValue("active_period") == null) {
                    continue;
                }
                JsonNode activePeriod = alert.get("active_period");
                long startTime = activePeriod.findValue("start").asLong();
                long endTime = activePeriod.findValue("end") == null ? currentTime : activePeriod.findValue("end").asLong();

                /** Check if the alert type is delay, and the current timestamp is within active time period */
                if (alertType.equals("Delays") && currentTime >= startTime + disPlayBeforeActive && currentTime <= endTime) {
                    for (JsonNode informedEntity : alert.findValue("informed_entity")) {
                        /** Check if the alert's route name matches with the given route name */
                        if (informedEntity.findValue("route_id") != null && informedEntity.findValue("route_id").asText().equals(routeName)) {
                            return Status.DELAY.name();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.AS_PLANNED.name();
    }


    /**
     * Monitor the realTime status of all subway routes
     * <p>
     * The route would be added to delay set if it's currently experiencing delay
     *
     * @return A set of route lines that are currently experiencing delay
     * @author Weiyao Tang
     */
    public Set<String> monitorStatus() {
        Set<String> delaySet = new HashSet<>();
        long currentTime = System.currentTimeMillis() / 1000;
        try {
            URL url = new URL(DataHolder.getApiUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-api-key", DataHolder.getApiKey());
            int responseCode = conn.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            /** The response data of current service alerts in json format */
            String jsonResponse = response.toString();

            conn.disconnect();

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            for (JsonNode entity : jsonNode.findValue("entity")) {
                if (entity.findValue("alert") == null) {
                    continue;
                }
                JsonNode alert = entity.findValue("alert");
                String alertType = alert.findValue("transit_realtime.mercury_alert").findValue("alert_type").asText();
                long disPlayBeforeActive = alert.findValue("transit_realtime.mercury_alert").findValue("display_before_active") == null ? 0 : alert.findValue("transit_realtime.mercury_alert").findValue("display_before_active").asLong();
                if (alert.findValue("active_period") == null) {
                    continue;
                }
                JsonNode activePeriod = alert.findValue("active_period");
                long startTime = activePeriod.findValue("start").asLong();
                long endTime = activePeriod.findValue("end") == null ? currentTime : activePeriod.findValue("end").asLong();

                /** Check if current timestamp is within active time period */
                if (currentTime >= startTime + disPlayBeforeActive && currentTime <= endTime) {
                    for (JsonNode informedEntity : alert.findValue("informed_entity")) {

                        /** Get the route name of the alert */
                        if (informedEntity.findValue("route_id") != null) {
                            String route = informedEntity.findValue("route_id").asText();

                            /** Check is alert type is delay */
                            if (alertType.equals("Delays")) {
                                delaySet.add(route);
                            }

                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delaySet;
    }

}
