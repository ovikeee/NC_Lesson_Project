package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.*;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.sun.deploy.util.ArrayUtil;
import org.apache.commons.lang3.ArrayUtils;

public class Main {
    private static final String APIKey = "AIzaSyB2ifm7v5evu58yCybWWlsKAVl9EoxTlaw";
    private static final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(APIKey)
            .build();

    public static void main(String[] args) {
        getRestPlaces();
    }

    private static void getPlaces(){
        NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(53.1999856, 50.1572578));
        try {
            PlacesSearchResponse response = request.radius(500).type(PlaceType.BAR).await();
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void getRestPlaces(){
        TextSearchRequest request = PlacesApi.textSearchQuery(context, "Самара пляж");
        TextSearchRequest request2 = PlacesApi.textSearchQuery(context, "Казань пляж");
        try {
            PlacesSearchResponse response = request.radius(1500).await();
            PlacesSearchResponse response2 = request2.radius(1500).await();
            PlacesSearchResponse total = new PlacesSearchResponse();
            total.results =  ArrayUtils.addAll(response.results, response2.results);
            System.out.println(total.results.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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