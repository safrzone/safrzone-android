package com.safrzone.safrzone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.safrzone.safrzone.R;
import com.safrzone.safrzone.models.ResultsModel;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapView {

    @Bind(R.id.mapview) com.mapbox.mapboxsdk.views.MapView mMapView;

    private Bus _bus = IoC.resolve(Bus.class);
    private ResultsModel _resultsModel = IoC.resolve(ResultsModel.class);
    private GpsLocationProvider mGpsLocationProvider;

    public MapView(GpsLocationProvider gpsLocationProvider) {
        mGpsLocationProvider = gpsLocationProvider;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapview, container, false);
        ButterKnife.bind(this, view);

        LatLng location = new LatLng(12.9667, 77.5667);
        mMapView.setCenter(location);
        mMapView.setZoom(9);

        UserLocationOverlay myLocationOverlay = new UserLocationOverlay(mGpsLocationProvider, mMapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mMapView.getOverlays().add(myLocationOverlay);

        mMapView.setUserLocationEnabled(true);

        _bus.register(this);

        return view;
    }

    public void dispose() {
        _bus.unregister(this);
    }
}
