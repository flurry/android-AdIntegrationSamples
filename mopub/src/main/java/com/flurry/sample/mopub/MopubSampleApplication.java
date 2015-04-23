package com.flurry.sample.mopub;

import android.app.Application;

import com.flurry.android.FlurryAgent;

public class MopubSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        Optional initialization: It is also done for you in com.mopub.FlurryAgentWrapper
        from the Flurry adapter for Mopub. Only use this init if you are already using
        Flurry analytics. In that case, remove the FlurryAgent.init(Context, String) and
        FlurryAgent.onStartSession(Context) in com.mopub.FlurryAgentWrapper
        */

        // FlurryAgent.init(this, "YOUR_FLURRY_API_KEY");
    }
}
