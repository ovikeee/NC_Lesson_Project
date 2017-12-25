package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.TextSearchRequest;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
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
            PlacesSearchResponse total = new PlacesSearchResponse();
            System.out.println(total.results.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


}
