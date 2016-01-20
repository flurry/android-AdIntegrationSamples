package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;

import java.util.Map;

import static com.mopub.mobileads.MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
import static com.mopub.mobileads.MoPubErrorCode.NETWORK_INVALID_STATE;
import static com.mopub.mobileads.MoPubErrorCode.NETWORK_NO_FILL;

public class FlurryCustomEventInterstitial extends com.mopub.mobileads.CustomEventInterstitial {
    public static final String LOG_TAG = FlurryCustomEventInterstitial.class.getSimpleName();

    private static final String API_KEY = "apiKey";
    private static final String AD_SPACE_NAME = "adSpaceName";

    private Context mContext;
    private CustomEventInterstitialListener mListener;

    private String mApiKey;
    private String mAdSpaceName;

    private FlurryAdInterstitial mInterstitial;

    public FlurryCustomEventInterstitial() {
        super();
    }

    // CustomEventInterstitial
    @Override
    protected void loadInterstitial(Context context,
                                    CustomEventInterstitialListener listener,
                                    Map<String, Object> localExtras, Map<String, String> serverExtras) {
        if (context == null) {
            Log.e(LOG_TAG, "Context cannot be null.");
            listener.onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (listener == null) {
            Log.e(LOG_TAG, "CustomEventInterstitialListener cannot be null.");
            return;
        }

        if (!(context instanceof Activity)) {
            Log.e(LOG_TAG, "Ad can be rendered only in Activity context.");
            listener.onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (!extrasAreValid(serverExtras)) {
            listener.onInterstitialFailed(ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mContext = context;
        mListener = listener;

        mApiKey = serverExtras.get(API_KEY);
        mAdSpaceName = serverExtras.get(AD_SPACE_NAME);

        // Not needed for Flurry Analytics users
        FlurryAgentWrapper.getInstance().onStartSession(context, mApiKey);

        Log.d(LOG_TAG, "fetch Flurry ad (" + mAdSpaceName + ")");
        mInterstitial = new FlurryAdInterstitial(mContext, mAdSpaceName);
        mInterstitial.setListener(new FlurryMopubInterstitialListener());
        mInterstitial.fetchAd();
    }

    @Override
    protected void onInvalidate() {
        if (mContext == null) {
            return;
        }

        Log.d(LOG_TAG, "MoPub issued onInvalidate (" + mAdSpaceName + ")");

        if (mInterstitial != null) {
            mInterstitial.destroy();
            mInterstitial = null;
        }

        // Not needed for Flurry Analytics users
        FlurryAgentWrapper.getInstance().onEndSession(mContext);

        mContext = null;
        mListener = null;
    }

    private boolean extrasAreValid(Map<String, String> serverExtras) {
        if (serverExtras == null) {
            return false;
        }

        return serverExtras.containsKey(API_KEY) && serverExtras.containsKey(AD_SPACE_NAME);
    }

    @Override
    protected void showInterstitial() {
        Log.d(LOG_TAG, "MoPub issued showInterstitial (" + mAdSpaceName + ")");

        if (mInterstitial != null) {
            mInterstitial.displayAd();
        }
    }

    // FlurryAdListener
    private class FlurryMopubInterstitialListener implements FlurryAdInterstitialListener {
        private final String LOG_TAG = getClass().getSimpleName();

        @Override
        public void onFetched(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onFetched(" + adInterstitial.toString() + ")");

            if (mListener != null) {
                mListener.onInterstitialLoaded();
            }
        }

        @Override
        public void onRendered(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onRendered(" + adInterstitial.toString() + ")");

            if (mListener != null) {
                mListener.onInterstitialShown();
            }
        }

        @Override
        public void onDisplay(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onDisplay(" + adInterstitial.toString() + ")");

            // no-op
        }

        @Override
        public void onClose(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onClose(" + adInterstitial.toString() + ")");

            if (mListener != null) {
                mListener.onInterstitialDismissed();
            }
        }

        @Override
        public void onAppExit(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onAppExit(" + adInterstitial.toString() + ")");

            if (mListener != null) {
                mListener.onLeaveApplication();
            }
        }

        @Override
        public void onClicked(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onClicked " + adInterstitial.toString());

            if (mListener != null) {
                mListener.onInterstitialClicked();
            }
        }

        @Override
        public void onVideoCompleted(FlurryAdInterstitial adInterstitial) {
            Log.d(LOG_TAG, "onVideoCompleted " + adInterstitial.toString());

            // no-op
        }

        @Override
        public void onError(FlurryAdInterstitial adBanner, FlurryAdErrorType adErrorType, int errorCode) {
            Log.d(LOG_TAG, "onError(" + adBanner.toString() + adErrorType.toString() + errorCode + ")");

            if (mListener != null) {
                if (FlurryAdErrorType.FETCH.equals(adErrorType)) {
                    mListener.onInterstitialFailed(NETWORK_NO_FILL);
                } else if (FlurryAdErrorType.RENDER.equals(adErrorType)) {
                    mListener.onInterstitialFailed(NETWORK_INVALID_STATE);
                }
            }
        }
    }
}