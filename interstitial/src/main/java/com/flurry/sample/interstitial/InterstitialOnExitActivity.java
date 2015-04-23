package com.flurry.sample.interstitial;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;

public class InterstitialOnExitActivity extends ActionBarActivity {

    private final static String TAG = InterstitialOnExitActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_interstitial_on_exit);
    }

    @Override
    public void onBackPressed() {
        FlurryAdInterstitial flurryAdInterstitial = new FlurryAdInterstitial(this, "InterstitialTest");
        flurryAdInterstitial.setListener(new FlurryAdInterstitialListener() {
            @Override
            public void onFetched(FlurryAdInterstitial flurryAdInterstitial) {
                Log.i(TAG, "Full screen ad fetched");
                flurryAdInterstitial.displayAd();
            }

            @Override
            public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onClose(FlurryAdInterstitial flurryAdInterstitial) {
                InterstitialOnExitActivity.super.onBackPressed();
            }

            @Override
            public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {

            }

            @Override
            public void onError(FlurryAdInterstitial flurryAdInterstitial,
                                FlurryAdErrorType flurryAdErrorType, int i) {
                Log.e(TAG, "Full screen ad load error - Error type: " + flurryAdErrorType + " Code: " + i);
            }
        });
        flurryAdInterstitial.fetchAd();
    }
}
