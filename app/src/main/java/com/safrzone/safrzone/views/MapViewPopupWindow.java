package com.safrzone.safrzone.views;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.InfoWindow;
import com.mapbox.mapboxsdk.views.MapView;
import com.safrzone.safrzone.R;
import com.safrzone.safrzone.utils.IoC;
import com.squareup.picasso.Picasso;

public class MapViewPopupWindow extends InfoWindow {
    String imageUrl;

    public MapViewPopupWindow(MapView mv, String s) {
        super(R.layout.mapview_popup_window, mv);
        this.imageUrl = s;
    }

    @Override
    public void onOpen(Marker overlayItem) {
        ImageView imageView = (ImageView) mView.findViewById(R.id.imageview);

        ((TextView) mView.findViewById(R.id.title)).setText(overlayItem.getTitle());
        ((TextView) mView.findViewById(R.id.description)).setText(overlayItem.getDescription());
        ((TextView) mView.findViewById(R.id.time)).setText(overlayItem.getSubDescription());

        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            IoC.resolve(Picasso.class)
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }
    }
}
