package com.weatherpoint.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherpoint.model.WeatherData;

public class WeatherService {
    
    private static final String API_KEY = "ENTER YOUR KEY HERE - GET KEY FROM OPEN WEATHER MAP .ORG";   //    aKEY
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherData getWeatherForecast(String location, String date) {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            throw new RuntimeException("API Key is missing or invalid. Please set it in WeatherService.java");
        }

        String encodedLocation;
        try {
             encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
             throw new RuntimeException("Failed to encode location: " + location, e);
        }

        String url = String.format(API_URL, encodedLocation, API_KEY);
        HttpGet request = new HttpGet(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse httpResponse = httpClient.execute(request)) {

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(httpResponse.getEntity());

            if (statusCode >= 200 && statusCode < 300) {
                JsonNode rootNode = objectMapper.readTree(responseBody);


                if (rootNode.has("cod") && rootNode.path("cod").asInt() != 200 && rootNode.has("message")) {
                     throw new IOException("API Error: " + rootNode.path("message").asText());
                }


                JsonNode mainNode = rootNode.path("main");
                JsonNode weatherArray = rootNode.path("weather");
                JsonNode windNode = rootNode.path("wind");
                JsonNode sysNode = rootNode.path("sys");


                 if (weatherArray.isMissingNode() || !weatherArray.isArray() || weatherArray.isEmpty()) {
                    throw new IOException("Weather data section is missing or empty in API response.");
                }
                JsonNode weatherNode = weatherArray.get(0);

                String confirmedLocationName = rootNode.path("name").asText(location);
                if(sysNode.has("country")) {
                    confirmedLocationName += ", " + sysNode.path("country").asText();
                }


                double temperature = mainNode.path("temp").asDouble(Double.NaN);
                double feelsLike = mainNode.path("feels_like").asDouble(Double.NaN);
                double tempMin = mainNode.path("temp_min").asDouble(Double.NaN);
                double tempMax = mainNode.path("temp_max").asDouble(Double.NaN);
                double pressure = mainNode.path("pressure").asDouble(Double.NaN);
                double humidity = mainNode.path("humidity").asDouble(Double.NaN);

                String condition = weatherNode.path("main").asText("N/A");
                String description = weatherNode.path("description").asText("");
                String weatherIcon = weatherNode.path("icon").asText("");

                
                double windSpeedMps = windNode.path("speed").asDouble(Double.NaN);
                double windSpeedKph = Double.isNaN(windSpeedMps) ? Double.NaN : windSpeedMps * 3.6;

                double visibilityMeters = rootNode.path("visibility").asDouble(Double.NaN);
                double visibilityKm = Double.isNaN(visibilityMeters) ? Double.NaN : visibilityMeters / 1000.0;

                return new WeatherData(
                    temperature, humidity, condition, windSpeedKph,
                    confirmedLocationName, date,
                    feelsLike, pressure, visibilityKm,
                    weatherIcon, description, tempMin, tempMax
                );
            } else {
                 String errorMessage = "HTTP Error: " + statusCode + " " + httpResponse.getStatusLine().getReasonPhrase();

                 try {
                    JsonNode errorNode = objectMapper.readTree(responseBody);
                    if (errorNode.has("message")) {
                         errorMessage += " - " + errorNode.path("message").asText();
                    }
                 } catch (Exception parseEx) {
                     
                 }
                throw new IOException(errorMessage);
            }
        } catch (IOException e) {
            
            throw new RuntimeException("Failed to fetch weather data for '" + location + "': " + e.getMessage(), e);
        }
    }
}