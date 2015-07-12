package com.safrzone.safrzone.controllers;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.safrzone.safrzone.services.SafrZoneService;
import com.safrzone.safrzone.services.ServiceConstants;
import com.safrzone.safrzone.services.events.AndroidBus;
import com.safrzone.safrzone.services.events.NewAutoCompleteSearchEvent;
import com.safrzone.safrzone.services.events.ServerSyncCompletedEvent;
import com.safrzone.safrzone.services.storage.HistoryContentProvider;
import com.safrzone.safrzone.utils.IoC;
import com.safrzone.safrzone.views.BaseView;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getName();

    private BaseView _view;
    private AndroidBus _bus = IoC.resolve(AndroidBus.class);

    private static final long SyncDelay = 2000;
    private Handler _handler = new Handler();
    private Runnable _serverSyncRunnable = new Runnable() {
        @Override
        public void run() {
            SafrZoneService service = new RestAdapter.Builder()
                    .setEndpoint(ServiceConstants.SafrZoneApiHost)
                    .build()
                    .create(SafrZoneService.class);
            service.getIncidents(10.0, 10.0, 10, 1000, new Callback<List<SafrZoneService.IncidentResult>>() {

                @Override
                public void success(List<SafrZoneService.IncidentResult> incidentResults, Response response) {
                    Log.v(TAG, "server sync success");

                    ServerSyncCompletedEvent event = new ServerSyncCompletedEvent(incidentResults);
                    _bus.post(event);

                    _handler.postDelayed(_serverSyncRunnable, SyncDelay);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "server sync failed");

                    _handler.postDelayed(_serverSyncRunnable, SyncDelay);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _view = new BaseView(this);

        _bus.register(this);

        // Start pull requests
        _handler.postDelayed(_serverSyncRunnable, SyncDelay);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);
                if (query != null && !query.isEmpty()) {
                    _view.openActionView(query);
                }

                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _handler.removeCallbacks(_serverSyncRunnable);
        _bus.unregister(this);
        _view.dispose();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        _view.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return _view.onCreateOptionsMenu(this, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return _view.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
