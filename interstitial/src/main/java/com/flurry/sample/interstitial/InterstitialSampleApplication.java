package com.flurry.sample.interstitial;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class InterstitialSampleApplication extends Application {

    private final static String TAG = InterstitialSampleApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogLevel(Log.VERBOSE);
        FlurryAgent.setLogEvents(true);
        // NOTE: Use your own Flurry API key. This is left here to make sample review easier
        FlurryAgent.init(this, "JQVT87W7TGN5W7SWY2FH");
        Log.i(TAG, "Flurry SDK initialized");
    }
}