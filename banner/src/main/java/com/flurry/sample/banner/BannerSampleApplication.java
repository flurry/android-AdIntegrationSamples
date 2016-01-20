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
        FlurryAgent.init(this, "JQVT87W7TGN5W7SWY2FH");
        Log.i(TAG, "Flurry SDK initialized");
    }
}
