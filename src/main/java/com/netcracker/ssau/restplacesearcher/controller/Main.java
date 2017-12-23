package com.netcracker.ssau.restplacesearcher.controller;

import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

public class Main {
    private static final String APIKey = "AIzaSyB2ifm7v5evu58yCybWWlsKAVl9EoxTlaw";
    private static final GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(APIKey)
            .build();

    public static void main(String[] args) {
        int i = 0;
        getRestPlacesByQuery("2", i, RankBy.DISTANCE, PlaceType.BAR, new LatLng(53.1999856, 50.1572578));
//        getRestPlaces();

//        getDistanceMatrix();
    }

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