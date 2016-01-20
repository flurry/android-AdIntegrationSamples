package com.flurry.sample.interstitial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.flurry.android.ads.FlurryAdTargeting;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FlurryAdInterstitial mFlurryAdInterstitial;
    ListView mListView;
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayAdapter<SimpleAdSpaceConfig> adapter = new ArrayAdapter<SimpleAdSpaceConfig>(
                this, android.R.layout.simple_list_item_2, android.R.id.text1,
                // NOTE: Use your own Flurry ad space names. This is left here to make sample review easier
                Arrays.asList(new SimpleAdSpaceConfig[]{
                        new SimpleAdSpaceConfig("Basic interstitial ad", "InterstitialTest"),
                        new SimpleAdSpaceConfig("Skippable video ad", "SkippableVideoTest"),
                        new SimpleAdSpaceConfig("Unskippable video ad", "UnskippableVideoTest"),
                        new SimpleAdSpaceConfig("Client-side rewarded ad", "ClientSideRewardedAd"),
                        new SimpleAdSpaceConfig("Test-mode video ad", "TestModeVideoTest"),
                        new SimpleAdSpaceConfig("Interstitial on Activity finish", "InterstitialTest"),
                })
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(getItem(position).mAdSpaceDescription);
                text2.setText((getItem(position)).mAdSpaceName);

                return view;
            }
        };

        mListView = (ListView)findViewById(R.id.list_view);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 5) {
                    Intent intent = new Intent(MainActivity.this, InterstitialOnExitActivity.class);
                    startActivity(intent);
                } else {
                    Log.i(TAG, "Loading full screen ad");
                    String clickedAdSpaceName = adapter.getItem(position).mAdSpaceName;
                    mFlurryAdInterstitial = new FlurryAdInterstitial(MainActivity.this, clickedAdSpaceName);
                    mFlurryAdInterstitial.setListener(mAdInterstitialListener);

                    if (position == 4) { // Enable test-mode for consistent ad fill.
                        FlurryAdTargeting testTarget = new FlurryAdTargeting();
                        testTarget.setEnableTestAds(true);
                        mFlurryAdInterstitial.setTargeting(testTarget);
                    }

                    mFlurryAdInterstitial.fetchAd();

                    mListView.setEnabled(false);

                    Toast.makeText(MainActivity.this, "Please wait for ad", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mFlurryAdInterstitial != null) {
            mFlurryAdInterstitial.destroy();
        }

        super.onDestroy();
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

    FlurryAdInterstitialListener mAdInterstitialListener = new FlurryAdInterstitialListener() {
        @Override
        public void onFetched(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "Full screen ad fetched");
            mListView.setEnabled(true);
            flurryAdInterstitial.displayAd();
        }

        @Override
        public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "Ad rendered");
        }

        @Override
        public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "Ad displayed");
        }

        @Override
        public void onClose(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "Ad closed");
        }

        @Override
        public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "App closing");
        }

        @Override
        public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "Ad clicked");
        }

        @Override
        public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {
            Log.i(TAG, "Video is completed");
            Toast.makeText(MainActivity.this, "Video completed, where's my reward",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FlurryAdInterstitial flurryAdInterstitial, FlurryAdErrorType flurryAdErrorType, int i) {
            Log.e(TAG, "Full screen ad load error - Error type: " + flurryAdErrorType + " Code: " + i);
            Toast.makeText(MainActivity.this, "Ad load failed - try again", Toast.LENGTH_SHORT).show();
            mListView.setEnabled(true);
        }
    };

    class SimpleAdSpaceConfig {

        SimpleAdSpaceConfig(String adSpaceDescription, String adSpaceName) {
            this.mAdSpaceDescription = adSpaceDescription;
            this.mAdSpaceName = adSpaceName;
        }

        String mAdSpaceDescription;
        String mAdSpaceName;
    }
}
