package com.safrzone.safrzone.services;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SafrZoneService {

    // http://safrzone-env.elasticbeanstalk.com/getIncidents/?longitude=324&latitude=32234&radius=242&timeinterval=242
    @GET("/getIncidents/")
    void getIncidents(
            @Query("longitude") Double lng,
            @Query("latitude") Double lat,
            @Query("radius") long radius,
            @Query("timeinterval") long
            age, Callback<List<IncidentResult>> results);

    /*[{"id":1,"incidentType":"rape","date":-19816963200000,"location":{"id":1,"latitude":"3242","longitude":"23342"}},{"id":2,"incidentType":"rape","date":1420070400000,"location":{"id":2,"latitude":"47.606209","longitude":"-122.332071"}},{"id":3,"incidentType":"rape","date":1420070400000,"location":{"id":3,"latitude":"12.971599","longitude":"77.594563"}},{"id":4,"incidentType":"rape","date":1420070400000,"location":{"id":4,"latitude":"12.971599","longitude":"77.594563"}},{"id":5,"incidentType":"rape","date":1325376000000,"location":{"id":5,"latitude":"3242","longitude":"23342"}},{"id":6,"incidentType":"rape2","date":1325376000000,"location":{"id":6,"latitude":"3242","longitude":"23342"}},{"id":7,"incidentType":"#SafrZone testing!","date":1356998400000,"location":{"id":7,"latitude":"3242","longitude":"23342"}},{"id":8,"incidentType":"#SafrZone testing!","date":1356998400000,"location":{"id":8,"latitude":"3242","longitude":"23342"}},{"id":9,"incidentType":"#SafrZone testing!","date":1388620800000,"location":{"id":9,"latitude":"3242","longitude":"23342"}}]*/

    class IncidentResult {

        @SerializedName("id")
        public Long id;

        @SerializedName("incidentType")
        public String type;

        @SerializedName("date")
        public long date;

        @SerializedName("location")
        public IncidentResultLocation location;

        @SerializedName("landmark")
        public String landmark;

        @SerializedName("imageUrl")
        public String imageUrl;

        @SerializedName("src")
        public String src;

        /*@SerializedName("features")
        public List<MapBoxGeoLookupResultFeature> features;*/

        /*{"landmark":"Tenderloin","imageUrl":null,"src":"twitter"}]*/
    }

    class IncidentResultLocation {

        @SerializedName("longitude")
        public Double lng;

        @SerializedName("latitude")
        public Double lat;
    }
}
