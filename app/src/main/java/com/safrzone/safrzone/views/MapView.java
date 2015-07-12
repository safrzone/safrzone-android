package com.safrzone.safrzone.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.safrzone.safrzone.R;
import com.safrzone.safrzone.controllers.adapters.GeoSearchAutocompleteAdapter;
import com.safrzone.safrzone.models.ResultsModel;
import com.safrzone.safrzone.services.MapBoxService;
import com.safrzone.safrzone.services.SafrZoneService;
import com.safrzone.safrzone.services.events.AndroidBus;
import com.safrzone.safrzone.services.events.GoToLngLatEvent;
import com.safrzone.safrzone.services.events.SearchCompletedEvent;
import com.safrzone.safrzone.services.events.ServerSyncCompletedEvent;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapView {

    @Bind(R.id.mapview) com.mapbox.mapboxsdk.views.MapView mMapView;
    /*@Bind(R.id.list_view) ListView mListView;*/

    private AndroidBus _bus = IoC.resolve(AndroidBus.class);
    private ResultsModel _resultsModel = IoC.resolve(ResultsModel.class);
    private GpsLocationProvider mGpsLocationProvider;
    private Context mContext;

    public MapView(GpsLocationProvider gpsLocationProvider, Context context) {
        mGpsLocationProvider = gpsLocationProvider;
        mContext = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapview, container, false);
        ButterKnife.bind(this, view);

        LatLng location = new LatLng(37.77665106, -122.41651297);
        mMapView.setCenter(location);
        mMapView.setZoom(14);
        _resultsModel.currentLocation = "San Francisco, 94103, California, United States";

        /*UserLocationOverlay myLocationOverlay = new UserLocationOverlay(mGpsLocationProvider, mMapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setDrawAccuracyEnabled(true);
        mMapView.getOverlays().add(myLocationOverlay);

        mMapView.setUserLocationEnabled(true);*/

        /*mListView.setAdapter(mAutocompleteAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapBoxService.MapBoxGeoLookupResultFeature result = mAutocompleteAdapter.getItem(position);
                Double lng = result.center.get(0);
                Double lat = result.center.get(1);
                mMapView.setCenter(new LatLng(lat, lng));
                mListView.setVisibility(View.GONE);
            }
        });*/

        _bus.register(this);

        return view;
    }

    @Subscribe
    public void onEventGoToLngLatEvent(GoToLngLatEvent event) {
        mMapView.setCenter(event.latLng);
    }

    public void dispose() {
        _bus.unregister(this);
    }

    /*@Subscribe
    public void onEventSearchCompleted(SearchCompletedEvent event) {
        mListView.setVisibility(View.VISIBLE);
    }*/

    @Subscribe
    public void onEventServerSyncCompleted(ServerSyncCompletedEvent event) {
        for(SafrZoneService.IncidentResult result : event.mIncidentResults) {
            if (!incidentsOnMap.contains(result.id) && result.location != null && result.location.lat != null &&
                    result.location.lng != null) {
                /*String[] icons = new String[] {
                  "police", "danger", "emergency-telephone"
                };*/

                int[] icons = new int[] {
                        R.drawable.fb_pin, R.drawable.twitter_pin, R.drawable.twilio_pin
                };

                String[] titles = new String[] {
                        "Facebook", "Twitter", "Twilio"
                };

                int index = 0;
                for (int i = 0; i < titles.length; i++) {
                    if (titles[i].equalsIgnoreCase(result.src)) {
                        index = i;
                        break;
                    }
                }

                int value = icons[index];
                String socialSource = titles[index];

                String dateString;
                if (result.date <= 0) {
                    Calendar cal = Calendar.getInstance();
                    Date date = cal.getTime();
                    SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a MM/dd/yy");
                    dateString = df2.format(date);
                } else {
                    Date date=new Date(value / 1000);
                    SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a MM/dd/yy");
                    dateString = df2.format(date);
                }

                Marker marker = new Marker(mMapView, result.type, socialSource, new LatLng(result.location.lat, result
                        .location
                        .lng));
                //marker.setIcon(new Icon(mContext, Icon.Size.LARGE, "", "3b5998"));
                marker.setIcon(new Icon(mContext.getResources().getDrawable(value)));
                marker.setSubDescription(dateString);
                MapViewPopupWindow popup = new MapViewPopupWindow(mMapView, result.imageUrl);
                marker.setToolTip(popup);

                mMapView.addMarker(marker);
                incidentsOnMap.add(result.id);
            }
        }
    }

    private final HashSet<Long> incidentsOnMap = new HashSet<>();
}
