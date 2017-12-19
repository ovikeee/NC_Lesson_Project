package com.netcracker.ssau.restplacesearcher.controller;


import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
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

    @RequestMapping(value = "/place", method = RequestMethod.GET)
    public PlacesSearchResponse getAllPlace() {
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(53.1999856, 50.1572578));
        PlacesSearchResponse response = null;
        try {
            response = request.radius(500).type(PlaceType.BAR).await();
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
