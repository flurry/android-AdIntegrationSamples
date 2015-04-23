package com.flurry.sample.gemini;

import android.app.Application;

import com.flurry.android.FlurryAgent;

public class StreamApplication extends Application {

    private static final String FLURRY_APIKEY = "YOUR_FLURRY_API_KEY";

    @Override
    public void onCreate() {
        super.onCreate();

        FlurryAgent.setLogEnabled(true);
        FlurryAgent.init(this, FLURRY_APIKEY);
    }
}
