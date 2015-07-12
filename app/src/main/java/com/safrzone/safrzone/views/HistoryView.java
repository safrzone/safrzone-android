package com.safrzone.safrzone.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.safrzone.safrzone.R;
import com.safrzone.safrzone.models.ResultsModel;
import com.safrzone.safrzone.services.events.AndroidBus;
import com.safrzone.safrzone.services.events.CloseDrawerEvent;
import com.safrzone.safrzone.services.events.GoToLngLatEvent;
import com.safrzone.safrzone.services.storage.StorageHelper;
import com.safrzone.safrzone.utils.ActivityUtils;
import com.safrzone.safrzone.utils.ControlUtils;
import com.safrzone.safrzone.utils.IoC;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryView {
    @Bind(R.id.list_view) ListView _listView;
    @Bind(R.id.subscribeButton) Button _button;

    private ListAdapter _adapter;
    private AndroidBus _bus = IoC.resolve(AndroidBus.class);
    private AppCompatActivity _context;
    private ResultsModel _resultsModel = IoC.resolve(ResultsModel.class);

    public HistoryView(Context context, SimpleCursorAdapter listAdapter) {
        _adapter = listAdapter;
        _context = (AppCompatActivity) context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        if (rootView == null) throw new NullPointerException("rootView");
        ButterKnife.bind(this, rootView);

        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new ListViewItemClickListener());

        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(_context)
                        .setPositiveButton("Subscribe", null);

                LayoutInflater inflater = _context.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.subscribe_dialog, null);
                final EditText nameEditText = (EditText) dialogView.findViewById(R.id.name);
                //nameEditText.setText("m");
                builder.setView(dialogView);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ControlUtils.dismiss(dialog);
                    }
                });

                final AlertDialog d = builder.create();

                d.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context
                                .INPUT_METHOD_SERVICE);
                        imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);

                        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                        if (b == null) throw new UnsupportedOperationException("could not find positive button");
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (nameEditText != null && nameEditText.getEditableText() != null) {
                                    String value = nameEditText.getEditableText().toString();
                                    if (!value.isEmpty()) {
                                        ActivityUtils.toast("Subscribed to incident at " + _resultsModel
                                                .currentLocation);
                                        ControlUtils.dismiss(d);

                                        CloseDrawerEvent event = new CloseDrawerEvent();
                                        _bus.post(event);
                                    }
                                }
                            }
                        });
                    }
                });

                d.show();
            }
        });

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
            Double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(StorageHelper.COLUMN_LAT));
            Double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(StorageHelper.COLUMN_LNG));

            GoToLngLatEvent event = new GoToLngLatEvent(query, new LatLng(lat, lng));
            _bus.post(event);
        }
    }
}
