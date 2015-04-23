package com.flurry.sample.mopub;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.util.Arrays;

public class MopubNativeActivity extends ActionBarActivity {

    MoPubAdAdapter mAdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ads);

        setupStreamAds();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdAdapter.loadAds("YOUR_MOPUB_NATIVE_AD_UNIT_ID");
    }

    private void setupStreamAds() {
        ViewBinder mViewBinder = new ViewBinder.Builder(R.layout.list_item_native_ads)
                .iconImageId(R.id.native_ad_icon_image)
                .mainImageId(R.id.native_ad_main_image)
                .titleId(R.id.native_ad_title)
                .textId(R.id.native_ad_text)
                .build();

        MoPubAdRenderer mAdRenderer = new MoPubNativeAdRenderer(mViewBinder);
        MoPubNativeAdPositioning.MoPubClientPositioning mAdPositioning =
                MoPubNativeAdPositioning.clientPositioning()
                .addFixedPosition(2)
                .enableRepeatingPositions(4);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                MopubNativeActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1,
                Arrays.asList(new String[]{
                        "Filler data 1", "Filler data 2", "Filler data 3", "Filler data 4",
                        "Filler data 5", "Filler data 6", "Filler data 7", "Filler data 8",
                        "Filler data 9", "Filler data 10", "Filler data 11", "Filler data 12",
                        "Filler data 13", "Filler data 14", "Filler data 15", "Filler data 16",
                        "Filler data 17", "Filler data 18", "Filler data 19", "Filler data 20"}));
        mAdAdapter = new MoPubAdAdapter(MopubNativeActivity.this, adapter, mAdPositioning);
        mAdAdapter.registerAdRenderer(mAdRenderer);

        ListView lv = (ListView)findViewById(R.id.lv_native);
        lv.setAdapter(mAdAdapter);
    }
}
