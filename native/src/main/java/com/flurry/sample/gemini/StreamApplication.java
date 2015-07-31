package com.flurry.sample.gemini;

import android.app.Application;

import com.flurry.android.FlurryAgent;

public class StreamApplication extends Application {

    // NOTE: Use your own Flurry API key. This is left here to make sample review easier
    private static final String FLURRY_APIKEY = "JQVT87W7TGN5W7SWY2FH";

    @Override
    public void onCreate() {
        super.onCreate();

        FlurryAgent.setLogEnabled(true);
        FlurryAgent.init(this, FLURRY_APIKEY);
    }
}
