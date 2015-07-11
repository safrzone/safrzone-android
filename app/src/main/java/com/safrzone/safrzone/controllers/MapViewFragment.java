package com.safrzone.safrzone.controllers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.overlay.GpsLocationProvider;
import com.safrzone.safrzone.models.ResultsModel;
import com.safrzone.safrzone.services.NewSearchEvent;
import com.safrzone.safrzone.utils.IoC;
import com.safrzone.safrzone.views.MapView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MapViewFragment
        extends Fragment {
    private static final String TAG = MapViewFragment.class.getName();

    private ResultsModel _resultsModel = IoC.resolve(ResultsModel.class);
    private MapView _view;
    private GpsLocationProvider mGpsLocationProvider;

    private Bus _bus = IoC.resolve(Bus.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mGpsLocationProvider = new GpsLocationProvider(getActivity());

        // create view
        _view = new MapView(mGpsLocationProvider);
        View resultView = _view.onCreateView(inflater, container, savedInstanceState);

        // listen for events
        _bus.register(this);

        return resultView;
    }

    @Override public void onDestroy() {
        super.onDestroy();

        _bus.unregister(this);

        _view.dispose();
    }

    @Subscribe
    public void eventSearchStarted(NewSearchEvent event) {
        _resultsModel.setNewQuery(event.query);
    }
}

