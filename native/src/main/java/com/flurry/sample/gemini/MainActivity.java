package com.flurry.sample.gemini;

import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener, NativeAdChooserFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_pane);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        displayHomeAsUpIfNeeded();

        if (savedInstanceState == null) {
            FragmentManager.enableDebugLogging(true);
            getSupportFragmentManager().addOnBackStackChangedListener(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, NativeAdChooserFragment.newInstance(), NativeAdChooserFragment.TAG)
                    .commit();
        }

        // http response cache
        File httpCacheDir = new File(getCacheDir(), "http");
        long httpCacheSize = 100 * 1024 * 1024; // 100 MiB

        try {
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(MainActivity.class.getSimpleName(), "HTTP response cache installation failed:" + e);
        }
    }

    @Override
    public void onBackStackChanged() {
        displayHomeAsUpIfNeeded();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onAdDisplaySelected(int index) {
        boolean expandableAdsMode = true;

        switch (index) {
            case 0:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, SingleAdFragment.newInstance(), SingleAdFragment.TAG)
                        .addToBackStack(SingleAdFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case 1:
                expandableAdsMode = false;
                // FALL THROUGH
            case 2:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, StreamListViewFragment.newInstance(expandableAdsMode),
                                StreamListViewFragment.TAG)
                        .addToBackStack(StreamListViewFragment.TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
        }
    }

    private void displayHomeAsUpIfNeeded() {
        boolean canGoUp = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(canGoUp);
        }
    }
}
