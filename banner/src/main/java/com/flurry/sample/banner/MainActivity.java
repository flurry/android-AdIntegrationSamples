package com.flurry.sample.banner;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;

public class MainActivity extends ActionBarActivity {

    public static final String STATE_PERSIST_STANDARD = "com.flurry.sample.banner.persiststandard";
    private FlurryAdBanner mFlurryAdStandardBanner = null;
    private final static String TAG = MainActivity.class.getSimpleName();
    private boolean mShouldRestoreStandardBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mShouldRestoreStandardBanner = savedInstanceState
                    .getBoolean(STATE_PERSIST_STANDARD, false);

            if (mShouldRestoreStandardBanner) {
                loadStandardBanner();
            }
        }

        findViewById(R.id.standard_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShouldRestoreStandardBanner = true;

                if (mFlurryAdStandardBanner != null) {
                    mFlurryAdStandardBanner.destroy();
                    ((ViewGroup) findViewById(R.id.banner_layout)).removeAllViews();
                }
                loadStandardBanner();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_PERSIST_STANDARD, mShouldRestoreStandardBanner);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        if (mFlurryAdStandardBanner != null) {
            mFlurryAdStandardBanner.destroy();
        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadStandardBanner() {
        ViewGroup bannerAdLayout = (ViewGroup) findViewById(R.id.banner_layout);
        mFlurryAdStandardBanner = new FlurryAdBanner(MainActivity.this, bannerAdLayout,
                "YOUR_AD_SPACE_NAME");
        mFlurryAdStandardBanner.setListener(mAdBannerListener);
        Log.i(TAG, "Fetching banner ad");

        mFlurryAdStandardBanner.fetchAd();
    }

    FlurryAdBannerListener mAdBannerListener = new FlurryAdBannerListener() {
        @Override
        public void onFetched(FlurryAdBanner flurryAdBanner) {
            Log.i(TAG, "Banner ad fetched");
            mFlurryAdStandardBanner.displayAd();
        }

        @Override
        public void onRendered(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onShowFullscreen(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onCloseFullscreen(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onAppExit(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onClicked(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onVideoCompleted(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onError(FlurryAdBanner flurryAdBanner, FlurryAdErrorType flurryAdErrorType, int i) {
            Log.e(TAG, "Banner ad load error - Error type: " + flurryAdErrorType + " Code: " + i);
            mFlurryAdStandardBanner.destroy();
        }
    };
}
