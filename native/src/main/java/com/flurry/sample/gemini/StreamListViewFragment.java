package com.flurry.sample.gemini;

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

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.sample.gemini.entities.NewsArticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamListViewFragment extends Fragment {
    public static final String TAG = StreamListViewFragment.class.getSimpleName();

    private ListView mListView;
    private List<NewsArticle> mArticles;
    private final static int ARTICLES_TO_LOAD = 10;
    private final static int MAX_ADS_TO_FETCH = 5;
    private final static int MAX_FETCH_ATTEMPT = 10;

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_AD = 1;

    private int mFetchAttempts;
    /**
     * List containing ads that are currently being fetched
     */
    private List<FlurryAdNative> mNativeAdsToFetch;
    /**
     * List containing ads that have been successfully fetched
     */
    private List<FlurryAdNative> mFetchedNativeAds;
    private NativeAdListener mNativeAdListener = new NativeAdListener();

    private static final String AD_SPACE_NAME = "YOUR_FLURRY_AD_SPACE_NAME";

    private final String AD_ASSET_SUMMARY = "summary";
    private final String AD_ASSET_HEADLINE = "headline";
    private final String AD_ASSET_SOURCE = "source";
    private final String AD_ASSET_SECURE_HQ_IMAGE = "secHqImage";
    private final String AD_ASSET_SECURE_BRAND_LOGO = "secBrandingLogo";
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
        fetchNewAds();
    }

    private void fetchNewAds() {
        // Create a new native ad and add it to a list of ads that are currently being fetched
        Log.i(TAG, "Trying to fetch new ad");
        if (mNativeAdsToFetch.size() < MAX_ADS_TO_FETCH) {
            FlurryAdNative flurryAdNative = new FlurryAdNative(getActivity(), AD_SPACE_NAME);
            flurryAdNative.setListener(mNativeAdListener);
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            switch (getItemViewType(position)) {
                case VIEW_TYPE_AD:
                    Log.i(TAG, "Placing ad in ListView");

                    AdViewHolder adHolder;

                    if (convertView == null) {
                        convertView = getActivity().getLayoutInflater()
                                .inflate(R.layout.list_item_article, parent, false);

                        adHolder = new AdViewHolder();
                        adHolder.adImage = (ImageView)convertView.findViewById(R.id.article_image);
                        adHolder.adTitle = (TextView)convertView.findViewById(R.id.article_title);
                        adHolder.adSummary = (TextView)convertView.findViewById(R.id.article_content);
                        adHolder.publisher = (TextView)convertView.findViewById(R.id.article_author);
                        adHolder.sponsoredImage = (ImageView)convertView.findViewById(R.id.sponsored_image);

                        // Clear the viewHolder content
                        adHolder.sponsoredImage.setVisibility(View.VISIBLE);
                        adHolder.adImage.setImageResource(R.color.loading_image_background);

                        convertView.setTag(adHolder);
                    } else {
                        adHolder = (AdViewHolder) convertView.getTag();
                        adHolder.adNative.removeTrackingView(); // Remove old tracking view
                    }
                    FlurryAdNative useableNativeAd = getUseableAd(position);

                    adHolder.adNative = useableNativeAd;
                    useableNativeAd.setTrackingView(convertView);

                    // Show an ad
                    useableNativeAd.getAsset(AD_ASSET_HEADLINE).loadAssetIntoView(adHolder.adTitle);
                    useableNativeAd.getAsset(AD_ASSET_SUMMARY).loadAssetIntoView(adHolder.adSummary);
                    useableNativeAd.getAsset(AD_ASSET_SOURCE).loadAssetIntoView(adHolder.publisher);
                    useableNativeAd.getAsset(AD_ASSET_SECURE_HQ_IMAGE).loadAssetIntoView(adHolder.adImage);
                    useableNativeAd.getAsset(AD_ASSET_SECURE_BRAND_LOGO).loadAssetIntoView(
                            adHolder.sponsoredImage);

                    useableNativeAd.setTrackingView(convertView);
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

                        viewHolder.articleImageView.setColorFilter(
                                new PorterDuffColorFilter(
                                        getResources().getColor(R.color.photo_tile_color_overlay),
                                        PorterDuff.Mode.SRC_ATOP));

                        // Clear the viewHolder content
                        convertView.findViewById(R.id.sponsored_badge).setVisibility(View.INVISIBLE);
                        convertView.findViewById(R.id.sponsored_image).setVisibility(View.GONE);

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
         * @return
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
                /*
                 Get an ad position that is unique to the given adapter position but is not more
                 than the number of already fetched ads.
                 */
                int adIndex = ((position + 1) / (INTERVAL_BETWEEN_ADS + 1)) - 1;

                if (adIndex >= mFetchedNativeAds.size()) { return null; }

                FlurryAdNative nativeAd = mFetchedNativeAds.get(adIndex);

                if (isAdUseable(nativeAd)) {
                    return nativeAd;
                }
            }

            return null;
        }
    }

    /**
     * Checks if an ad object is ready ad useable.
     * @param adNative the {@link com.flurry.android.ads.FlurryAdNative} object to check
     * @return <code>true</code> if ad object is useable, false otherwise
     */
    private boolean isAdUseable(FlurryAdNative adNative) {
        return adNative.isReady() &&
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
        TextView adTitle;
        TextView adSummary;
        TextView publisher;
        ImageView sponsoredImage;
        FlurryAdNative adNative;
    }
}
