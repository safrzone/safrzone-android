package com.safrzone.safrzone.services;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MapBoxService {

    // Geo-lookup
    // http://api.tiles.mapbox.com/v4/geocode/mapbox.places/{query}.json?access_token={token}
    @GET("/v4/geocode/mapbox.places/{query}.json")
    void geoLookup(@Query("access_token") String token, @Path("query") String query,
            Callback<MapBoxGeoLookupResult> result);

    class MapBoxGeoLookupResult {

        @SerializedName("type")
        public String type;

        @SerializedName("query")
        public List<String> queries;

        @SerializedName("features")
        public List<MapBoxGeoLookupResultFeature> features;
    }

    class MapBoxGeoLookupResultFeature {

        @SerializedName("type")
        public String type;

        @SerializedName("text")
        public String text;

        @SerializedName("place_name")
        public String placeName;

        @SerializedName("center")
        public List<Double> center;
    }
}

