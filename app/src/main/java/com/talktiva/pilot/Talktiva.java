package com.talktiva.pilot;

import android.app.Application;
import android.content.res.Configuration;

public class Talktiva extends Application {

    public static String TAG;
    private static Talktiva talktiva;

    public Talktiva() {
        super();
    }

    public static Talktiva getInstance() {
        return talktiva;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        talktiva = this;
        TAG = getResources().getString(R.string.app_name);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
