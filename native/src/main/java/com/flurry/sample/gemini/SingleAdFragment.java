package com.flurry.sample.gemini;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;

public class SingleAdFragment extends BaseAdFragment {

    private FlurryAdNative mFlurryAdNative;
    // Assets documented here: https://developer.yahoo.com/flurry/docs/publisher/code/android/
    private static final String AD_ASSET_SUMMARY = "summary";
    private static final String AD_ASSET_HEADLINE = "headline";
    private static final String AD_ASSET_SOURCE = "source";
    private static final String AD_ASSET_SEC_HQ_BRANDING_LOGO = "secHqBrandingLogo";
    private static final String AD_ASSET_SEC_HQ_RATING_IMAGE = "secHqRatingImg";
    private static final String AD_ASSET_SHOW_RATING = "showRating";
    private static final String AD_ASSET_SEC_HQ_IMAGE = "secHqImage";
    private static final String AD_ASSET_SEC_IMAGE = "secImage";
    private static final String AD_ASSET_VIDEO_URL = "videoUrl";

    static final String TAG = SingleAdFragment.class.getSimpleName();

    public static final SingleAdFragment newInstance() {
        return new SingleAdFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_ad, container, false);

        final TextView adSourceTxt = (TextView)rootView.findViewById(R.id.ad_source);
        final TextView adHeadlineTxt = (TextView)rootView.findViewById(R.id.ad_headline);
        final TextView adDescription = (TextView)rootView.findViewById(R.id.ad_description);
        final ViewGroup adVideo = (ViewGroup)rootView.findViewById(R.id.ad_video);
        final ImageView adImage = (ImageView)rootView.findViewById(R.id.ad_image);
        final ImageView adSponsorImg = (ImageView)rootView.findViewById(R.id.sponsored_image);
        final ImageView adAppRatingImg = (ImageView)rootView.findViewById(R.id.app_rating_image);

        final Button renderAdBtn = (Button)rootView.findViewById(R.id.render_ad_btn);
        final NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onFetched(FlurryAdNative flurryAdNative) {
                super.onFetched(flurryAdNative);

                renderAdBtn.setEnabled(true);
            }

            @Override
            public void onError(FlurryAdNative flurryAdNative,
                                FlurryAdErrorType flurryAdErrorType, int errorCode) {
                super.onError(flurryAdNative, flurryAdErrorType, errorCode);

                renderAdBtn.setEnabled(false);
            }
        };

        rootView.findViewById(R.id.fetch_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlurryAdNative != null) { mFlurryAdNative.destroy(); }

                mFlurryAdNative = new FlurryAdNative(getActivity(), AD_SPACE_NAME);
                mFlurryAdNative.setListener(nativeAdListener);
                mFlurryAdNative.fetchAd();
            }
        });
        renderAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlurryAdNative.getAsset(AD_ASSET_SOURCE).loadAssetIntoView(adSourceTxt);
                mFlurryAdNative.getAsset(AD_ASSET_HEADLINE).loadAssetIntoView(adHeadlineTxt);
                mFlurryAdNative.getAsset(AD_ASSET_SUMMARY).loadAssetIntoView(adDescription);
                if (mFlurryAdNative.isVideoAd()) {
                    mFlurryAdNative.getAsset(AD_ASSET_VIDEO_URL).loadAssetIntoView(adVideo);
                }
                if (mFlurryAdNative.getAsset(AD_ASSET_SEC_HQ_IMAGE) != null) {
                    mFlurryAdNative.getAsset(AD_ASSET_SEC_HQ_IMAGE).loadAssetIntoView(adImage);
                } else if (mFlurryAdNative.getAsset(AD_ASSET_SEC_IMAGE) != null) {
                    mFlurryAdNative.getAsset(AD_ASSET_SEC_IMAGE).loadAssetIntoView(adImage);
                }
                mFlurryAdNative.getAsset(AD_ASSET_SEC_HQ_BRANDING_LOGO).loadAssetIntoView(adSponsorImg);
                if (mFlurryAdNative.getAsset(AD_ASSET_SHOW_RATING) != null &&
                        mFlurryAdNative.getAsset(AD_ASSET_SHOW_RATING).equals("true")) {
                    mFlurryAdNative.getAsset(AD_ASSET_SEC_HQ_RATING_IMAGE).loadAssetIntoView(adAppRatingImg);
                }

                renderAdBtn.setEnabled(false);
            }
        });

        return rootView;
    }
}
