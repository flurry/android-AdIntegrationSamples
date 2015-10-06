package com.flurry.sample.gemini;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.sample.gemini.entities.NewsArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamListViewFragment extends Fragment {
    public static final String TAG = StreamListViewFragment.class.getSimpleName();

    private ListView mListView;
    private List<NewsArticle> mArticles;
    private final static int ARTICLES_TO_LOAD = 20;
    private final static int MAX_ADS_TO_FETCH = 5;
    private final static int MAX_FETCH_ATTEMPT = 10;

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_AD = 1;

    private int mFetchAttempts;
    /*
     List containing ads that are currently being fetched. Hold them to prevent GC while the ad
     request is being fulfilled.
     */
    private List<FlurryAdNative> mNativeAdsToFetch;
    // List containing ads that have been successfully fetched
    private List<FlurryAdNative> mFetchedNativeAds;
    private NativeAdListener mNativeAdListener = new NativeAdListener();

    /*
    One ad space can fetch either static or video native ads if enabled on Flurry dashboard.

    NOTE: Use your own Flurry ad space. This is left here to make sample review easier
     */
    private static final String AD_SPACE_NAME = "StaticVideoNativeTest";

    private final String AD_ASSET_SUMMARY = "summary";
    private final String AD_ASSET_HEADLINE = "headline";
    private final String AD_ASSET_SOURCE = "source";
    private final String AD_ASSET_SECURE_HQ_IMAGE = "secHqImage";
    private final String AD_ASSET_SECURE_BRAND_LOGO = "secBrandingLogo";
    // For app-install ads
    private final String AD_ASSET_SECURE_HQ_RATING_IMAGE = "secHqRatingImg";
    // For native video ads
    private final String AD_ASSET_VIDEO_URL = "videoUrl";
    // Other (unused) assets
    @SuppressWarnings("unused")
    private final String AD_ASSET_IMAGE = "image";
    @SuppressWarnings("unused")
    private final String AD_ASSET_HQ_IMAGE = "hqImage";
    @SuppressWarnings("unused")
    private final String AD_ASSET_ORIG_IMAGE = "origImg";
    @SuppressWarnings("unused")
    private final String AD_ASSET_SECURE_ORIG_BRAND_LOGO = "secOrigImg";
    @SuppressWarnings("unused")
    private final String AD_ASSET_SECURE_IMAGE = "secImage";
    @SuppressWarnings("unused")
    private final String AD_ASSET_SECURE_HQ_BRAND_LOGO = "secHqBrandingLogo";
    // Unused for app-install ads
    @SuppressWarnings("unused")
    private final String AD_ASSET_SECURE_RATING_IMAGE = "secRatingImg";
    @SuppressWarnings("unused")
    private final String AD_ASSET_APP_CATEGORY = "appCategory";
    @SuppressWarnings("unused")
    private final String AD_ASSET_APP_RATING = "appRating";


    public static final StreamListViewFragment newInstance() {
        return new StreamListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_stream, container, false);

        mListView = (ListView)rootView.findViewById(android.R.id.list);
        mNativeAdsToFetch = new ArrayList<>();
        mFetchedNativeAds = new ArrayList<>();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setAdapter(new StreamArrayAdapter());

        mArticles = new ArrayList<>(ARTICLES_TO_LOAD);

        loadArticles();
        // This listener is registered before Activity#onStart() may be called in the lifecycle
        FlurryAgent.setFlurryAgentListener(new FlurryAgentListener() {
            @Override
            public void onSessionStarted() {
                // Only fetch new ads after session has started
                Log.i(TAG, "Session started with ID " + FlurryAgent.getSessionId());
                fetchNewAds();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (getActivity().isFinishing()) {
            for (FlurryAdNative adNative : mNativeAdsToFetch) {
                adNative.destroy();
            }

            for (FlurryAdNative adNative : mFetchedNativeAds) {
                adNative.destroy();
            }
            mNativeAdsToFetch.clear();
            mFetchedNativeAds.clear();
        }
    }

    private void fetchNewAds() {
        // Create a new native ad and add it to a list of ads that are currently being fetched
        Log.i(TAG, "Trying to fetch new ad");
        if (mNativeAdsToFetch.size() < MAX_ADS_TO_FETCH) {
            /*
             For native ads, you can use either application context (Context#getApplicationContext())
             or activity context. To support pause & resume of native video ads, use application
             context.
             */
            FlurryAdNative flurryAdNative = new FlurryAdNative(
                    getActivity().getApplicationContext(), AD_SPACE_NAME);
            flurryAdNative.setListener(mNativeAdListener);
            // Enable test mode (test ads cannot be monetized)
            FlurryAdTargeting testModeTargeting = new FlurryAdTargeting();
            testModeTargeting.setEnableTestAds(true);
            flurryAdNative.setTargeting(testModeTargeting);
            flurryAdNative.fetchAd();
            mNativeAdsToFetch.add(flurryAdNative);
        }
    }

    private void loadArticles() {
        String[] titles = getResources().getStringArray(R.array.article_titles);
        String[] contents = getResources().getStringArray(R.array.article_contents);
        String[] authors = getResources().getStringArray(R.array.article_authors);

        Random random = new Random();

        for (int i = 0; i < ARTICLES_TO_LOAD; i++) {
            NewsArticle article = new NewsArticle();
            article.setArticleTitle(titles[random.nextInt(15)]);
            article.setArticleContent(contents[random.nextInt(15)]);
            // In a real application, you want to get higher quality images from a better source.
            article.setArticleImageResourceId(R.color.placeholder_image_background);
            article.setArticleCreator(authors[random.nextInt(15)]);

            mArticles.add(article);
        }

        ((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
    }

    private class NativeAdListener implements FlurryAdNativeListener {

        @Override
        public void onFetched(FlurryAdNative flurryAdNative) {
            Log.i(TAG, "onFetched callback called with Native ad object: " + flurryAdNative);

            if (mFetchedNativeAds.size() < MAX_ADS_TO_FETCH) {
                if (isAdUseable(flurryAdNative)) {
                    mFetchedNativeAds.add(flurryAdNative);
                    ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();

                }
                fetchNewAds();
            } else {
                // Destroy discarded ad
                flurryAdNative.destroy();
            }
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

            // If there was an error with fetching a native ad, try fetch a new one
            if (flurryAdErrorType == FlurryAdErrorType.FETCH
                    && mFetchAttempts <= MAX_FETCH_ATTEMPT) {
                if (mNativeAdsToFetch.contains(flurryAdNative)) {
                    flurryAdNative.destroy();
                    mNativeAdsToFetch.remove(flurryAdNative);
                }
                // Try again with a new native ad
                mFetchAttempts++;
                fetchNewAds();
            }
        }
    }

    class StreamArrayAdapter extends BaseAdapter {
        private final static int INTERVAL_BETWEEN_ADS = 3;

        @Override
        public int getCount() {
            return mArticles != null && mArticles.size() > 0 ?
                    mArticles.size() + Math.min(mFetchedNativeAds.size(), 2) : 0;
        }

        @Override
        public NewsArticle getItem(int position) {
            if (mArticles != null && mArticles.size() > position) {
                return mArticles.get(position);
            }

            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true; // Underlying data set is not changing anyway
        }

        @SuppressLint("CutPasteId")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            switch (getItemViewType(position)) {
                case VIEW_TYPE_AD:
                    Log.i(TAG, "Placing ad in ListView");

                    AdViewHolder adHolder;

                    if (convertView == null) {
                        convertView = getActivity().getLayoutInflater()
                                .inflate(R.layout.list_item_ad, parent, false);

                        adHolder = new AdViewHolder();
                        adHolder.adImage = (ImageView)convertView.findViewById(R.id.ad_image);
                        adHolder.adVideo = (ViewGroup)convertView.findViewById(R.id.ad_video);
                        adHolder.adTitle = (TextView)convertView.findViewById(R.id.ad_headline);
                        adHolder.adSummary = (TextView)convertView.findViewById(R.id.ad_description);
                        adHolder.publisher = (TextView)convertView.findViewById(R.id.ad_source);
                        adHolder.appRatingImg = (ImageView)convertView.findViewById(R.id.app_rating_image);
                        adHolder.sponsoredImage = (ImageView)convertView.findViewById(R.id.sponsored_image);

                        adHolder.adImage.setColorFilter(
                                new PorterDuffColorFilter(
                                        getResources().getColor(R.color.photo_tile_color_overlay),
                                        PorterDuff.Mode.SRC_ATOP));

                        convertView.setTag(adHolder);
                    } else {
                        adHolder = (AdViewHolder) convertView.getTag();
                        adHolder.adNative.removeTrackingView(); // Remove old tracking view
                    }
                    FlurryAdNative useableNativeAd = getUseableAd(position);

                    if (useableNativeAd != null) {
                        adHolder.adNative = useableNativeAd;
                        useableNativeAd.setTrackingView(convertView);

                        // Show an ad
                        useableNativeAd.getAsset(AD_ASSET_HEADLINE).loadAssetIntoView(adHolder.adTitle);
                        useableNativeAd.getAsset(AD_ASSET_SUMMARY).loadAssetIntoView(adHolder.adSummary);
                        useableNativeAd.getAsset(AD_ASSET_SOURCE).loadAssetIntoView(adHolder.publisher);
                        useableNativeAd.getAsset(AD_ASSET_SECURE_BRAND_LOGO).loadAssetIntoView(
                                adHolder.sponsoredImage);
                        if (useableNativeAd.isVideoAd()) {
                            useableNativeAd.getAsset(AD_ASSET_VIDEO_URL).loadAssetIntoView(adHolder.adVideo);
                            adHolder.adImage.setVisibility(View.GONE);
                            adHolder.adVideo.setVisibility(View.VISIBLE);

                            convertView.requestLayout();

                        } else if (useableNativeAd.getAsset(AD_ASSET_SECURE_HQ_IMAGE) != null) {
                            useableNativeAd.getAsset(AD_ASSET_SECURE_HQ_IMAGE).loadAssetIntoView(adHolder.adImage);
                            adHolder.adVideo.setVisibility(View.GONE);
                            adHolder.adImage.setVisibility(View.VISIBLE);
                        }
                        if (useableNativeAd.getAsset(AD_ASSET_SECURE_HQ_RATING_IMAGE) != null) {
                            adHolder.appRatingImg.findViewById(R.id.app_rating_image)
                                    .setVisibility(View.VISIBLE);
                            useableNativeAd.getAsset(AD_ASSET_SECURE_HQ_RATING_IMAGE)
                                    .loadAssetIntoView(adHolder.appRatingImg);
                        } else {
                            adHolder.appRatingImg.findViewById(R.id.app_rating_image).setVisibility(View.GONE);
                        }

                        useableNativeAd.setTrackingView(convertView);
                    } else {
                        // The ad is expired, hence unusable. Remove it from our collection
                        removeAd(position);
                    }
                    break;
                case VIEW_TYPE_NORMAL:
                    Log.i(TAG, "Placing data in ListView");

                    ViewHolder viewHolder;

                    if (convertView == null) {
                        convertView = getActivity().getLayoutInflater()
                                .inflate(R.layout.list_item_article, parent, false);

                        viewHolder = new ViewHolder();
                        viewHolder.articleTitleTextView = (TextView)convertView.findViewById(
                                R.id.article_title);
                        viewHolder.articleContentTextView = (TextView)convertView.findViewById(
                                R.id.article_content);
                        viewHolder.articleAuthorTextView = (TextView)convertView.findViewById(
                                R.id.article_author);
                        viewHolder.articleImageView = (ImageView)convertView.findViewById(
                                R.id.article_image);

                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder)convertView.getTag();
                    }

                    // Show an article
                    int originalArticlePosition = getOriginalContentPosition(position);
                    viewHolder.articleTitleTextView.setText(
                            getItem(originalArticlePosition).getArticleTitle());
                    viewHolder.articleContentTextView.setText(
                            getItem(originalArticlePosition).getArticleContent());
                    viewHolder.articleAuthorTextView.setText(
                            getItem(originalArticlePosition).getArticleCreator());
                    viewHolder.articleImageView.setImageResource(
                            getItem(originalArticlePosition).getArticleImageResourceId());
                    break;
            }


            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (getUseableAd(position) != null) {
                return VIEW_TYPE_AD;
            } else {
                return VIEW_TYPE_NORMAL;
            }
        }

        /**
         * Translates a given position to the original position in the underlying dataset,
         * assuming no ads were present.
         * @param position the position to translate
         * @return the original position from the underlying dataset
         */
        private int getOriginalContentPosition(int position) {
            int noOfFetchedAds = mFetchedNativeAds.size();
            // No of spaces for ads in the dataset, according to ad placement rules
            int adSpacesCount = position / (INTERVAL_BETWEEN_ADS + 1);
            return position - Math.min(adSpacesCount, noOfFetchedAds);
        }

        /**
         * Gets a useable ad or null if no useable ad with useable assets exist
         * @param position The position in the list that the ad should be placed at.
         * @return a useable {@link com.flurry.android.ads.FlurryAdNative} that contains
         * the bare minimum of assets.
         */
        private FlurryAdNative getUseableAd(int position) {
            if ((position + 1) % (INTERVAL_BETWEEN_ADS +1) == 0 &&
                    mFetchedNativeAds != null && mFetchedNativeAds.size() > 0) {

                int adIndex = getAdIndex(position);

                if (adIndex >= mFetchedNativeAds.size()) { return null; }

                FlurryAdNative nativeAd = mFetchedNativeAds.get(adIndex);

                if (isAdUseable(nativeAd)) {
                    return nativeAd;
                }
            }

            return null;
        }

        /**
         * Get an ad position that is unique to the given adapter position but is not more than the
         * number of already fetched ads.
         *
         * @param position The position in the list that the ad would be placed at.
         * @return the index position of the ad in the ad collection
         */
        private int getAdIndex(int position) {
            return ((position + 1) / (INTERVAL_BETWEEN_ADS + 1)) - 1;
        }

        /**
         * Removes the ad from the ad list. This would occur when the app detects that the ad is expired.
         *
         * It should ideally be called only after <code>getUseableAd(int)</code> returns null.
         *
         * @param position The position in the list that the ad would be placed at.
         */
        private void removeAd(int position) {
            int adIndex = getAdIndex(position);
            mFetchedNativeAds.remove(adIndex);

            notifyDataSetChanged();
        }
    }

    /**
     * Checks if an ad object is ready and useable.
     * @param adNative the {@link com.flurry.android.ads.FlurryAdNative} object to check
     * @return <code>true</code> if ad object is useable, false otherwise
     */
    private boolean isAdUseable(FlurryAdNative adNative) {
        return adNative.isReady() && !adNative.isExpired() &&
                adNative.getAsset(AD_ASSET_HEADLINE) != null &&
                adNative.getAsset(AD_ASSET_SUMMARY) != null &&
                adNative.getAsset(AD_ASSET_SECURE_HQ_IMAGE) != null;
    }

    private static class ViewHolder {
        public ImageView articleImageView;
        public TextView articleTitleTextView;
        public TextView articleContentTextView;
        public TextView articleAuthorTextView;
    }

    private static class AdViewHolder {
        ImageView adImage;
        ViewGroup adVideo;
        TextView adTitle;
        TextView adSummary;
        TextView publisher;
        ImageView sponsoredImage;
        ImageView appRatingImg;
        FlurryAdNative adNative;
    }
}
