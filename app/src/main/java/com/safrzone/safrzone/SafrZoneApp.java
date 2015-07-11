package com.safrzone.safrzone;

import android.app.Application;
import android.content.Context;

import com.safrzone.safrzone.utils.IoC;
import com.squareup.picasso.Picasso;

public class SafrZoneApp extends Application {
    private static SafrZoneApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Our IoC container doesn't support objects without empty constructors yet
        IoC.register(Picasso.with(getContext()));
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
