package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.netcracker.ssau.restplacesearcher.model.WeatherData;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RPSController {
    private final String APIKey = "AIzaSyDM8J5cXMLFnrVt0Il99BwvVotBpx9dmtc"; // AIzaSyB2ifm7v5evu58yCybWWlsKAVl9EoxTlaw
    private final String WEATHER_API_KEY = "appid=b089342ff727fbb2fc357f71779ba4d3";
    private final double CONVERT_TO_TORR_COEF = 0.00750062;

    private final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(APIKey)
            .build();

    @RequestMapping(value = "/findPlaceByType", method = RequestMethod.GET)
    public PlacesSearchResponse findPlaceByType(String placeType,
                                                double radius,
                                                double lat,
                                                double lng) {
        TextSearchRequest request = PlacesApi.textSearchQuery(context, placeType);
        PlacesSearchResponse response = null;
        try {
            response = request.location(new LatLng(lat, lng)).radius(Double.valueOf(radius).intValue()).await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @RequestMapping(value = "/getWeatherData", method = RequestMethod.GET)
    public WeatherData getWeatherData(double lat, double lng) {
        WeatherData weatherData = null;

        try {
            JSONObject historicalData = new JSONObject(getHistoricalWeatherData(lat, lng));
            JSONArray weatherHistoricalDate = historicalData.getJSONArray("list");
            int countOfWeatherHistoricalDate = weatherHistoricalDate.length();

            double averageTemperatureDay = 0;
            double averageTemperatureNight = 0;
            double averagePressure = 0;

            for (int i = 0; i < countOfWeatherHistoricalDate; i++) {
                JSONObject main = weatherHistoricalDate.getJSONObject(i).getJSONObject("main");
                averageTemperatureDay += kelvinToCelsius(main.getDouble("temp_max"));
                averageTemperatureNight += kelvinToCelsius(main.getDouble("temp_min"));
                averagePressure += main.getDouble("pressure");
            }

            averageTemperatureDay = averageTemperatureDay / countOfWeatherHistoricalDate;
            averageTemperatureNight = averageTemperatureNight / countOfWeatherHistoricalDate;
            averagePressure = averagePressure / countOfWeatherHistoricalDate * CONVERT_TO_TORR_COEF;

            JSONObject currentWeatherData = new JSONObject(getCurrentWeatherData(lat, lng));

            weatherData = new WeatherData(
                    getCurrentDay(),
                    getCurrentWeekDay(),
                    getCity(),
                    kelvinToCelsius(currentWeatherData.getJSONObject("main").getDouble("temp")),
                    kelvinToCelsius(currentWeatherData.getJSONObject("main").getDouble("temp_max")),
                    kelvinToCelsius(currentWeatherData.getJSONObject("main").getDouble("temp_min")),
                    (int) Math.round(averageTemperatureDay),
                    (int) Math.round(averageTemperatureNight),
                    (int) Math.round(averagePressure)
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return weatherData;
    }

    @RequestMapping(value = "/getRoad", method = RequestMethod.GET)
    public Map<String, String> getRoad(String origin, String destination, double petrolCost) {
        Map<String, String> map = new HashMap<>();
        DistanceMatrix distanceMatrix = null;
        String[] orign = new String[1];
        String[] dest = new String[1];
        orign[0] = origin;
        dest[0] = destination;
        try {
            distanceMatrix = DistanceMatrixApi.getDistanceMatrix(context, orign, dest).await();

            DistanceMatrixElement element = distanceMatrix.rows[0].elements[0];
            map.put("distance", String.valueOf(element.distance.humanReadable));
            map.put("duration", String.valueOf(element.duration.humanReadable));
            map.put("cost", String.valueOf(element.distance.inMeters / 100000 * 12 * petrolCost * 2));
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private String getCurrentWeatherData(double lat, double lon) throws IOException {
        return performRequest("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&" + WEATHER_API_KEY);
    }

    // API key = b089342ff727fbb2fc357f71779ba4d3
    // http://api.openweathermap.org/data/2.5/history/city?lat=41.85&lon=-87.65&appid=b089342ff727fbb2fc357f71779ba4d3
    private String getHistoricalWeatherData(double lat, double lon) throws IOException {
        return performRequest("http://samples.openweathermap.org/data/2.5/history/city?lat=41.85&lon=-87.65&appid=b1b15e88fa797225412429c1c50c122a1");
        // (http://api.openweathermap.org/data/2.5/history/city?lat=" + lat + "&lon=" + lon + "&" + WEATHER_API_KEY);
    }

    private String performRequest(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("content-type", "application/x-www-form-urlencoded");
        HttpResponse response = httpClient.execute(request);

        BufferedReader stream = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = stream.readLine()) != null)
            responseStrBuilder.append(inputStr);
        return responseStrBuilder.toString();
    }

    private int kelvinToCelsius(double kellvin) {
        return (int) Math.round(kellvin) - 273;
    }

    private String getCity() {
        // TODO Remove
        return "Самара";
    }

    private String getCurrentWeekDay() {
        Date dateNow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dateNow);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return "Воскресенье";
            case 2:
                return "Понедельник";
            case 3:
                return "Вторник";
            case 4:
                return "Среда";
            case 5:
                return "Четверг";
            case 6:
                return "Пятница";
            case 7:
                return "Суббота";

        }
        return null;
    }

    private int getCurrentDay() {
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("d");
        return Integer.valueOf(formatForDateNow.format(dateNow));
    }

    private String getDate(long date) {
        System.out.println("date millsec " + date);
        Date dateNow = new Date(date * 1000L);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return formatForDateNow.format(dateNow);
    }
}