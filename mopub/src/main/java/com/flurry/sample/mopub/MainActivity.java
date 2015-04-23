package com.flurry.sample.mopub;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;

import java.util.Arrays;

public class MainActivity extends ActionBarActivity
        implements MoPubInterstitial.InterstitialAdListener, MoPubView.BannerAdListener {

    private MoPubInterstitial mInterstitial;
    private MoPubView mBanner;
    private ListView mListView;
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, android.R.id.text1,
                Arrays.asList(new String[]{
                        "Test native ads",
                        "Test interstitial ads",
                        "Test banner ads",
                })
        );

        mListView = (ListView)findViewById(R.id.list_view);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, MopubNativeActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        mListView.setEnabled(false);
                        mInterstitial.load();
                        Toast.makeText(MainActivity.this, "Loading interstitial ad", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        mBanner.loadAd();
                        break;
                }
            }
        });

        mInterstitial = new MoPubInterstitial(this, "YOUR_MOPUB_INTERSTITIAL_AD_UNIT_ID");
        mInterstitial.setInterstitialAdListener(this);
        mBanner = (MoPubView)findViewById(R.id.mopub_banner);
        mBanner.setAdUnitId("YOUR_MOPUB_BANNER_AD_UNIT_ID");
        mBanner.setBannerAdListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInterstitial.destroy();
        mBanner.destroy();
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

    //region MoPubInterstitial.InterstitialAdListener implementation
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        if (mInterstitial.isReady()) {
            Log.w(TAG, "Interstitial ad is loaded");
            mInterstitial.show();
        } else {
            Toast.makeText(this, "Interstitial ad is loaded but not ready", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Interstitial ad is loaded but not ready");
        }
        mListView.setEnabled(true);
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        Toast.makeText(this, "Interstitial ad failed to load. Error code: " + errorCode,
                Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Interstitial ad failed to load. Error code: " + errorCode);
        mListView.setEnabled(true);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        Toast.makeText(this, "Interstitial ad is visible", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Interstitial ad is visible");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        Toast.makeText(this, "Interstitial ad clicked", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Interstitial ad clicked");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        Toast.makeText(this, "Interstitial ad dismissed", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Interstitial ad dismissed");
    }
    //endregion

    //region MoPubView.BannerAdListener implementation
    @Override
    public void onBannerLoaded(MoPubView banner) {
        Log.w(TAG, "Banner ad is loaded");
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Toast.makeText(this, "Banner ad failed to load. Error code: " + errorCode,
                Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Banner ad failed to load. Error code: " + errorCode);
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        Toast.makeText(this, "Banner ad clicked", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Banner ad clicked");
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {

    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {

    }
    //endregion
}
