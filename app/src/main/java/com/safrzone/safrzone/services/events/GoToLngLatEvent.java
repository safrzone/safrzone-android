package com.safrzone.safrzone.services.events;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class GoToLngLatEvent {
    public final LatLng latLng;
    public final String query;

    public GoToLngLatEvent(String query, LatLng latLng) {
        this.latLng = latLng;
        this.query = query;
    }
}
