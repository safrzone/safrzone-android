package com.safrzone.safrzone.services.events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class AndroidBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public void postOnMainThread(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }
}
