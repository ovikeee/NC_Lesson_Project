package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
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

@RestController
public class RPSController {
    private static final String placeType = "Пляж";
    //center: {lat: 53.1999856, lng: 50.1572578},//Samara
    private static final int radius = 1000; //1000m
    private static final String APIKey = "AIzaSyDM8J5cXMLFnrVt0Il99BwvVotBpx9dmtc";
    //AIzaSyB2ifm7v5evu58yCybWWlsKAVl9EoxTlaw
    private static final String WEATHER_API_KEY = "appid=b089342ff727fbb2fc357f71779ba4d3";
    private static final double CONVERT_TO_TORR_COEF = 0.00750062;


    private static final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(APIKey)
            .build();


    @RequestMapping(value = "/findPlaceByType", method = RequestMethod.GET)

    public static PlacesSearchResponse findPlaceByType(String placeType, int radius, double lat, double lng) {
        TextSearchRequest request = PlacesApi.textSearchQuery(context, placeType);
        PlacesSearchResponse response = null;
        try {
            response = request.location(new LatLng(lat, lng)).radius(radius).await();
            System.out.println(response.results.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    @RequestMapping(value = "/getWeatherData", method = RequestMethod.GET)
    public static WeatherData getWeatherData(double lat, double lng) {
        WeatherData weatherData = null;

        try {
            JSONObject historicalData = new JSONObject(getHistoricalWeatherData(lat, lng));
            JSONArray weatherHistoricalDate = historicalData.getJSONArray("list");
            int countOfWeatherHistoricalDate = weatherHistoricalDate.length();
            System.out.println("Сведений о погоде найдено: " + weatherHistoricalDate.length());

            double averageTemperatureDay = 0;
            double averageTemperatureNight = 0;
            double averagePressure = 0;

            for (int i = 0; i < countOfWeatherHistoricalDate; i++) {
                JSONObject main = weatherHistoricalDate.getJSONObject(i).getJSONObject("main");
                System.out.println("Погода за " + getDate(weatherHistoricalDate.getJSONObject(i).getLong("dt")));
                System.out.println("Днём: " + kelvinToCelsius(main.getDouble("temp_max")));
                System.out.println("Ночью: " + kelvinToCelsius(main.getDouble("temp_min")));
                averageTemperatureDay += kelvinToCelsius(main.getDouble("temp_max"));
                averageTemperatureNight += kelvinToCelsius(main.getDouble("temp_min"));
                averagePressure += main.getDouble("pressure");
            }

            averageTemperatureDay = averageTemperatureDay / countOfWeatherHistoricalDate;
            averageTemperatureNight = averageTemperatureNight / countOfWeatherHistoricalDate;
            averagePressure = averagePressure / countOfWeatherHistoricalDate * CONVERT_TO_TORR_COEF;


//            JSONObject main = weatherHistoricalDate.getJSONObject(0).getJSONObject("main");
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

    @RequestMapping(value = "/getDistanceMatrix", method = RequestMethod.GET)
    private static DistanceMatrix getDistanceMatrix(String origin, String destinationLng) {
        String[] origins = new String[1];
        String[] destinations = new String[1];
        DistanceMatrix distanceMatrix = null;
        origins[0] = origin;
//        origins[1] = "Seattle";
        destinations[0] = destinationLng;
//        destinations[1] = "Victoria BC";
        DistanceMatrixApiRequest request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations);
        try {
            distanceMatrix = request.await();
            System.out.println(distanceMatrix.toString());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return distanceMatrix;
    }


    private static String getCurrentWeatherData(double lat, double lon) throws IOException {
        return performRequest("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&" + WEATHER_API_KEY);
    }


    //API key = b089342ff727fbb2fc357f71779ba4d3
    //http://api.openweathermap.org/data/2.5/history/city?lat=41.85&lon=-87.65&appid=b089342ff727fbb2fc357f71779ba4d3
    private static String getHistoricalWeatherData(double lat, double lon) throws IOException {
        return performRequest("http://samples.openweathermap.org/data/2.5/history/city?lat=41.85&lon=-87.65&appid=b1b15e88fa797225412429c1c50c122a1");
        //(http://api.openweathermap.org/data/2.5/history/city?lat=" + lat + "&lon=" + lon + "&" + WEATHER_API_KEY);

    }

    private static String performRequest(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
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

    private static int kelvinToCelsius(double kellvin) {
        return (int) Math.round(kellvin) - 273;
    }

    private static String getCity() {
        return "Самара";
    }

    private static String getCurrentWeekDay() {
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
                return "Пятниа";
            case 7:
                return "Суббота";

        }
        return null;
    }


    private static int getCurrentDay() {
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("d");
        System.out.println("Текущая дата " + formatForDateNow.format(dateNow));
        return Integer.valueOf(formatForDateNow.format(dateNow));
    }

    private static String getDate(long date) {
        System.out.println("date millsec " + date);
        Date dateNow = new Date(date * 1000L);
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return formatForDateNow.format(dateNow);
    }


}
