package com.mopub.nativeads;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class FlurryStaticNativeAd extends StaticNativeAd {

    private static final String kLogTag = FlurryStaticNativeAd.class.getSimpleName();
    private static final int IMPRESSION_VIEW_MIN_TIME = 1000;
    private static final String CALL_TO_ACTION = "callToAction";

    private final Context mContext;
    private final CustomEventNative.CustomEventNativeListener mCustomEventNativeListener;
    private final FlurryStaticNativeAd mFlurryStaticNativeAd;

    private static final String ASSET_SEC_HQ_IMAGE = "secHqImage";
    private static final String ASSET_SEC_IMAGE = "secImage";
    private static final String ASSET_SEC_HQ_RATING_IMG = "secHqRatingImg";
    private static final String ASSET_SEC_RATING_IMG = "secRatingImg";
    private static final String ASSET_APP_RATING = "appRating";
    private static final String ASSET_APP_CATEGORY = "appCategory";
    private static final String ASSET_HEADLINE = "headline";
    private static final String ASSET_SUMMARY = "summary";
    private static final double MOPUB_STAR_RATING_SCALE = StaticNativeAd.MAX_STAR_RATING;

    public static final String EXTRA_STAR_RATING_IMG = "starratingimage";
    public static final String EXTRA_APP_CATEGORY = "appcategory";

    private FlurryAdNative nativeAd;

    FlurryStaticNativeAd(Context context, FlurryAdNative adNative,
                         CustomEventNative.CustomEventNativeListener mCustomEventNativeListener) {
        this.mContext = context;
        this.nativeAd = adNative;
        this.mCustomEventNativeListener = mCustomEventNativeListener;
        this.mFlurryStaticNativeAd = this;
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
            Log.d(kLogTag, "onFetched: Native Ad fetched successfully!"
                    + adNative.toString());
            setupNativeAd(adNative);
        }
    }

    private synchronized void onFetchFailed(FlurryAdNative adNative) {
        Log.d(kLogTag, "onFetchFailed: Native ad not available. "
                + adNative.toString());
        if (mCustomEventNativeListener != null) {
            mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }
    }

    private synchronized void setupNativeAd(FlurryAdNative adNative) {
        if (adNative != null) {
            nativeAd = adNative;
            FlurryAdNativeAsset coverImageAsset = nativeAd.getAsset(ASSET_SEC_HQ_IMAGE);
            FlurryAdNativeAsset iconImageAsset = nativeAd.getAsset(ASSET_SEC_IMAGE);

            if (coverImageAsset != null && !TextUtils.isEmpty(coverImageAsset.getValue())) {
                setMainImageUrl(coverImageAsset.getValue());
            }
            if (iconImageAsset != null && !TextUtils.isEmpty(iconImageAsset.getValue())) {
                setIconImageUrl(iconImageAsset.getValue());
            }

            setTitle(nativeAd.getAsset(ASSET_HEADLINE).getValue());
            setText(nativeAd.getAsset(ASSET_SUMMARY).getValue());

            if(isAppInstallAd()) {
                // App rating image URL may be null
                FlurryAdNativeAsset ratingHqImageAsset = nativeAd.getAsset(ASSET_SEC_HQ_RATING_IMG);
                if (ratingHqImageAsset != null && !TextUtils.isEmpty(ratingHqImageAsset.getValue())) {
                    addExtra(EXTRA_STAR_RATING_IMG, ratingHqImageAsset.getValue());
                } else {
                    FlurryAdNativeAsset ratingImageAsset = nativeAd.getAsset(ASSET_SEC_RATING_IMG);
                    if (ratingImageAsset != null && !TextUtils.isEmpty(ratingImageAsset.getValue())) {
                        addExtra(EXTRA_STAR_RATING_IMG, ratingImageAsset.getValue());
                    }
                }

                FlurryAdNativeAsset appCategoryAsset = nativeAd.getAsset(ASSET_APP_CATEGORY);
                if (appCategoryAsset != null) {
                    addExtra(EXTRA_APP_CATEGORY, appCategoryAsset.getValue());
                }
                FlurryAdNativeAsset appRatingAsset = nativeAd.getAsset(ASSET_APP_RATING);
                if(appRatingAsset != null) {
                    setStarRating(getStarRatingValue(appRatingAsset.getValue()));
                }
            }

            FlurryAdNativeAsset ctaAsset = nativeAd.getAsset(CALL_TO_ACTION);
            if(ctaAsset != null){
                setCallToAction(ctaAsset.getValue());
            }

            setImpressionMinTimeViewed(IMPRESSION_VIEW_MIN_TIME);

            if (getImageUrls() == null || getImageUrls().isEmpty()) {
                Log.d(kLogTag, "preCacheImages: No images to cache. Flurry Ad Native: " + nativeAd.toString());
                mCustomEventNativeListener.onNativeAdLoaded(mFlurryStaticNativeAd);
            } else {
                NativeImageHelper.preCacheImages(mContext, getImageUrls(), new NativeImageHelper.ImageListener() {
                    @Override
                    public void onImagesCached() {
                        if (mCustomEventNativeListener != null) {
                            Log.d(kLogTag, "preCacheImages: Ad image cached.");
                            mCustomEventNativeListener.onNativeAdLoaded(mFlurryStaticNativeAd);
                        } else {
                            Log.d(kLogTag, "Unable to notify cache failure: CustomEventNativeListener is null.");
                        }
                    }

                    @Override
                    public void onImagesFailedToCache(NativeErrorCode errorCode) {
                        if (mCustomEventNativeListener != null) {
                            Log.d(kLogTag, "preCacheImages: Unable to cache Ad image. Error[" + errorCode.toString() + "]");
                            mCustomEventNativeListener.onNativeAdFailed(errorCode);
                        } else {
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

    private Double getStarRatingValue(@Nullable String appRatingString) {
        // App rating String should be of the form X/Y. E.g. 80/100
        Double rating = null;
        if (appRatingString != null) {
            String[] ratingParts = appRatingString.split("/");
            if (ratingParts.length == 2) {
                try {
                    float numer = Integer.valueOf(ratingParts[0]);
                    float denom = Integer.valueOf(ratingParts[1]);
                    rating = (numer / denom) * MOPUB_STAR_RATING_SCALE;
                } catch (NumberFormatException e) { /*Ignore and return null*/ }
            }
        }
        return rating;
    }

    private boolean isAppInstallAd() {
        return nativeAd.getAsset(ASSET_SEC_RATING_IMG) != null || nativeAd.getAsset(ASSET_SEC_HQ_RATING_IMG) != null
                || nativeAd.getAsset(ASSET_APP_CATEGORY) != null;
    }

    // BaseForwardingNativeAd
    @Override
    public void prepare(@NonNull final View view) {
        super.prepare(view);
        nativeAd.setTrackingView(view);
        Log.d(kLogTag, "prepare(" + nativeAd.toString() + " " + view.toString() + ")");
    }

    @Override
    public void clear(@NonNull View view) {
        super.clear(view);
        nativeAd.removeTrackingView();
        Log.d(kLogTag, "clear("+ nativeAd.toString() + ")");
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
            mFlurryStaticNativeAd.onFetched(adNative);
        }

        @Override
        public void onError(FlurryAdNative adNative, FlurryAdErrorType adErrorType, int errorCode) {
            if (adErrorType.equals(FlurryAdErrorType.FETCH)) {
                Log.d(kLogTag, "onError(" + adNative.toString() + ", " + adErrorType.toString() +","+ errorCode + ")");
                mFlurryStaticNativeAd.onFetchFailed(adNative);
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

        @Override
        public void onCollapsed(FlurryAdNative adNative) {
            Log.d(kLogTag, "onCollapsed(" + adNative.toString() + ")");
        }

        @Override
        public void onExpanded(FlurryAdNative adNative) {
            Log.d(kLogTag, "onExpanded(" + adNative.toString() + ")");
        }
    };
}