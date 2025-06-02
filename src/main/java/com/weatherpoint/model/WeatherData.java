package com.weatherpoint.model;

public class WeatherData {
    private double temperature; // Celsius
    private double humidity; // Percentage
    private String condition; // e.g., "Clouds", "Rain"
    private double windSpeed; // Kilometers per hour
    private String location; // City, Country
    private String date; // Formatted date string
    private double feelsLike; // Celsius
    private double pressure; // hPa
    private double visibility; // Kilometers
    private String weatherIcon; // OpenWeatherMap icon code
    private String description; // e.g., "scattered clouds"
    private double tempMin; // Celsius
    private double tempMax; // Celsius

    public WeatherData(double temperature, double humidity, String condition, double windSpeed,
                       String location, String date, double feelsLike, double pressure,
                       double visibility, String weatherIcon, String description,
                       double tempMin, double tempMax) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
        this.windSpeed = windSpeed;
        this.location = location;
        this.date = date;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.visibility = visibility;
        this.weatherIcon = weatherIcon;
        this.description = description;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public String getCondition() { return condition; }
    public double getWindSpeed() { return windSpeed; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public double getFeelsLike() { return feelsLike; }
    public double getPressure() { return pressure; }
    public double getVisibility() { return visibility; }
    public String getWeatherIcon() { return weatherIcon; }
    public String getDescription() { return description; }
    public double getTempMin() { return tempMin; }
    public double getTempMax() { return tempMax; }


}