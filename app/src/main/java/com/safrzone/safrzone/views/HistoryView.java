package com.safrzone.safrzone.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.safrzone.safrzone.R;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.otto.Bus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryView {
    @Bind(R.id.list_view) ListView _listView;

    private ListAdapter _adapter;
    private Bus _bus = IoC.resolve(Bus.class);

    public HistoryView(SimpleCursorAdapter historyAdapter) {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        if (rootView == null) throw new NullPointerException("rootView");
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    public void dispose() {
        _bus.unregister(this);
    }
}
