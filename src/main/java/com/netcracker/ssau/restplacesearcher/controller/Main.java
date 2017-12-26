package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import com.netcracker.ssau.restplacesearcher.model.WeatherData;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {
    private static final String placeType = "Пляж";
    //center: {lat: 53.1999856, lng: 50.1572578},//Samara
    private static final int radius = 1000; //1000m
    private static final double lat = 53.1999856;
    private static final double lng = 50.1572578;
    private static final String APIKey = "AIzaSyDM8J5cXMLFnrVt0Il99BwvVotBpx9dmtc";
    //AIzaSyB2ifm7v5evu58yCybWWlsKAVl9EoxTlaw
    private static final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(APIKey)
            .build();
    private static final String WEATHER_API_KEY = "appid=b089342ff727fbb2fc357f71779ba4d3";
    private static final double CONVERT_TO_TORR_COEF = 0.00750062;

    public static void main(String[] args) {
//        lat: 53.1999856,
//                lng: 50.1572578
//        getWeather(53.1999856, 50.1572578);
        getDistanceMatrix();
//        PlacesSearchResponse response = findPlaceByType(placeType, radius, lat, lng);
//        System.out.println(response.results);
//        getWeather();


//        getRestPlacesByQuery("2", i, RankBy.DISTANCE, PlaceType.BAR, new LatLng(53.1999856, 50.1572578));
//        getRestPlaces();
//        getDistanceMatrix();
    }

// RPSController ---------------->


    //    @RequestMapping(value = "/findPlaceByType", method = RequestMethod.GET)
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

    //    @RequestMapping(value = "/getWeatherData", method = RequestMethod.GET)
    public static WeatherData getWeatherData(String sity) {
        //int todayNow, int todayDay, int todayNight, int averageTemperature, int averagePrecipitation
        WeatherData weatherData = new WeatherData(26, "Вторник", "Самара", 0, 3, -3, 2, 1, 750);
        return weatherData;
    }

    /**
     * type type of the call, keep this parameter in the API call as 'hour'
     * start start date (unix time, UTC time zone), e.g. start=1369728000
     * end end date (unix time, UTC time zone), e.g. end=1369789200
     * cnt amount of returned data (one per hour, can be used instead of 'end')
     */

    private static WeatherData getWeather(double lat, double lng) {
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


//  ------------------------------------------------


    private static void getPlaces() {
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(53.1999856, 50.1572578));
        try {
            PlacesSearchResponse response = request.radius(500).type(PlaceType.BAR).await();
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //todo много флажковый метод
    private static void getRestPlacesByQuery(String query, int radius, RankBy rankBy, PlaceType placeType, LatLng location) {
//        TextSearchRequest request = PlacesApi.textSearchQuery(context, query);
        System.out.println(radius);
        if (location != null && radius != 0) {

        }

    }

    private static void getRestPlaces() {
        TextSearchRequest request = PlacesApi.textSearchQuery(context, "Самара пляж");
        TextSearchRequest request2 = PlacesApi.textSearchQuery(context, "Казань пляж");
        try {
            PlacesSearchResponse response = request.type(PlaceType.BAR).location(new LatLng(53.1999856, 50.1572578)).rankby(RankBy.DISTANCE).await();
            PlacesSearchResponse response2 = request2.radius(1500).await();
            PlacesSearchResponse total = new PlacesSearchResponse();
            total.results = ArrayUtils.addAll(response.results, response2.results);
            System.out.println(total.results.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getDistanceMatrix() {
        String[] origins = new String[1];
        String[] destinations = new String[1];
        origins[0] = "Самара, Ленинградская область";
//        origins[1] = "Seattle";
        destinations[0] = "Самара, самарская область";
//        destinations[1] = "Victoria BC";
        DistanceMatrixApiRequest request = DistanceMatrixApi.getDistanceMatrix(context, origins, destinations);
        try {
            DistanceMatrix distanceMatrix = request.await();
//            DistanceMatrix distanceMatrix = request.await();
//            distanceMatrix.
            System.out.println(distanceMatrix.toString());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getArchitecturalSights() {
        getPlaces();
    }


//    https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&key=YOUR_API_KEY


}
//        lat: 53.1999856, lng: 50.1572578
        /*
        GeocodingApiRequest req = GeocodingApi.newRequest(context).address("Sydney");

// Synchronous
try {
    req.await();
    // Handle successful request.
} catch (Exception e) {
    // Handle error
}

req.awaitIgnoreError(); // No checked exception.

// Async
req.setCallback(new PendingResult.Callback<GeocodingResult[]>() {
  @Override
  public void onResult(GeocodingResult[] result) {
    // Handle successful request.
  }

  @Override
  public void onFailure(Throwable e) {
    // Handle error.
  }
});





        * */


//    @RequestMapping(value = "/place", method = RequestMethod.GET)
//    public PlacesSearchResponse getAllPlaceGet() {
//        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(53.1999856, 50.1572578));
//        PlacesSearchResponse response = null;
//        try {
//            response = request.radius(500).type(PlaceType.BAR).await();
//            System.out.println(response.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
//
//    @RequestMapping(value = "/places", method = RequestMethod.POST)
//
//    /**
//     * current position
//     * target position
//     * time to place ( 30min ... to 1.5h )
//     * with architectural sights
//     * -------- weather (от +15 до +25)
//     * */
//
//    public PlacesSearchResponse getAllPlacePost(List<String> flags, double Lat, double Lng, int radius) {
//        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(Lat, Lng));
//        PlacesSearchResponse response = null;
//        try {
//            response = request.radius(radius).type(PlaceType.BAR).await();
//            System.out.println(response.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
//
//
//    @RequestMapping(value = "/getLucky", method = RequestMethod.POST)
//    public PlacesSearchResponse getLucky() {
//        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(53.1999856, 50.1572578));
//        PlacesSearchResponse response = null;
//        try {
//            response = request.radius(500).type(PlaceType.BAR).await();
//            System.out.println(response.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return response; //return one field
//    }
//
