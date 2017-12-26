package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.netcracker.ssau.restplacesearcher.model.WeatherData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RPSController {

    private static final String APIKey = "AIzaSyB2ifm7v5evu58yCybWWlsKAVl9EoxTlaw";
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

        //API key = b089342ff727fbb2fc357f71779ba4d3
        //http://samples.openweathermap.org/data/2.5/history/city?lat=41.85&lon=-87.65&appid=b089342ff727fbb2fc357f71779ba4d3
//        System.out.println(currentPlace);
        WeatherData weatherData = new WeatherData(26, "Вторник", "Самара", 0, 3, -3, 2, 750);
        return weatherData;
    }
}
