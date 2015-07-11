package com.safrzone.safrzone.controllers;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.safrzone.safrzone.services.NewSearchEvent;
import com.safrzone.safrzone.utils.IoC;
import com.safrzone.safrzone.views.BaseView;
import com.squareup.otto.Bus;

public class BaseActivity extends AppCompatActivity {

    private BaseView _view;
    private Bus _bus = IoC.resolve(Bus.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _view = new BaseView(this);

        _bus.register(this);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getAction()) {
            case Intent.ACTION_SEARCH: {
                String query = intent.getStringExtra(SearchManager.QUERY);
                if (query != null && !query.isEmpty()) {
                    NewSearchEvent event = new NewSearchEvent(query);
                    _bus.post(event);
                    /*HistoryContentProvider.insertQuery(query);*/
                }

                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        _bus.unregister(this);
        _view.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return _view.onCreateOptionsMenu(this, menu);
    }
}
