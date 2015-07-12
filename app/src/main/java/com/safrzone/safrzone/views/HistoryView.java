package com.safrzone.safrzone.views;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.safrzone.safrzone.R;
import com.safrzone.safrzone.services.events.AndroidBus;
import com.safrzone.safrzone.services.events.NewSearchEvent;
import com.safrzone.safrzone.services.storage.StorageHelper;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryView {
    @Bind(R.id.list_view) ListView _listView;

    private ListAdapter _adapter;
    private AndroidBus _bus = IoC.resolve(AndroidBus.class);

    public HistoryView(SimpleCursorAdapter listAdapter) {
        _adapter = listAdapter;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        if (rootView == null) throw new NullPointerException("rootView");
        ButterKnife.bind(this, rootView);

        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new ListViewItemClickListener());

        _bus.register(this);

        return rootView;
    }

    public void dispose() {
        _bus.unregister(this);
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) _adapter.getItem(position);
            String query = cursor.getString(cursor.getColumnIndexOrThrow(StorageHelper.COLUMN_QUERY));

            NewSearchEvent event = new NewSearchEvent(query);
            _bus.post(event);
        }
    }
}
