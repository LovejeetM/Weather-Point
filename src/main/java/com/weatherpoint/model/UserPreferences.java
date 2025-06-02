package com.weatherpoint.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserPreferences {
    private String temperatureUnit = "°C";
    private String windSpeedUnit = "km/h";

    private List<String> favoriteLocations;
    private boolean receiveAlerts;
    private String theme = "Light";

    public UserPreferences() {
        
        this.favoriteLocations = new CopyOnWriteArrayList<>();
        this.receiveAlerts = true;
    }

    public String getTemperatureUnit() { return temperatureUnit; }
    public void setTemperatureUnit(String unit) {
        if ("°C".equals(unit) || "°F".equals(unit)) {
            this.temperatureUnit = unit;
            
        }
    }
    public String getWindSpeedUnit() { return windSpeedUnit; }
    public void setWindSpeedUnit(String unit) {
         if ("km/h".equals(unit) || "mph".equals(unit)) {
            this.windSpeedUnit = unit;

        }
    }

    public List<String> getFavoriteLocations() {
        return new ArrayList<>(favoriteLocations);
    }

    public void addFavoriteLocation(String location) {
        if (location != null && !location.trim().isEmpty() && !favoriteLocations.contains(location.trim())) {
            favoriteLocations.add(location.trim());
            
        }
    }

    public void removeFavoriteLocation(String location) {
         if (location != null) {
            favoriteLocations.remove(location.trim());

         }
    }

    public boolean isReceiveAlerts() { return receiveAlerts; }
    public void setReceiveAlerts(boolean receive) {
        this.receiveAlerts = receive;

    }

    public String getTheme() { return theme; }
    public void setTheme(String theme) {
         if ("Light".equals(theme) || "Dark".equals(theme)) {
            this.theme = theme;

        }
    }
}