package com.safrzone.safrzone.views;

import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.safrzone.safrzone.R;
import com.safrzone.safrzone.SafrZoneApp;
import com.safrzone.safrzone.controllers.BaseActivity;
import com.safrzone.safrzone.models.ResultsModel;
import com.safrzone.safrzone.services.MapBoxService;
import com.safrzone.safrzone.services.events.AndroidBus;
import com.safrzone.safrzone.services.events.CloseDrawerEvent;
import com.safrzone.safrzone.services.events.GoToLngLatEvent;
import com.safrzone.safrzone.services.events.NewAutoCompleteSearchEvent;
import com.safrzone.safrzone.services.events.NewBackgroundAutoCompleteSearchEvent;
import com.safrzone.safrzone.services.events.SearchCompletedEvent;
import com.safrzone.safrzone.services.ServiceConstants;
import com.safrzone.safrzone.services.storage.HistoryContentProvider;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseView {
    private static final String TAG = BaseView.class.getName();

    @Bind(R.id.drawer_layout) DrawerLayout _drawer;
    @Bind(R.id.drawer_fragment) View _drawerSidebar;

    private ActionBarDrawerToggle _drawerToggle;

    private ActionBar _actionBar;
    private MenuItem _searchMenuItem;
    private SearchView searchView;
    private ResultsModel _resultsModel = IoC.resolve(ResultsModel.class);
    private AndroidBus _bus = IoC.resolve(AndroidBus.class);
    private MapBoxService _mapBoxService = IoC.resolve(MapBoxService.class);
    private SimpleCursorAdapter busStopCursorAdapter;
    private AppCompatActivity activity;

    public BaseView(final Context context) {
        activity = (AppCompatActivity) context;
        _actionBar = activity.getSupportActionBar();

        View view = View.inflate(context, R.layout.activity_main, null);
        activity.setContentView(view);
        ButterKnife.bind(this, view);

        _actionBar.setDisplayHomeAsUpEnabled(true);
        _actionBar.setHomeButtonEnabled(true);

        _drawerToggle = new ActionBarDrawerToggle(
                activity,
                _drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                updateTitle();
                ((AppCompatActivity) context).invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateTitle();
                ((AppCompatActivity) context).invalidateOptionsMenu();
            }
        };

        _drawer.post(new Runnable() {
            @Override
            public void run() {
                _drawerToggle.syncState();
            }
        });

        _drawer.setDrawerListener(_drawerToggle);

        _bus.register(this);
    }

    @Subscribe
    public void eventBackgroundSearchStarted(NewBackgroundAutoCompleteSearchEvent event) {
        if (!TextUtils.isEmpty(event.query)) {
            MapBoxService service = new RestAdapter.Builder()
                    .setEndpoint(ServiceConstants.MapBoxApiHost)
                    .build()
                    .create(MapBoxService.class);
            service.geoLookup(ServiceConstants.AccessToken, event.query, new Callback<MapBoxService
                    .MapBoxGeoLookupResult>() {
                @Override
                public void success(MapBoxService.MapBoxGeoLookupResult result, Response response) {
                    List<MapBoxService.MapBoxGeoLookupResultFeature> features = new ArrayList<>();

                    if (result != null && result.features != null) {
                        for(MapBoxService.MapBoxGeoLookupResultFeature feature : result.features) {
                            if (feature.center != null && feature.placeName != null && feature.center.size() == 2) {
                                features.add(feature);
                            }
                        }
                    }

                    _resultsModel.results = features;

                    SearchCompletedEvent event = new SearchCompletedEvent();
                    _bus.post(event);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "geo lookup failed");
                }
            });
        }
    }

    @Subscribe
    public void onEventGoToLngLatEvent(GoToLngLatEvent event) {
        _drawer.closeDrawers();
        if (_searchMenuItem != null) _searchMenuItem.collapseActionView();
        _resultsModel.setNewQuery(event.query);
        updateTitle();
        _resultsModel.currentLocation = event.query;
    }

    @Subscribe
    public void eventSearchStarted(NewAutoCompleteSearchEvent event) {
        _drawer.closeDrawers();
        if (_searchMenuItem != null) _searchMenuItem.collapseActionView();
        _resultsModel.setNewQuery(event.query);
        updateTitle();

        if (!TextUtils.isEmpty(event.query)) {
            MapBoxService service = new RestAdapter.Builder()
                    .setEndpoint(ServiceConstants.MapBoxApiHost)
                    .build()
                    .create(MapBoxService.class);
            service.geoLookup(ServiceConstants.AccessToken, event.query, new Callback<MapBoxService
                    .MapBoxGeoLookupResult>() {
                @Override
                public void success(MapBoxService.MapBoxGeoLookupResult result, Response response) {
                    List<MapBoxService.MapBoxGeoLookupResultFeature> features = new ArrayList<>();

                    if (result != null && result.features != null) {
                        for(MapBoxService.MapBoxGeoLookupResultFeature feature : result.features) {
                            if (feature.center != null && feature.placeName != null && feature.center.size() == 2) {
                                features.add(feature);
                            }
                        }
                    }

                    _resultsModel.results = features;

                    SearchCompletedEvent event = new SearchCompletedEvent();
                    _bus.post(event);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "geo lookup failed");
                }
            });
        }
    }

    public void dispose() {
        _bus.unregister(this);
    }

    public boolean onCreateOptionsMenu(BaseActivity activity, Menu menu) {
        activity.getMenuInflater().inflate(R.menu.menu_main, menu);
        _searchMenuItem = menu.findItem(R.id.search);

        // Initialize SearchView in action bar
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) _searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));

        // Close search view when it loses focus
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    _searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                    _resultsModel.results = null;
                    updateSearchAutocompleteResults();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    _resultsModel.query = null;
                    _resultsModel.results = null;
                    updateSearchAutocompleteResults();
                } else {
                    _resultsModel.query = newText;
                    NewBackgroundAutoCompleteSearchEvent event = new NewBackgroundAutoCompleteSearchEvent(newText);
                    _bus.post(event);
                }

                return false;
            }
        });

        // Set up SearchView auto-complete
        updateSearchAutocompleteResults();

        return true;
    }

    @Subscribe public void onEventCloseDrawerEvent(CloseDrawerEvent event) {
        _drawer.closeDrawers();
    }

    @Subscribe public void onEventSearchCompletedEvent(SearchCompletedEvent event) {
        updateSearchAutocompleteResults();
    }

    private void updateSearchAutocompleteResults() {
        String[] columnNames = {"_id","text", "lng", "lat"};
        MatrixCursor cursor = new MatrixCursor(columnNames);

        MapBoxService.MapBoxGeoLookupResultFeature[] array;

        if (_resultsModel.results == null) {
            array = new MapBoxService.MapBoxGeoLookupResultFeature[0];
        } else {
            array = new MapBoxService.MapBoxGeoLookupResultFeature[_resultsModel.results.size()];
            for (int i = 0; i < _resultsModel.results.size(); i++) {
                array[i] = _resultsModel.results.get(i);
            }
        }

        String[] temp = new String[4];
        int id = 0;
        for(MapBoxService.MapBoxGeoLookupResultFeature item : array){
            temp[0] = Integer.toString(id++);
            temp[1] = item.placeName;
            temp[2] = item.center.get(0).toString();
            temp[3] = item.center.get(1).toString();
            cursor.addRow(temp);
        }
        String[] from = {"text"};
        int[] to = {R.id.name};
        busStopCursorAdapter = new SimpleCursorAdapter(activity, R.layout.item_searchresult, cursor, from, to);
        searchView.setSuggestionsAdapter(busStopCursorAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                String selectedItem = (String)busStopCursorAdapter.getItem(position);
                Log.v("search view", selectedItem);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                MatrixCursor mc = (MatrixCursor)busStopCursorAdapter.getItem(position);
                Log.e(TAG, "text: " + mc.getString(1));
                String text = mc.getString(1);
                Double lng = mc.getDouble(2);
                Double lat = mc.getDouble(3);

                HistoryContentProvider.insertQuery(text, lng, lat);
                _actionBar.collapseActionView();

                GoToLngLatEvent event = new GoToLngLatEvent(text, new LatLng(lat, lng));
                _bus.post(event);

                return false;
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                busStopCursorAdapter.notifyDataSetChanged();
            }
        }, 200);
    }

    private void updateTitle() {
        boolean drawerOpen = isDrawerOpen();

        if (drawerOpen) {
            _actionBar.setTitle(SafrZoneApp.getContext().getString(R.string.history_fragment_title));
        } else {
            if (_resultsModel.query != null && !_resultsModel.query.isEmpty()) {
                _actionBar.setTitle(_resultsModel.query);
            } else {
                _actionBar.setTitle(R.string.app_name);
            }
        }
    }

    private boolean isDrawerOpen() {
        return _drawer.isDrawerOpen(_drawerSidebar);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = isDrawerOpen();
        _searchMenuItem.setVisible(!drawerOpen);
        if (drawerOpen) {
            _searchMenuItem.collapseActionView();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    public void openActionView(String query) {
        searchView.setIconified(false);
        searchView.setIconified(true);
        searchView.setQuery(query, false);
    }
}
