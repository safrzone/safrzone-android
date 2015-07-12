package com.safrzone.safrzone.utils;

import android.widget.Toast;

import com.safrzone.safrzone.SafrZoneApp;

public final class ActivityUtils {
    private static Toast _toast;

    public static void toast(String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    public static void toast(String text, int duration) {
        clearToast();

        Toast toast = Toast.makeText(SafrZoneApp.getContext(), text, duration);
        toast.show();
        _toast = toast;
    }

    public static void clearToast() {
        if (_toast != null) _toast.cancel();
    }
}

