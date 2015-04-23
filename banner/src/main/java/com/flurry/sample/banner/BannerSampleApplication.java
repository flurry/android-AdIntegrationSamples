package com.flurry.sample.banner;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class BannerSampleApplication extends Application {
    private final static String TAG = BannerSampleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.setLogLevel(Log.VERBOSE);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.init(this, "YOUR_FLURRY_API_KEY");
        Log.i(TAG, "Flurry SDK initialized");
    }
}
