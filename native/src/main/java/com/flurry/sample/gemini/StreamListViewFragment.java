package com.flurry.sample.gemini;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.sample.gemini.entities.NewsArticle;
import com.yahoo.mobile.library.streamads.FlurryAdListAdapter;
import com.yahoo.mobile.library.streamads.NativeAdAdapter;
import com.yahoo.mobile.library.streamads.NativeAdViewBinder;
import com.yahoo.mobile.library.streamads.positioning.LinearIntervalAdPositioner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This sample uses the library at https://github.com/flurry/StreamAds-Android
 * for integrating ads interspersed with other content in a stream adapter.
 *
 * For best practices in using ads in a stream, please review the code from the library.
 */
public class StreamListViewFragment extends BaseAdFragment {
    static final String TAG = StreamListViewFragment.class.getSimpleName();

    private ListView mListView;
    private List<NewsArticle> mArticles;
    private boolean mExpandableAdsMode;
    private final static int ARTICLES_TO_LOAD = 20;

    protected NativeAdListener mNativeAdListener = new NativeAdListener();

    public static final StreamListViewFragment newInstance(boolean expandableAdsMode) {
        StreamListViewFragment newInstance = new StreamListViewFragment();
        newInstance.mExpandableAdsMode = expandableAdsMode;
        return newInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_stream, container, false);

        mListView = (ListView)rootView.findViewById(android.R.id.list);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mArticles = new ArrayList<>(ARTICLES_TO_LOAD);

        loadArticles();

        BaseAdapter dataAdapter = new ArticleAdapter(getActivity(), R.layout.list_item_article, mArticles);

        NativeAdViewBinder.ViewBinderBuilder viewBinderBuilder = new NativeAdViewBinder.ViewBinderBuilder();

        viewBinderBuilder = viewBinderBuilder.setAdLayoutId(R.layout.list_item_ad)
                .setHeadlineTextId(R.id.ad_headline)
                .setDescriptionTextId(R.id.ad_description)
                .setSourceTextId(R.id.ad_source)
                .setBrandingLogoImageId(R.id.sponsored_image)
                .setAppStarRatingImageId(R.id.app_rating_image)
                .setAdImageId(R.id.ad_image);

        if (mExpandableAdsMode) {
            viewBinderBuilder = viewBinderBuilder
                    .setCallToActionViewId(R.id.ad_cta_btn)
                    .setAdCollapseViewId(R.id.ad_collapse_btn);
        }

        NativeAdViewBinder viewBinder = viewBinderBuilder.build();

        FlurryAdListAdapter.Builder flurryAdAdapterBuilder = FlurryAdListAdapter
                .from(getActivity(), dataAdapter, viewBinder, AD_SPACE_NAME)
                .setAdPositioner(new LinearIntervalAdPositioner(3, 4))
                .setFlurryAdNativeListener(mNativeAdListener)
                .setAutoDestroy(true);

        if (mExpandableAdsMode) {
            flurryAdAdapterBuilder = flurryAdAdapterBuilder
                    .setExpandableAdMode(NativeAdAdapter.EXPANDABLE_AD_MODE_COLLAPSED);
        }

        FlurryAdListAdapter adListAdapter = flurryAdAdapterBuilder.build();

        mListView.setAdapter(adListAdapter);

        adListAdapter.refreshAds();
    }

    private void loadArticles() {
        String[] titles = getResources().getStringArray(R.array.article_titles);
        String content = getResources().getString(R.string.sample_long_text);
        String[] authors = getResources().getStringArray(R.array.article_authors);

        Random random = new Random();

        for (int i = 0; i < ARTICLES_TO_LOAD; i++) {
            NewsArticle article = new NewsArticle();
            article.setArticleTitle(titles[random.nextInt(15)]);
            article.setArticleContent(content);
            article.setArticleImageResourceId(R.color.placeholder_image_background);
            article.setArticleCreator(authors[random.nextInt(15)]);

            mArticles.add(article);
        }
    }

    private static class ArticleAdapter extends ArrayAdapter<NewsArticle> {

        public ArticleAdapter(Context context, int resource, List<NewsArticle> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
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
            viewHolder.articleTitleTextView.setText(
                    getItem(position).getArticleTitle());
            viewHolder.articleContentTextView.setText(
                    getItem(position).getArticleContent());
            viewHolder.articleAuthorTextView.setText(
                    getItem(position).getArticleCreator());
            viewHolder.articleImageView.setImageResource(
                    getItem(position).getArticleImageResourceId());

            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView articleImageView;
        public TextView articleTitleTextView;
        public TextView articleContentTextView;
        public TextView articleAuthorTextView;
    }
}
