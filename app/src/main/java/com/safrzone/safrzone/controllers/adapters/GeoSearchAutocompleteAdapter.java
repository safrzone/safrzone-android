package com.safrzone.safrzone.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.safrzone.safrzone.R;
import com.safrzone.safrzone.services.MapBoxService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GeoSearchAutocompleteAdapter extends ArrayAdapter<MapBoxService.MapBoxGeoLookupResultFeature> {
    LayoutInflater _inflater;

    public GeoSearchAutocompleteAdapter(Context context, ArrayList<MapBoxService.MapBoxGeoLookupResultFeature> results) {
        super(context, R.layout.item_searchresult, results);
        _inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = _inflater.inflate(R.layout.item_searchresult, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MapBoxService.MapBoxGeoLookupResultFeature result = getItem(position);
        holder.textView.setText(result.placeName);

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.name) TextView textView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
