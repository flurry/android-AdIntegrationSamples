package com.flurry.sample.gemini;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;

public abstract class BaseAdFragment extends Fragment {

    static final String TAG = BaseAdFragment.class.getSimpleName();

    /*
    One ad space can fetch either static or video native ads if enabled on Flurry dashboard.

    NOTE: Use your own Flurry ad space. This is left here to make sample review easier
     */
    protected static final String AD_SPACE_NAME = "StaticVideoNativeTest";

    protected class NativeAdListener implements FlurryAdNativeListener {

        @Override
        public void onFetched(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onFetched callback called with Native ad object: " + flurryAdNative);
        }

        @Override
        public void onShowFullscreen(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onShowFullscreen callback called");
        }

        @Override
        public void onCloseFullscreen(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onCloseFullscreen callback called");
        }

        @Override
        public void onAppExit(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onAppExit callback called");

        }

        @Override
        public void onImpressionLogged(FlurryAdNative adNative) {
            Log.i(TAG, "onImpressionLogged callback called");
        }

        @Override
        public void onClicked(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onClicked callback called");
        }

        @Override
        public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType,
                            int errorCode) {
            Log.e(TAG, String.format("onError called. Error type: %s. Error code: %d",
                    flurryAdErrorType, errorCode));
        }

        @Override
        public void onCollapsed(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onCollapsed callback called");
        }

        @Override
        public void onExpanded(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onExpanded callback called");
        }
    }
}
