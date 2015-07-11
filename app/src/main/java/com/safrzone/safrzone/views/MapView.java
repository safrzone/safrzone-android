package com.safrzone.safrzone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.safrzone.safrzone.R;
import com.safrzone.safrzone.controllers.adapters.GeoSearchAutocompleteAdapter;
import com.safrzone.safrzone.models.ResultsModel;
import com.safrzone.safrzone.services.MapBoxService;
import com.safrzone.safrzone.services.SearchCompletedEvent;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapView {

    @Bind(R.id.mapview) com.mapbox.mapboxsdk.views.MapView mMapView;
    @Bind(R.id.list_view) ListView mListView;

    private Bus _bus = IoC.resolve(Bus.class);
    private ResultsModel _resultsModel = IoC.resolve(ResultsModel.class);
    private GpsLocationProvider mGpsLocationProvider;
    private GeoSearchAutocompleteAdapter mAutocompleteAdapter;

    public MapView(GpsLocationProvider gpsLocationProvider, GeoSearchAutocompleteAdapter adapter) {
        mGpsLocationProvider = gpsLocationProvider;
        mAutocompleteAdapter = adapter;
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

        mListView.setAdapter(mAutocompleteAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapBoxService.MapBoxGeoLookupResultFeature result = mAutocompleteAdapter.getItem(position);
                Float lng = result.center.get(0);
                Float lat = result.center.get(1);
                mMapView.setCenter(new LatLng(lat, lng));
                mListView.setVisibility(View.GONE);
            }
        });

        _bus.register(this);

        return view;
    }

    public void dispose() {
        _bus.unregister(this);
    }

    @Subscribe
    public void onEventSearchCompleted(SearchCompletedEvent event) {
        mListView.setVisibility(View.VISIBLE);
    }
}
