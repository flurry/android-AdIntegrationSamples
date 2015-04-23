package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.mopub.mobileads.FlurryAgentWrapper;

import java.util.ArrayList;
import java.util.List;

public class FlurryForwardingNativeAd extends BaseForwardingNativeAd {

    private static final String kLogTag = FlurryForwardingNativeAd.class.getSimpleName();
    private static final int IMPRESSION_VIEW_MIN_TIME = 1000;

    private final Context mContext;
    private final CustomEventNative.CustomEventNativeListener mCustomEventNativeListener;
    private final FlurryForwardingNativeAd mFlurryForwardingNativeAd;

    private FlurryAdNative nativeAd;

    FlurryForwardingNativeAd(Context context, FlurryAdNative adNative, CustomEventNative.CustomEventNativeListener mCustomEventNativeListener) {
        this.mContext = context;
        this.nativeAd = adNative;
        this.mCustomEventNativeListener = mCustomEventNativeListener;
        this.mFlurryForwardingNativeAd = this;
    }

    public synchronized void fetchAd() {
        Context context = mContext;
        if (context != null) {
            Log.d(kLogTag, "Fetching Flurry Native Ad now.");
            nativeAd.setListener(listener);
            nativeAd.fetchAd();
        } else {
            Log.d(kLogTag, "Context is null, not fetching Flurry Native Ad.");
        }
    }

    private synchronized void onFetched(FlurryAdNative adNative) {
        if (adNative != null) {
            Log.d(kLogTag, "FlurryForwardingNativeAd onFetched: Native Ad fetched successfully! "+ adNative.toString());
            setupNativeAd(adNative);
        }
    }

    private synchronized void onFetchFailed(FlurryAdNative adNative) {
        Log.d(kLogTag, "FlurryForwardingNativeAd onFetchFailed: Native ad not available. " + adNative.toString());
        if (mCustomEventNativeListener != null) {
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    private synchronized void setupNativeAd(FlurryAdNative adNative) {
        if (adNative != null) {
            nativeAd = adNative;
            FlurryAdNativeAsset coverImageAsset = nativeAd.getAsset("secHqImage");
            FlurryAdNativeAsset iconImageAsset = nativeAd.getAsset("secHqBrandingLogo");

            if (coverImageAsset != null && !TextUtils.isEmpty(coverImageAsset.getValue())) {
                setMainImageUrl(coverImageAsset.getValue());
            }
            if (iconImageAsset != null && !TextUtils.isEmpty(iconImageAsset.getValue())) {
                setIconImageUrl(iconImageAsset.getValue());
            }

            setTitle(nativeAd.getAsset("headline").getValue());
            setText(nativeAd.getAsset("summary").getValue());

            //setCallToAction(CALL_TO_ACTION);
            setImpressionMinTimeViewed(IMPRESSION_VIEW_MIN_TIME);
            setOverridingClickTracker(true);
            setOverridingImpressionTracker(true);

            if (getImageUrls() == null || getImageUrls().isEmpty()) {
                Log.d(kLogTag, "preCacheImages: No images to cache. Flurry Ad Native: " + nativeAd.toString());
                mCustomEventNativeListener.onNativeAdLoaded(mFlurryForwardingNativeAd);
            } else {
                preCacheImages(mContext, getImageUrls(), new CustomEventNative.ImageListener() {
                    @Override
                    public void onImagesCached() {
                        if (mCustomEventNativeListener != null) {
                            Log.d(kLogTag, "preCacheImages: Ad image cached.");
                            mCustomEventNativeListener.onNativeAdLoaded(mFlurryForwardingNativeAd);
                        }
                        else{
                            Log.d(kLogTag, "Unable to notify cache failure: CustomEventNativeListener is null.");
                        }
                    }

                    @Override
                    public void onImagesFailedToCache(NativeErrorCode errorCode) {
                        if (mCustomEventNativeListener != null) {
                            Log.d(kLogTag, "preCacheImages: Unable to cache Ad image. Error[" + errorCode.toString()+ "]");
                            mCustomEventNativeListener.onNativeAdFailed(errorCode);
                        }
                        else{
                            Log.d(kLogTag, "Unable to notify cache failure: CustomEventNativeListener is null.");
                        }
                    }
                });
            }
        }
        else{
            Log.d(kLogTag, "Flurry Native Ad setup failed: ad object is null.");
        }
    }

    private List<String> getImageUrls() {
        final List<String> imageUrls = new ArrayList<String>(2);
        final String mainImageUrl = getMainImageUrl();

        if (mainImageUrl != null) {
            imageUrls.add(getMainImageUrl());
            Log.d(kLogTag, "Flurry Native Ad main image found.");
        }

        final String iconUrl = getIconImageUrl();
        if (iconUrl != null) {
            imageUrls.add(this.getIconImageUrl());
            Log.d(kLogTag, "Flurry Native Ad icon image found.");
        }
        return imageUrls;
    }

    // BaseForwardingNativeAd
    @Override
    public void prepare(final View view) {
        super.prepare(view);
        nativeAd.setTrackingView(view);
        Log.d(kLogTag, "prepare(" + nativeAd.toString() + " " + view.toString() + ")");
    }

    @Override
    public void clear(@Nullable View view) {
        super.clear(view);
        nativeAd.removeTrackingView();
        Log.d(kLogTag, "clear("+ nativeAd.toString() + " " + view.toString() + ")");
    }

    @Override
    public void destroy() {
        Log.d(kLogTag, "destroy(" +nativeAd.toString() + ") started.");
        super.destroy();
        nativeAd.destroy();

        // Not needed for Flurry Analytics users
        FlurryAgentWrapper.getInstance().onEndSession(mContext);
    }

    FlurryAdNativeListener listener = new FlurryAdNativeListener() {
        @Override
        public void onFetched(FlurryAdNative adNative) {
            Log.d(kLogTag, "onFetched(" +adNative.toString() + ") Successful.");
            mFlurryForwardingNativeAd.onFetched(adNative);
        }

        @Override
        public void onError(FlurryAdNative adNative, FlurryAdErrorType adErrorType, int errorCode) {
            if (adErrorType.equals(FlurryAdErrorType.FETCH)) {
                Log.d(kLogTag, "onError(" + adNative.toString() + ", " + adErrorType.toString() +","+ errorCode + ")");
                mFlurryForwardingNativeAd.onFetchFailed(adNative);
            }
        }

        @Override
        public void onShowFullscreen(FlurryAdNative adNative) {
            Log.d(kLogTag, "onShowFullscreen(" + adNative.toString() + ")");
        }

        @Override
        public void onCloseFullscreen(FlurryAdNative adNative) {
            Log.d(kLogTag, "onCloseFullscreen(" + adNative.toString() + ")");
        }

        @Override
        public void onClicked(FlurryAdNative adNative) {
            Log.d(kLogTag, "onClicked(" +adNative.toString() + ") Successful.");
            notifyAdClicked();
        }

        @Override
        public void onImpressionLogged(FlurryAdNative flurryAdNative) {
            Log.d(kLogTag, "onImpressionLogged(" +flurryAdNative.toString() + ")  Successful.");
            notifyAdImpressed();
        }

        @Override
        public void onAppExit(FlurryAdNative adNative) {
            Log.d(kLogTag, "onAppExit(" + adNative.toString() + ")");
        }
    };
}